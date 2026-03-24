package ru.teamscore.events.web.screens.event;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import ru.teamscore.events.entity.Event;
import ru.teamscore.events.entity.EventField;
import ru.teamscore.events.entity.EventFieldValue;
import ru.teamscore.events.entity.EventType;
import ru.teamscore.events.entity.enums.EventFieldType;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UiController("events_Event.edit")
@UiDescriptor("event-edit.xml")
@EditedEntityContainer("eventDc")
@LoadDataBeforeShow
public class EventEdit extends StandardEditor<Event> {
    @Inject
    private VBoxLayout dynamicFieldsContainer;

    @Inject
    private GroupBoxLayout dynamicFieldsBox;

    @Inject
    private UiComponents uiComponents;

    @Inject
    private Notifications notifications;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private DataContext dataContext;

    /**
     * Карта компонентов динамических полей (ключ - fieldId, значение - UI компонент)
     */
    private final Map<String, Component> dynamicFieldComponents = new HashMap<>();

    /**
     * Карта метаданных полей (ключ - fieldId, значение - EventField)
     */
    private final Map<String, EventField> fieldsMetadata = new HashMap<>();

    /**
     * Построение динамических полей для выбранного типа события
     */
    private void buildDynamicFields(EventType eventType) {
        // Очищаем контейнер и компоненты
        dynamicFieldsContainer.removeAll();
        dynamicFieldComponents.clear();
        fieldsMetadata.clear();
        // Скрываем блок динамических полей, если тип события не выбран
        if (eventType == null) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        // Загружаем поля для выбранного типа события напрямую через DataManager
        List<EventField> fields = dataManager.load(EventField.class)
                .query("select f from events_EventField f where f.eventType.id = :typeId order by f.name")
                .parameter("typeId", eventType.getId())
                .view("_local")
                .list();
        // Если нет полей, скрываем блок динамических полей
        if (fields.isEmpty()) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        // Показываем блок динамических полей
        dynamicFieldsBox.setVisible(true);
        // Получаем существующие значения
        Map<String, EventFieldValue> existingValues = getExistingFieldValuesMap();
        // Создаем UI-компоненты для каждого поля
        for (EventField field : fields) {
            fieldsMetadata.put(field.getFieldId(), field);
            Component component = createFieldComponent(field, existingValues.get(field.getFieldId()));
            if (component != null) {
                dynamicFieldsContainer.add(component);
                dynamicFieldComponents.put(field.getFieldId(), component);
            }
        }
    }

    /**
     * Получить существующие значения полей для события в виде Map
     */
    private Map<String, EventFieldValue> getExistingFieldValuesMap() {
        Map<String, EventFieldValue> existingValues = new HashMap<>();
        Event event = getEditedEntity();
        if (event.getId() != null && event.getFieldValues() != null) {
            // Используем уже загруженные значения из события
            for (EventFieldValue fieldValue : event.getFieldValues()) {
                EventField field = fieldValue.getEventField();
                if (field != null && field.getFieldId() != null) {
                    existingValues.put(field.getFieldId(), fieldValue);
                }
            }
        }
        return existingValues;
    }


    /**
     * Сохранить значения динамических полей
     */
    private void saveDynamicFieldValues() {
        EventType eventType = getEditedEntity().getType();
        if (eventType == null) {
            return;
        }
        Event event = getEditedEntity();
        Map<String, EventFieldValue> existingValues = getExistingFieldValuesMap();
        for (Map.Entry<String, Component> entry : dynamicFieldComponents.entrySet()) {
            String fieldId = entry.getKey();
            Component component = entry.getValue();
            EventField field = fieldsMetadata.get(fieldId);
            if (field == null) {
                continue;
            }
            Object value = extractValueFromComponent(component);
            if (value != null) {
                createOrUpdateFieldValue(event, field, value, existingValues.get(fieldId));
            }
        }
    }

    /**
     * Создать или обновить значение дополнительного поля
     */
    private void createOrUpdateFieldValue(Event event, EventField field, Object value, EventFieldValue existingValue) {
        EventFieldValue fieldValue;
        if (existingValue == null) {
            // Создаем новую сущность через metadata
            fieldValue = metadata.create(EventFieldValue.class);
            fieldValue.setEvent(event);
            EventField managedField = dataManager.load(EventField.class)
                    .id(field.getId())
                    .one();
            fieldValue.setEventField(managedField);
            if (event.getFieldValues() == null) {
                event.setFieldValues(new ArrayList<>());
            }
            event.getFieldValues().add(fieldValue);
        } else {
            // Используем существующее значение, которое уже в DataContext
            fieldValue = existingValue;
        }
        setFieldValueByType(fieldValue, field.getType(), value);
    }

    /**
     * Установить значение поля в зависимости от его типа
     */
    private void setFieldValueByType(EventFieldValue fieldValue, EventFieldType fieldType, Object value) {
        if (value == null) {
            return;
        }
        switch (fieldType) {
            case STRING:
                fieldValue.setStringValue((String) value);
                break;
            case TEXT:
                fieldValue.setTextValue((String) value);
                break;
            case DATE:
                fieldValue.setDateValue((LocalDate) value);
                break;
            case DATETIME:
                fieldValue.setDateTimeValue((LocalDateTime) value);
                break;
            case BINARY:
                fieldValue.setFileValue((com.haulmont.cuba.core.entity.FileDescriptor) value);
                break;
        }
    }

    /**
     * Извлечь значение из UI-компонента
     */
    private Object extractValueFromComponent(Component component) {
        if (component instanceof TextField) {
            return ((TextField<?>) component).getValue();
        } else if (component instanceof TextArea) {
            return ((TextArea<?>) component).getValue();
        } else if (component instanceof DateField) {
            return ((DateField<?>) component).getValue();
        } else if (component instanceof FileUploadField) {
            return ((FileUploadField) component).getValue();
        }
        return null;
    }

    /**
     * Создание UI-компонента для дополнительного поля
     */
    private Component createFieldComponent(EventField field, EventFieldValue existingValue) {
        EventFieldType fieldType = field.getType();
        switch (fieldType) {
            case STRING:
                TextField<String> textField = uiComponents.create(TextField.class);
                textField.setCaption(field.getName());
                textField.setWidth("100%");
                if (existingValue != null) {
                    textField.setValue(existingValue.getStringValue());
                }
                return textField;
            case TEXT:
                TextArea<String> textArea = uiComponents.create(TextArea.class);
                textArea.setCaption(field.getName());
                textArea.setWidth("100%");
                textArea.setRows(3);
                if (existingValue != null) {
                    textArea.setValue(existingValue.getTextValue());
                }
                return textArea;
            case DATE:
                DateField<java.time.LocalDate> dateField = uiComponents.create(DateField.class);
                dateField.setCaption(field.getName());
                dateField.setDateFormat("dd.MM.yyyy");
                if (existingValue != null) {
                    dateField.setValue(existingValue.getDateValue());
                }
                return dateField;
            case DATETIME:
                DateField<java.time.LocalDateTime> dateTimeField = uiComponents.create(DateField.class);
                dateTimeField.setCaption(field.getName());
                dateTimeField.setDateFormat("dd.MM.yyyy HH:mm");
                dateTimeField.setResolution(DateField.Resolution.MIN);
                if (existingValue != null) {
                    dateTimeField.setValue(existingValue.getDateTimeValue());
                }
                return dateTimeField;
            case BINARY:
                FileUploadField fileField = uiComponents.create(FileUploadField.class);
                fileField.setCaption(field.getName());
                if (existingValue != null && existingValue.getFileValue() != null) {
                    fileField.setValue(existingValue.getFileValue());
                }
                return fileField;
            default:
                return null;
        }
    }

    /**
     * Валидация перед сохранением события (дата начала не позже даты окончания
     */
    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        LocalDateTime startDateTime = getEditedEntity().getStartDateTime();
        LocalDateTime endDateTime = getEditedEntity().getEndDateTime();
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption("Ошибка валидации")
                    .withDescription("Дата начала не может быть позже даты окончания события")
                    .show();
            event.preventCommit();
            return;
        }
        saveDynamicFieldValues();
    }

    /**
     * Обновление динамических полей при изменении типа события
     */
    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<EventType> event) {
        buildDynamicFields(event.getValue());
    }

    /**
     * Обработчик после отображения экрана. Строит динамические поля для текущего типа события
     */
    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        EventType eventType = getEditedEntity().getType();
        if (eventType != null) {
            buildDynamicFields(eventType);
        }
    }
}
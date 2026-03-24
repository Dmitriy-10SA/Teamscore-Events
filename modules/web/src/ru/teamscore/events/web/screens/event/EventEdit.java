package ru.teamscore.events.web.screens.event;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import ru.teamscore.events.entity.Event;
import ru.teamscore.events.entity.EventField;
import ru.teamscore.events.entity.EventFieldValue;
import ru.teamscore.events.entity.EventType;
import ru.teamscore.events.entity.enums.EventFieldType;
import ru.teamscore.events.entity.enums.EventStatus;

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
    private Messages messages;

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
        dynamicFieldsContainer.removeAll();
        dynamicFieldComponents.clear();
        fieldsMetadata.clear();
        if (eventType == null) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        List<EventField> fields = dataManager.load(EventField.class)
                .query("select f from events_EventField f where f.eventType.id = :typeId order by f.name")
                .parameter("typeId", eventType.getId())
                .view("_local")
                .list();
        if (fields.isEmpty()) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        dynamicFieldsBox.setVisible(true);
        Map<String, EventFieldValue> existingValues = getExistingFieldValuesMap();
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
        Event event = getEditedEntity();
        EventType eventType = event.getType();
        Map<String, EventFieldValue> existingValues = getExistingFieldValuesMap();
        List<EventFieldValue> newValues = new ArrayList<>();
        if (eventType == null) {
            event.setFieldValues(newValues);
            return;
        }
        for (Map.Entry<String, Component> entry : dynamicFieldComponents.entrySet()) {
            String fieldId = entry.getKey();
            Component component = entry.getValue();
            EventField field = fieldsMetadata.get(fieldId);
            if (field == null) {
                continue;
            }
            Object value = extractValueFromComponent(component);
            if (value instanceof String && ((String) value).trim().isEmpty()) {
                value = null;
            }
            if (value == null) {
                continue;
            }
            EventFieldValue fieldValue = existingValues.get(fieldId);
            boolean isValidExistingValue = fieldValue != null
                    && fieldValue.getEventField() != null
                    && fieldValue.getEventField().getId().equals(field.getId());
            if (!isValidExistingValue) {
                fieldValue = metadata.create(EventFieldValue.class);
                fieldValue.setEvent(event);
                fieldValue.setEventField(field);
            }
            clearFieldValue(fieldValue);
            setFieldValueByType(fieldValue, field.getType(), value);
            newValues.add(fieldValue);
        }
        event.setFieldValues(newValues);
    }

    /**
     * Очистить значения дополнительных полей
     */
    private void clearFieldValue(EventFieldValue fieldValue) {
        fieldValue.setStringValue(null);
        fieldValue.setTextValue(null);
        fieldValue.setDateValue(null);
        fieldValue.setDateTimeValue(null);
        fieldValue.setFileValue(null);
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
                fieldValue.setFileValue((FileDescriptor) value);
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
        } else if (component instanceof VBoxLayout) {
            VBoxLayout container = (VBoxLayout) component;
            for (Component child : container.getComponents()) {
                if (child instanceof FileUploadField) {
                    return ((FileUploadField) child).getValue();
                }
            }
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
                return createTextFieldComponent(field, existingValue);
            case TEXT:
                return createTextAreaComponent(field, existingValue);
            case DATE:
                return createDateFieldComponent(field, existingValue);
            case DATETIME:
                return createDateTimeFieldComponent(field, existingValue);
            case BINARY:
                return createBinaryFieldComponent(field, existingValue);
            default:
                return null;
        }
    }

    /**
     * Создание UI-компонента для STRING поля
     */
    @SuppressWarnings("unchecked")
    private TextField<String> createTextFieldComponent(EventField field, EventFieldValue existingValue) {
        TextField<String> textField = uiComponents.create(TextField.class);
        textField.setCaption(field.getName());
        textField.setWidth("100%");
        if (existingValue != null) {
            textField.setValue(existingValue.getStringValue());
        }
        return textField;
    }

    /**
     * Создание UI-компонента для TEXT поля
     */
    @SuppressWarnings("unchecked")
    private TextArea<String> createTextAreaComponent(EventField field, EventFieldValue existingValue) {
        TextArea<String> textArea = uiComponents.create(TextArea.class);
        textArea.setCaption(field.getName());
        textArea.setWidth("100%");
        textArea.setRows(3);
        if (existingValue != null) {
            textArea.setValue(existingValue.getTextValue());
        }
        return textArea;
    }

    /**
     * Создание UI-компонента для DATE поля
     */
    @SuppressWarnings("unchecked")
    private DateField<LocalDate> createDateFieldComponent(EventField field, EventFieldValue existingValue) {
        DateField<java.time.LocalDate> dateField = uiComponents.create(DateField.class);
        dateField.setCaption(field.getName());
        dateField.setDateFormat("dd.MM.yyyy");
        if (existingValue != null) {
            dateField.setValue(existingValue.getDateValue());
        }
        return dateField;
    }

    /**
     * Создание UI-компонента для DATETIME поля
     */
    @SuppressWarnings("unchecked")
    private DateField<LocalDateTime> createDateTimeFieldComponent(EventField field, EventFieldValue existingValue) {
        DateField<java.time.LocalDateTime> dateTimeField = uiComponents.create(DateField.class);
        dateTimeField.setCaption(field.getName());
        dateTimeField.setDateFormat("dd.MM.yyyy HH:mm");
        dateTimeField.setResolution(DateField.Resolution.MIN);
        if (existingValue != null) {
            dateTimeField.setValue(existingValue.getDateTimeValue());
        }
        return dateTimeField;
    }

    /**
     * Создание UI-компонента для BINARY поля
     */
    private Component createBinaryFieldComponent(EventField field, EventFieldValue existingValue) {
        VBoxLayout fileContainer = uiComponents.create(VBoxLayout.class);
        fileContainer.setCaption(field.getName());
        fileContainer.setSpacing(true);
        fileContainer.setWidth("100%");
        FileUploadField fileField = uiComponents.create(FileUploadField.class);
        fileField.setWidth("100%");
        fileField.setShowFileName(true);
        fileField.setShowClearButton(true);
        fileField.setUploadButtonCaption(messages.getMessage(getClass(), "uploadFileButton"));
        fileField.setClearButtonCaption(messages.getMessage(getClass(), "clearFileButton"));
        fileField.setMode(FileUploadField.FileStoragePutMode.IMMEDIATE);
        if (existingValue != null && existingValue.getFileValue() != null) {
            fileField.setValue(existingValue.getFileValue());
        }
        fileContainer.add(fileField);
        return fileContainer;
    }

    /**
     * Отображение ошибки валидации
     */
    private void notifyValidationError(String descriptionKey) {
        notifications.create(Notifications.NotificationType.WARNING)
                .withCaption(messages.getMessage(getClass(), "validationError"))
                .withDescription(messages.getMessage(getClass(), descriptionKey))
                .show();
    }

    /**
     * Валидация и сохранение динамических полей перед сохранением события
     * - дата начала не позже даты окончания
     * - событие не может быть в статусе "ожидание", если дата начала события уже наступила
     */
    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        LocalDateTime startDateTime = getEditedEntity().getStartDateTime();
        LocalDateTime endDateTime = getEditedEntity().getEndDateTime();
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            notifyValidationError("validationErrorDateTime");
            event.preventCommit();
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        boolean isExpected = getEditedEntity().getStatus() == EventStatus.EXPECTED;
        if (startDateTime != null && !startDateTime.isAfter(now) && isExpected) {
            notifyValidationError("validationErrorWaitingStatusPastStartDate");
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
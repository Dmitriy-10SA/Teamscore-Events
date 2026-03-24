package ru.teamscore.events.web.screens.event;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import ru.teamscore.events.entity.Event;
import ru.teamscore.events.entity.EventField;
import ru.teamscore.events.entity.EventFieldValue;
import ru.teamscore.events.entity.EventType;
import ru.teamscore.events.entity.enums.EventFieldType;
import ru.teamscore.events.service.EventFieldService;

import javax.inject.Inject;
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

    private final Map<String, Component> dynamicFieldComponents = new HashMap<>();

    private EventFieldService getEventFieldService() {
        return AppBeans.get(EventFieldService.class);
    }

    /**
     * Построение динамических полей для выбранного типа события
     */
    private void buildDynamicFields(EventType eventType) {
        // Очищаем контейнер и компоненты
        dynamicFieldsContainer.removeAll();
        dynamicFieldComponents.clear();
        // Скрываем блок динамических полей, если тип события не выбран
        if (eventType == null) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        // Загружаем поля для выбранного типа события через сервис
        List<EventField> fields = getEventFieldService().getEventFieldsByType(eventType);
        // Если нет полей, скрываем блок динамических полей
        if (fields.isEmpty()) {
            dynamicFieldsBox.setVisible(false);
            return;
        }
        // Показываем блок динамических полей
        dynamicFieldsBox.setVisible(true);
        // Получаем существующие значения через сервис
        Map<String, EventFieldValue> existingValues = getEventFieldService().getExistingFieldValuesMap(getEditedEntity());
        // Создаем UI-компоненты для каждого поля
        for (EventField field : fields) {
            Component component = createFieldComponent(field, existingValues.get(field.getFieldId()));
            if (component != null) {
                dynamicFieldsContainer.add(component);
                dynamicFieldComponents.put(field.getFieldId(), component);
            }
        }
    }


    private void saveDynamicFieldValues() {
        EventType eventType = getEditedEntity().getType();
        if (eventType == null) {
            return;
        }

        // Собираем значения из UI-компонентов
        Map<String, Object> fieldValues = new HashMap<>();

        for (Map.Entry<String, Component> entry : dynamicFieldComponents.entrySet()) {
            String fieldId = entry.getKey();
            Component component = entry.getValue();

            Object value = extractValueFromComponent(component);
            if (value != null) {
                fieldValues.put(fieldId, value);
            }
        }

        // Сохраняем значения через сервис
        getEventFieldService().saveEventFieldValues(getEditedEntity(), fieldValues);
    }

    /**
     * Извлечение значения из UI-компонента
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

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        // Сохраняем значения динамических полей
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
     * При открытии экрана - построение динамических полей для текущего типа
     */
    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        EventType eventType = getEditedEntity().getType();
        if (eventType != null) {
            buildDynamicFields(eventType);
        }
    }
}

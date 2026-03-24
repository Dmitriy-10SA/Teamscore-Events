package ru.teamscore.events.service;

import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;
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

/**
 * Реализация сервиса для работы с динамическими полями событий
 */
@Service(EventFieldService.NAME)
public class EventFieldServiceBean implements EventFieldService {
    @Inject
    private DataManager dataManager;

    @Override
    public List<EventField> getEventFieldsByType(EventType eventType) {
        if (eventType == null) {
            return new ArrayList<>();
        }
        return dataManager.load(EventField.class)
                .query("select f from events_EventField f where f.eventType.id = :typeId order by f.name")
                .parameter("typeId", eventType.getId())
                .view("_local")
                .list();
    }

    @Override
    public Map<String, EventFieldValue> getExistingFieldValuesMap(Event event) {
        Map<String, EventFieldValue> existingValues = new HashMap<>();
        if (event.getFieldValues() != null) {
            for (EventFieldValue fieldValue : event.getFieldValues()) {
                existingValues.put(fieldValue.getEventField().getFieldId(), fieldValue);
            }
        }
        return existingValues;
    }

    @Override
    public void saveEventFieldValues(Event event, Map<String, Object> fieldValues) {
        EventType eventType = event.getType();
        if (eventType == null || fieldValues == null || fieldValues.isEmpty()) {
            return;
        }
        // Загружаем поля для типа события
        List<EventField> fields = getEventFieldsByType(eventType);
        // Получаем существующие значения
        Map<String, EventFieldValue> existingValues = getExistingFieldValuesMap(event);
        // Обрабатываем каждое поле
        for (EventField field : fields) {
            Object value = fieldValues.get(field.getFieldId());
            if (value != null) {
                createOrUpdateFieldValue(event, field, value, existingValues.get(field.getFieldId()));
            }
        }
    }

    /**
     * Внутренний метод для создания или обновления значения поля
     */
    private void createOrUpdateFieldValue(Event event, EventField field, Object value, EventFieldValue existingValue) {
        EventFieldValue fieldValue = existingValue;
        // Создаем новое значение, если его нет
        if (fieldValue == null) {
            fieldValue = dataManager.create(EventFieldValue.class);
            fieldValue.setEvent(event);
            fieldValue.setEventField(field);

            if (event.getFieldValues() == null) {
                event.setFieldValues(new ArrayList<>());
            }
            event.getFieldValues().add(fieldValue);
        }
        // Устанавливаем значение в зависимости от типа поля
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
}
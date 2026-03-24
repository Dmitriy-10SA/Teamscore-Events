package ru.teamscore.events.service;

import ru.teamscore.events.entity.Event;
import ru.teamscore.events.entity.EventField;
import ru.teamscore.events.entity.EventFieldValue;
import ru.teamscore.events.entity.EventType;

import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с динамическими полями событий
 */
public interface EventFieldService {
    String NAME = "events_EventFieldService";

    /**
     * Получить список полей для указанного типа события
     */
    List<EventField> getEventFieldsByType(EventType eventType);

    /**
     * Получить существующие значения полей для события в виде Map
     */
    Map<String, EventFieldValue> getExistingFieldValuesMap(Event event);

    /**
     * Сохранить значения динамических полей для события
     */
    void saveEventFieldValues(Event event, Map<String, Object> fieldValues);
}
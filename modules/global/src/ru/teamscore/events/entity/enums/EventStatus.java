package ru.teamscore.events.entity.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * Перечисление статусов события
 */
public enum EventStatus implements EnumClass<String> {
    EXPECTED("Ожидается"),
    CONDUCTED("Проведено"),
    CANCELLED("Отменено");

    private String id;

    EventStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EventStatus fromId(String id) {
        for (EventStatus at : EventStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
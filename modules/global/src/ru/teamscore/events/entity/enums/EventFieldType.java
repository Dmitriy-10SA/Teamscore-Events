package ru.teamscore.events.entity.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * Перечисление типов дополнительных полей события
 */
public enum EventFieldType implements EnumClass<String> {
    STRING("Строка"),
    TEXT("Многострочный"),
    BINARY("Бинарный"),
    DATE("Дата"),
    DATETIME("Дата и время");

    private String id;

    EventFieldType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EventFieldType fromId(String id) {
        for (EventFieldType at : EventFieldType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
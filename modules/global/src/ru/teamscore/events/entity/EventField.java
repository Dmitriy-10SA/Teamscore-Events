package ru.teamscore.events.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import ru.teamscore.events.entity.enums.EventFieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Сущность "Дополнительное поле события"
 */
@NamePattern("%s (%s)|name,fieldId")
@Table(name = "EVENTS_EVENT_FIELD")
@Entity(name = "events_EventField")
public class EventField extends StandardEntity {
    private static final long serialVersionUID = 3426854476284751234L;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])[a-z_-]+$", message = "{msg://ru.teamscore.events.entity/EventField.fieldId.validation}")
    @Size(max = 40)
    @Column(name = "ID_", nullable = false, unique = true, length = 40)
    protected String fieldId;

    @NotNull
    @Pattern(
            regexp = "^(?=.*[a-zA-Zа-яА-ЯёЁ0-9])[a-zA-Zа-яА-ЯёЁ0-9\\\\-_, ]+$",
            message = "{msg://ru.teamscore.events.entity/EventField.name.validation}")
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_TYPE_ID")
    protected EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventFieldType getType() {
        return type == null ? null : EventFieldType.fromId(type);
    }

    public void setType(EventFieldType type) {
        this.type = type == null ? null : type.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}
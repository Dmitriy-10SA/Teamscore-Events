package ru.teamscore.events.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * Сущность "Тип события"
 */
@NamePattern("%s|name")
@Table(name = "EVENTS_EVENT_TYPE")
@Entity(name = "events_EventType")
public class EventType extends StandardEntity {
    private static final long serialVersionUID = 8426854476284751234L;

    @NotNull
    @Pattern(
            regexp = "^(?=.*[a-zA-Zа-яА-ЯёЁ0-9])[a-zA-Zа-яА-ЯёЁ0-9\\\\-_ ,]+$",
            message = "{msg://ru.teamscore.events.entity/EventType.name.validation}"
    )
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @OneToMany(mappedBy = "eventType")
    protected List<EventField> fields;

    public List<EventField> getFields() {
        return fields;
    }

    public void setFields(List<EventField> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
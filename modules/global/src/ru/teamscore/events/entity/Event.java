package ru.teamscore.events.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.security.entity.User;
import org.springframework.format.annotation.DateTimeFormat;
import ru.teamscore.events.entity.enums.EventStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность "Событие"
 */
@NamePattern("%s (%s)|name,startDateTime")
@Table(name = "EVENTS_EVENT")
@Entity(name = "events_Event")
public class Event extends StandardEntity {
    private static final long serialVersionUID = 1426854476284751234L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "START_DATE_TIME", nullable = false)
    protected LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "END_DATE_TIME", nullable = false)
    protected LocalDateTime endDateTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESPONSIBLE_USER_ID")
    protected User responsibleUser;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected EventType type;

    @OneToMany(mappedBy = "event")
    protected List<EventFieldValue> fieldValues;

    public List<EventFieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<EventFieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventStatus getStatus() {
        return status == null ? null : EventStatus.fromId(status);
    }

    public void setStatus(EventStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
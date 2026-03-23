package ru.teamscore.events.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сущность "Значение дополнительного поля события"
 */
@NamePattern("%s|eventField")
@Table(name = "EVENTS_EVENT_FIELD_VALUE")
@Entity(name = "events_EventFieldValue")
public class EventFieldValue extends StandardEntity {
    private static final long serialVersionUID = 5426854476284751234L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_ID")
    protected Event event;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_FIELD_ID")
    protected EventField eventField;

    @Lob
    @Column(name = "STRING_VALUE")
    protected String stringValue;

    @Lob
    @Column(name = "TEXT_VALUE")
    protected String textValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor fileValue;

    @Column(name = "DATE_VALUE")
    protected LocalDate dateValue;

    @Column(name = "DATE_TIME_VALUE")
    protected LocalDateTime dateTimeValue;

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    public FileDescriptor getFileValue() {
        return fileValue;
    }

    public void setFileValue(FileDescriptor fileValue) {
        this.fileValue = fileValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public EventField getEventField() {
        return eventField;
    }

    public void setEventField(EventField eventField) {
        this.eventField = eventField;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
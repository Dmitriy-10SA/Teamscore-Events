package ru.teamscore.events.web.screens.event;

import com.haulmont.cuba.gui.screen.*;
import ru.teamscore.events.entity.Event;

@UiController("events_Event.browse")
@UiDescriptor("event-browse.xml")
@LookupComponent("eventsTable")
@LoadDataBeforeShow
public class EventBrowse extends StandardLookup<Event> {
}

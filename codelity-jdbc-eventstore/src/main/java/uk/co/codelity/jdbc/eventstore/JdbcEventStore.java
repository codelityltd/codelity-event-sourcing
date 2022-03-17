package uk.co.codelity.jdbc.eventstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.EventStream;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.exception.MapperException;
import uk.co.codelity.jdbc.eventstore.repository.EventRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class JdbcEventStore implements EventStore {
    private final EventRepository eventRepository;
    private final EventHandlerRegistry eventHandlerRegistry;
    private final ObjectMapper objectMapper;

    public JdbcEventStore(EventRepository eventRepository, EventHandlerRegistry eventHandlerRegistry, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.eventHandlerRegistry = eventHandlerRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void append(String streamId, Stream<EventInfo> events) throws EventPersistenceException {
        requireNonNull(streamId);
        requireNonNull(events);

        List<Event> eventList = events.map(this::buildEventLog)
                .collect(Collectors.toUnmodifiableList());

        try {
            eventRepository.saveAll(streamId, eventList);
        } catch (SQLException | MapperException e) {
            throw new EventPersistenceException("The events could not be saved.", e);
        }
    }

    public Iterable<EventInfo> loadEvents(String streamId) throws EventLoadException {
        requireNonNull(streamId);

        try {
            List<Event> events =  eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
            return events.stream().map(this::convertToEventInfo)
                    .collect(Collectors.toUnmodifiableList());
        } catch (SQLException | MapperException e) {
            throw new EventLoadException("The events could not be loaded!", e);
        }
    }

    @Override
    public EventStream getStreamById(String streamId) throws EventLoadException {
        requireNonNull(streamId);

        try {
            List<Event> events =  eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
            List<EventInfo> eventInfoList =  events.stream().map(this::convertToEventInfo)
                    .collect(Collectors.toUnmodifiableList());

            int position = 0;
            if (!eventInfoList.isEmpty()) {
                position = eventInfoList.get(eventInfoList.size() - 1).position;
            }

            return new EventStream(this, objectMapper, streamId, position, eventInfoList);
        } catch (SQLException | MapperException e) {
            throw new EventLoadException("The events could not be loaded!", e);
        }
    }

    private EventInfo convertToEventInfo(Event event) {
        return new EventInfo(event.streamId, event.position, event.name, event.metadata, event.payload);
    }

    private Event buildEventLog(EventInfo eventInfo) {
        Collection<String> handlerCodes = eventHandlerRegistry.getHandlersByEventName(eventInfo.name);

        return new Event(
                null,
                eventInfo.streamId,
                eventInfo.position,
                eventInfo.name,
                eventInfo.metadata,
                eventInfo.payload,
                LocalDateTime.now(),
                handlerCodes);

    }
}

package uk.co.codelity.jdbc.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.common.exceptions.MissingEventAnnotationException;
import uk.co.codelity.event.sourcing.common.utils.TypeUtils;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.exception.MapperException;
import uk.co.codelity.jdbc.eventstore.repository.EventRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public void append(String streamId, List<Object> events) throws EventPersistenceException {
        requireNonNull(streamId);
        requireNonNull(events);

        List<Event> eventList = new ArrayList<>();

        for (final Object event : events) {
            eventList.add(buildEventLog(streamId, event));
        }

        try {
            eventRepository.saveAll(streamId, eventList);
        } catch (SQLException | MapperException e) {
            throw new EventPersistenceException("The events could not be saved.", e);
        }
    }

    @Override
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

    private EventInfo convertToEventInfo(Event event) {
        return new EventInfo(event.streamId, event.position, event.name, event.metadata, event.payload);
    }

    private Event buildEventLog(final String streamId, final Object event) throws EventPersistenceException {
        try {
            String eventName = TypeUtils.eventNameOf(event);
            Collection<String> handlerCodes = eventHandlerRegistry.getHandlersByEventName(eventName);

            return new Event(
                    null,
                    streamId,
                    null,
                    eventName,
                    "",
                    objectMapper.writeValueAsString(event),
                    LocalDateTime.now(),
                    handlerCodes);


        } catch (MissingEventAnnotationException |
                JsonProcessingException e) {
            throw new EventPersistenceException(String.format("The events could not be published! %s", event), e);
        }
    }
}

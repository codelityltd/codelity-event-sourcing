package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStream;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public class AggregateService {
    private final EventSourcingContext eventSourcingContext;
    private final ObjectMapper objectMapper;
    private final ObjectFactory objectFactory;

    public AggregateService(EventSourcingContext eventSourcingContext,
                            ObjectFactory objectFactory,
                            ObjectMapper objectMapper) {
        this.eventSourcingContext = eventSourcingContext;
        this.objectMapper = objectMapper;
        this.objectFactory = objectFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T load(EventStream eventStream, Class<T> clazz) throws AggregateLoadException {
        requireNonNull(eventStream);
        requireNonNull(clazz);

        try {
            T aggregate = objectFactory.create(clazz);

            for (EventInfo event: eventStream.getEvents()) {
                final Class<?> eventType = eventSourcingContext.getEventType(event.name);
                final BiConsumer<T, Object> eventHandler = eventSourcingContext.getAggregateEventHandler(event.name);
                final Object obj = objectMapper.readValue(event.payload, eventType);
                eventHandler.accept(aggregate, obj);
            }

            return aggregate;

        } catch (Exception e) {
           throw new AggregateLoadException("An error occurred while loading Aggregate streamId:" + eventStream.getStreamId(), e);
        }
    }
}

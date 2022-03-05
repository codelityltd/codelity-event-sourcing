package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

import java.util.function.BiConsumer;

public class AggregateService {

    private final EventStore eventStore;
    private final EventSourcingContext eventSourcingContext;
    private final ObjectMapper objectMapper;
    private final ObjectFactory objectFactory;

    public AggregateService(final EventStore eventStore,
                            final EventSourcingContext eventSourcingContext,
                            final ObjectFactory objectFactory,
                            final ObjectMapper objectMapper) {
        this.eventStore = eventStore;
        this.eventSourcingContext = eventSourcingContext;
        this.objectMapper = objectMapper;
        this.objectFactory = objectFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T load(final String streamId, final Class<T> clazz) throws AggregateLoadException {
        try {
            Iterable<EventInfo> events = eventStore.loadEvents(streamId);
            T aggregate = objectFactory.create(clazz);

            for (EventInfo event: events) {
                final Class<?> eventType = eventSourcingContext.getEventType(event.name);
                final BiConsumer<T, Object> eventHandler = eventSourcingContext.getAggregateEventHandler(event.name);
                final Object obj = objectMapper.readValue(event.payload, eventType);
                eventHandler.accept(aggregate, obj);
            }

            return aggregate;

        } catch (Exception e) {
           throw new AggregateLoadException("An error occurred while loading Aggregate streamId:" + streamId, e);
        }
    }
}

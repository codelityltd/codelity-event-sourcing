package uk.co.codelity.event.sourcing.core.context;

import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateEventHandlerNotFoundException;
import uk.co.codelity.event.sourcing.core.exceptions.EventHandlerNotFoundException;
import uk.co.codelity.event.sourcing.core.exceptions.EventNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class EventSourcingContext implements EventHandlerRegistry {
    private final Map<String, Class<?>> eventNameAndTypeMapping;
    private final Map<String, Map<String, EventSubscription>> eventHandlers;
    private final Map<String, BiConsumer<?, ?>> aggregateEventHandlers;

    private EventSourcingContext(Map<String, Class<?>> eventNameAndTypeMapping, Map<String, Map<String, EventSubscription>> eventHandlers, Map<String, BiConsumer<?, ?>> aggregateEventHandlers){
        this.eventNameAndTypeMapping = eventNameAndTypeMapping;
        this.eventHandlers = eventHandlers;
        this.aggregateEventHandlers = aggregateEventHandlers;
    }

    public static EventSourcingContextBuilder builder() {
        return new EventSourcingContextBuilder();
    }

    public Class<?> getEventType(String eventName) throws EventNotFoundException {
        if (!this.eventNameAndTypeMapping.containsKey(eventName)) {
            throw new EventNotFoundException(eventName);
        } else {
            return this.eventNameAndTypeMapping.get(eventName);
        }
    }


    public EventSubscription getEventSubscription(String eventName, String handlerCode) throws EventHandlerNotFoundException {
        if (!this.eventHandlers.containsKey(eventName)) {
            throw new EventHandlerNotFoundException(String.format("EventHandler not found. EventName: %s", eventName));
        }

        if (!this.eventHandlers.get(eventName).containsKey(handlerCode)) {
            throw new EventHandlerNotFoundException(
                    String.format("EventHandler not found. EventName: %s, HandlerCode: %s", eventName, handlerCode));
        }

        return this.eventHandlers.get(eventName).get(handlerCode);
    }

    @SuppressWarnings("java:S3740")
    public BiConsumer getAggregateEventHandler(String eventName) throws AggregateEventHandlerNotFoundException {
        if (!this.aggregateEventHandlers.containsKey(eventName)) {
            throw new AggregateEventHandlerNotFoundException("AggregateEventHandler not found. EventName:" + eventName);
        } else {
            return this.aggregateEventHandlers.get(eventName);
        }
    }

    @Override
    public Collection<String> getHandlersByEventName(String eventName) {
        if (!eventHandlers.containsKey(eventName)) {
            return Collections.emptyList();
        }

        return eventHandlers.get(eventName).keySet();
    }

    public static class EventSourcingContextBuilder {
        private Map<String, Class<?>> eventNameAndTypeMapping = Collections.emptyMap();
        private Map<String, Map<String, EventSubscription>> eventHandlers = Collections.emptyMap();
        private Map<String, BiConsumer<?, ?>> aggregateEventHandlers = Collections.emptyMap();

        public EventSourcingContextBuilder withEvents(Map<String, Class<?>> eventNameAndTypeMapping) {
            this.eventNameAndTypeMapping = eventNameAndTypeMapping;
            return this;
        }

        public EventSourcingContextBuilder withEventHandlers(Map<String, Map<String, EventSubscription>> eventHandlers) {
            this.eventHandlers = eventHandlers;
            return this;
        }

        public EventSourcingContextBuilder withAggregateEventHandlers(Map<String, BiConsumer<?, ?>> aggregateEventHandlers) {
            this.aggregateEventHandlers = aggregateEventHandlers;
            return this;
        }

        public EventSourcingContext build() {
            return new EventSourcingContext(eventNameAndTypeMapping, eventHandlers, aggregateEventHandlers);
        }
    }
}

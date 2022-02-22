package uk.co.codelity.event.sourcing.core.context;

import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateEventHandlerNotFoundException;
import uk.co.codelity.event.sourcing.core.exceptions.EventNotFoundException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class EventSourcingContext implements EventHandlerRegistry {
    private final Map<String, Class<?>> eventNameAndTypeMapping;
    private final Collection<Method> eventHandlerMethods;
    private final Map<String, BiConsumer<?, ?>> aggregateEventHandlers;

    private EventSourcingContext(Map<String, Class<?>> eventNameAndTypeMapping, Collection<Method> eventHandlerMethods, Map<String, BiConsumer<?, ?>> aggregateEventHandlers){
        this.eventNameAndTypeMapping = eventNameAndTypeMapping;
        this.eventHandlerMethods = eventHandlerMethods;
        this.aggregateEventHandlers = aggregateEventHandlers;
    }

    public Collection<Method> getEventHandlerMethods() {
        return eventHandlerMethods;
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

    public BiConsumer<?, ?> getEventHandler(String eventName) throws AggregateEventHandlerNotFoundException {
        if (!this.aggregateEventHandlers.containsKey(eventName)) {
            throw new AggregateEventHandlerNotFoundException(eventName);
        } else {
            return this.aggregateEventHandlers.get(eventName);
        }
    }

    @Override
    public Collection<String> getHandlersByEventName(String s) {
        return Collections.emptySet();
    }

    public static class EventSourcingContextBuilder {
        private Map<String, Class<?>> eventNameAndTypeMapping = Collections.emptyMap();
        private Collection<Method> eventHandlerMethods = Collections.emptySet();
        private Map<String, BiConsumer<?, ?>> aggregateEventHandlers = Collections.emptyMap();

        public EventSourcingContextBuilder withEvents(Map<String, Class<?>> eventNameAndTypeMapping) {
            this.eventNameAndTypeMapping = eventNameAndTypeMapping;
            return this;
        }

        public EventSourcingContextBuilder withEventHandlers(Collection<Method> eventHandlerMethods) {
            this.eventHandlerMethods = eventHandlerMethods;
            return this;
        }

        public EventSourcingContextBuilder withAggregateEventHandlers(Map<String, BiConsumer<?, ?>> aggregateEventHandlers) {
            this.aggregateEventHandlers = aggregateEventHandlers;
            return this;
        }

        public EventSourcingContext build() {
            return new EventSourcingContext(eventNameAndTypeMapping, eventHandlerMethods, aggregateEventHandlers);
        }
    }
}

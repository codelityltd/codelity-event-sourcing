package uk.co.codelity.event.sourcing.core.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class EventSourcingContext {
    private final Collection<Class<?>> eventClasses;
    private final Collection<Method> eventHandlerMethods;
    private final Collection<Method> aggregateEventHandlerMethods;

    private EventSourcingContext(Collection<Class<?>> eventClasses, Collection<Method> eventHandlerMethods, Collection<Method> aggregateEventHandlerMethods){
        this.eventClasses = eventClasses;
        this.eventHandlerMethods = eventHandlerMethods;
        this.aggregateEventHandlerMethods = aggregateEventHandlerMethods;
    }

    public Collection<Class<?>> getEventClasses() {
        return eventClasses;
    }

    public Collection<Method> getEventHandlerMethods() {
        return eventHandlerMethods;
    }

    public Collection<Method> getAggregateEventHandlerMethods() {
        return aggregateEventHandlerMethods;
    }

    public static EventSourcingContextBuilder builder() {
        return new EventSourcingContextBuilder();
    }

    public static class EventSourcingContextBuilder {
        private Collection<Class<?>> eventClasses = Collections.emptySet();
        private Collection<Method> eventHandlerMethods = Collections.emptySet();
        private Collection<Method> aggregateEventHandlerMethods = Collections.emptySet();

        public EventSourcingContextBuilder withEvents(Collection<Class<?>> eventClasses) {
            this.eventClasses = eventClasses;
            return this;
        }

        public EventSourcingContextBuilder withEventHandlers(Collection<Method> eventHandlerMethods) {
            this.eventHandlerMethods = eventHandlerMethods;
            return this;
        }

        public EventSourcingContextBuilder withAggregateEventHandlers(Collection<Method> aggregateEventHandlerMethods) {
            this.aggregateEventHandlerMethods = aggregateEventHandlerMethods;
            return this;
        }

        public EventSourcingContext build() {
            return new EventSourcingContext(eventClasses, eventHandlerMethods, aggregateEventHandlerMethods);
        }
    }
}

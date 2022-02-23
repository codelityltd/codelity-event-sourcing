package uk.co.codelity.event.sourcing.core.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.exceptions.BootstrapException;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;
import uk.co.codelity.event.sourcing.core.utils.HandlerLambdaFactory;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class Bootstrapper {
    Logger logger = LoggerFactory.getLogger(Bootstrapper.class);

    private final EventScanner eventScanner;
    private final AggregateEventHandlerScanner aggregateEventHandlerScanner;
    private final EventHandlerScanner eventHandlerScanner;

    public Bootstrapper(EventScanner eventScanner, AggregateEventHandlerScanner aggregateEventHandlerScanner, EventHandlerScanner eventHandlerScanner) {
        this.eventScanner = eventScanner;
        this.aggregateEventHandlerScanner = aggregateEventHandlerScanner;
        this.eventHandlerScanner = eventHandlerScanner;
    }

    public EventSourcingContext initContext(String applicationPackageName) throws BootstrapException {
        requireNonNull(applicationPackageName);
        if (!isEventSourcingEnabled(applicationPackageName)) {
            return EventSourcingContext.builder()
                    .build();
        }

        Collection<Class<?>> eventClasses = scanForEvents(applicationPackageName);
        Collection<Method> eventHandlerMethods = scanForEventHandlers(applicationPackageName);
        Collection<Method> aggregateEventHandlerMethods = scanForAggregateEventHandlers(applicationPackageName);

        Map<String, Class<?>> eventNameAndTypeMapping = buildEventNameAndTypeMapping(eventClasses);
        Map<String, BiConsumer<?, ?>> aggregateEventHandlers = buildAggregateEventHandlers(aggregateEventHandlerMethods);


        return EventSourcingContext.builder()
                .withEvents(eventNameAndTypeMapping)
                .withEventHandlers(eventHandlerMethods)
                .withAggregateEventHandlers(aggregateEventHandlers)
                .build();
    }

    private Map<String, BiConsumer<?, ?>> buildAggregateEventHandlers(Collection<Method> aggregateEventHandlerMethods) throws BootstrapException {
        Map<String, BiConsumer<?, ?>> aggregateEventHandlers = new HashMap<>();
        for (Method method: aggregateEventHandlerMethods) {
            AggregateEventHandler aggregateMethod = method.getAnnotation(AggregateEventHandler.class);
            if (nonNull(aggregateMethod)) {
                if (method.getParameterCount() != 1) {
                    throw new BootstrapException(String.format("Aggregate event handler has more than one parameter. class: %s method: %s", method.getDeclaringClass().getName(), method.getName()));
                }

                Class<?> eventClass = method.getParameterTypes()[0];
                Event eventAnnotation = eventClass.getAnnotation(Event.class);
                if (isNull(eventAnnotation)) {
                    throw new BootstrapException(String.format("Aggregate event handler has a parameter which is not annotated with @Event. class: %s method: %s", method.getDeclaringClass().getName(), method.getName()));
                }

                try {
                    BiConsumer<?, ?> handler = HandlerLambdaFactory.createHandlerLambda(method.getDeclaringClass(), eventClass, method.getName());
                    aggregateEventHandlers.put(eventAnnotation.name(), handler);
                } catch (Throwable e) {
                    throw new BootstrapException("Aggregate event handler proxy could not be created.", e);
                }
            }
        }
        return aggregateEventHandlers;
    }

    private Map<String, Class<?>> buildEventNameAndTypeMapping (Collection<Class<?>> eventClasses) throws BootstrapException {
        Map<String, Class<?>> eventNameAndTypeMapping = new HashMap<>();
        for (Class<?> clazz: eventClasses) {
            Event event = clazz.getAnnotation(Event.class);
            if (isNull(event.name())) {
                throw new BootstrapException(String.format("Event annotation mush have name attribute. class: %s", clazz.getName()));
            }
            eventNameAndTypeMapping.put(event.name(), clazz);
        }

        return eventNameAndTypeMapping;
    }

    private boolean isEventSourcingEnabled(String applicationPackageName) throws BootstrapException {
        try {
            Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(applicationPackageName, EventSourcingEnabled.class);
            return !classes.isEmpty();
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while looking at EventSourcingEnabled annotation.", e);
        }
    }

    private Collection<Class<?>> scanForEvents(String applicationPackageName) throws BootstrapException {
        try {
            Collection<Class<?>> eventClasses = eventScanner.scanForEvents(applicationPackageName);
            logger.info("Event scan is completed.");
            return eventClasses;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for Event annotations.", e);
        }
    }

    private Collection<Method> scanForEventHandlers(String applicationPackageName) throws BootstrapException {
        try {
            Collection<Method> eventHandlerMethods = eventHandlerScanner.scanForEventHandlers(applicationPackageName);
            logger.info("EventHandler scan is completed.");
            return eventHandlerMethods;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for EventHandler annotations.", e);
        }
    }

    private Collection<Method> scanForAggregateEventHandlers(String applicationPackageName) throws BootstrapException {
        try {
            Collection<Method> methods = aggregateEventHandlerScanner.scanForAggregateEventHandlers(applicationPackageName);
            logger.info("Aggregate scan is completed.");
            return methods;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for AggregateEventHandler annotations.", e);
        }
    }
}

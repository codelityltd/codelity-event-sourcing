package uk.co.codelity.event.sourcing.core.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.exceptions.BootstrapException;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

        Map<String, Class<?>> eventNameAndTypeMapping = new HashMap<>();
        eventClasses.forEach(
                clazz -> {
                    Event event = clazz.getAnnotation(Event.class);
                    eventNameAndTypeMapping.put(event.name(), clazz);
                }
        );

        Map<String, Method> aggregateEventHandlers = new HashMap<>();
        aggregateEventHandlerMethods.forEach(
                method -> {
                    AggregateEventHandler aggregateMethod = method.getAnnotation(AggregateEventHandler.class);
                    if (nonNull(aggregateMethod)) {
                        String eventClassName = method.getParameterTypes()[0].getName();
                        aggregateEventHandlers.put(eventClassName, method);
                    }
                }
        );

        return EventSourcingContext.builder()
                .withEvents(eventNameAndTypeMapping)
                .withEventHandlers(eventHandlerMethods)
                .withAggregateEventHandlers(aggregateEventHandlers)
                .build();
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
        String[] packages = eventPackages(applicationPackageName);
        String packagesAsStr = String.join(",", packages);
        logger.info("ApplicationEventListener is scanning for events. Packages are being scanned {}", packagesAsStr);
        try {
            Collection<Class<?>> eventClasses = eventScanner.scanForEvents(packages);
            logger.info("Event scan is completed.");
            return eventClasses;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for Event annotations.", e);
        }
    }

    private Collection<Method> scanForEventHandlers(String applicationPackageName) throws BootstrapException {
        String[] packages = eventHandlerPackages(applicationPackageName);
        String packagesAsStr = String.join(",", packages);
        logger.info("ApplicationEventListener is scanning for event handler methods. Packages are being scanned {}", packagesAsStr);
        try {
            Collection<Method> eventHandlerMethods = eventHandlerScanner.scanForEventHandlers(packages);
            logger.info("EventHandler scan is completed.");
            return eventHandlerMethods;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for EventHandler annotations.", e);
        }
    }

    private Collection<Method> scanForAggregateEventHandlers(String applicationPackageName) throws BootstrapException {
        String[] packages = aggregateEventHandlerPackages(applicationPackageName);
        String packagesAsStr = String.join(",", packages);
        logger.info("ApplicationEventListener is scanning for aggregate event handlers. Packages are being scanned {}", packagesAsStr);
        try {
            Collection<Method> methods = aggregateEventHandlerScanner.scanForAggregateEventHandlers(packages);
            logger.info("Aggregate scan is completed.");
            return methods;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for AggregateEventHandler annotations.", e);
        }
    }

    private String[] eventPackages(String applicationPackageName) throws BootstrapException {
        try {
            Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventScan.class);
            if (clazz.isPresent()) {
                EventScan eventScan = clazz.get().getAnnotation(EventScan.class);
                if (nonNull(eventScan)) {
                    return eventScan.basePackages();
                }
            }

            return new String[]{applicationPackageName};
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for EventScan annotation.", e);
        }
    }

    private String[] eventHandlerPackages(String applicationPackageName) throws BootstrapException {
        try {
            Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventHandlerScan.class);
            if (clazz.isPresent()) {
                EventHandlerScan eventScan = clazz.get().getAnnotation(EventHandlerScan.class);
                if (nonNull(eventScan)) {
                    return eventScan.basePackages();
                }
            }

            return new String[] { applicationPackageName };
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for EventHandlerScan annotation.", e);
        }
    }

    private String[] aggregateEventHandlerPackages(String applicationPackageName) throws BootstrapException {
        try {
            Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, AggregateEventHandlerScan.class);
            if (clazz.isPresent()) {
                AggregateEventHandlerScan eventScan = clazz.get().getAnnotation(AggregateEventHandlerScan.class);
                if (nonNull(eventScan)) {
                    return eventScan.basePackages();
                }
            }

            return new String[]{applicationPackageName};
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new BootstrapException("An error occured while scanning for AggregateEventHandlerScan annotation.", e);
        }
    }

}

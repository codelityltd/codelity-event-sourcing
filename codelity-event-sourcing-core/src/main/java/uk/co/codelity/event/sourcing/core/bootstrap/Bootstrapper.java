package uk.co.codelity.event.sourcing.core.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static uk.co.codelity.event.sourcing.core.utils.StringUtils.merge;

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

    public EventSourcingContext initContext(String applicationPackageName) throws Exception {

        if (!isEventSourcingEnabled(applicationPackageName)) {
            return EventSourcingContext.builder()
                    .build();
        }

        Collection<Class<?>> eventClasses = scanForEvents(applicationPackageName);
        Collection<Method> eventHandlerMethods = scanForEventHandlers(applicationPackageName);
        Collection<Method> aggregateEventHandlerMethods = scanForAggregateEventHandlers(applicationPackageName);

        return EventSourcingContext.builder()
                .withEvents(eventClasses)
                .withEventHandlers(eventHandlerMethods)
                .withAggregateEventHandlers(aggregateEventHandlerMethods)
                .build();
    }

    private boolean isEventSourcingEnabled(String applicationPackageName) throws Exception {
        Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(applicationPackageName, EventSourcingEnabled.class);
        return !classes.isEmpty();
    }

    private Collection<Class<?>> scanForEvents(String applicationPackageName) throws Exception {
        String[] packages = eventPackages(applicationPackageName);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is scanning for events. Packages are being scanned {}", packagesAsStr);
        Collection<Class<?>> eventClasses = eventScanner.scanForEvents(packages);
        logger.info("Event scan is completed.");
        return eventClasses;
    }

    private Collection<Method> scanForEventHandlers(String applicationPackageName) throws Exception {
        String[] packages = eventHandlerPackages(applicationPackageName);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is scanning for event handler methods. Packages are being scanned {}", packagesAsStr);
        Collection<Method> eventHandlerMethods = eventHandlerScanner.scanForEventHandlers(packages);
        logger.info("EventHandler scan is completed.");
        return eventHandlerMethods;
    }

    private Collection<Method> scanForAggregateEventHandlers(String applicationPackageName) throws Exception {
        String[] packages = aggregateEventHandlerPackages(applicationPackageName);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is scanning for aggregate event handlers. Packages are being scanned {}", packagesAsStr);
        Collection<Method> methods = aggregateEventHandlerScanner.scanForAggregateEventHandlers(packages);
        logger.info("Aggregate scan is completed.");
        return methods;
    }

    private String[] eventPackages(String applicationPackageName) throws Exception {
        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventScan.class);
        if (clazz.isPresent()) {
            EventScan eventScan = clazz.get().getAnnotation(EventScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[] { applicationPackageName };
    }

    private String[] eventHandlerPackages(String applicationPackageName) throws Exception {
        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventHandlerScan.class);
        if (clazz.isPresent()) {
            EventHandlerScan eventScan = clazz.get().getAnnotation(EventHandlerScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[] { applicationPackageName };
    }

    private String[] aggregateEventHandlerPackages(String applicationPackageName) throws Exception {
        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, AggregateEventHandlerScan.class);
        if (clazz.isPresent()) {
            AggregateEventHandlerScan eventScan = clazz.get().getAnnotation(AggregateEventHandlerScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[] { applicationPackageName };
    }

}

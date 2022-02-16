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
import uk.co.codelity.event.sourcing.core.utils.reflection.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;

import static java.util.Objects.nonNull;
import static uk.co.codelity.event.sourcing.core.utils.reflection.StringUtils.merge;

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

    public EventSourcingContext initContext(Class<?> applicationClass) throws Exception {
        boolean eventSourcingEnabled = nonNull(applicationClass.getDeclaredAnnotation(EventSourcingEnabled.class));

        if (!eventSourcingEnabled) {
            return EventSourcingContext.builder()
                    .build();
        }

        Collection<Class<?>> eventClasses = scanForEvents(applicationClass);
        Collection<Method> eventHandlerMethods = scanForEventHandlers(applicationClass);
        Collection<Method> aggregateEventHandlerMethods = scanForAggregateEventHandlers(applicationClass);

        return EventSourcingContext.builder()
                .withEvents(eventClasses)
                .withEventHandlers(eventHandlerMethods)
                .withAggregateEventHandlers(aggregateEventHandlerMethods)
                .build();
    }

    private Collection<Class<?>> scanForEvents(Class<?> applicationClass) throws Exception {
        String[] packages = eventPackages(applicationClass);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is starting scanning for events. Packages are being scanned {}", packagesAsStr);
        Collection<Class<?>> eventClasses = eventScanner.scanForEvents(packages);
        logger.info("Event scan is completed.");
        return eventClasses;
    }

    private Collection<Method> scanForEventHandlers(Class<?> applicationClass) throws Exception {
        String[] packages = eventHandlerPackages(applicationClass);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is starting scanning for event handler methods. Packages are being scanned {}", packagesAsStr);
        Collection<Method> eventHandlerMethods = eventHandlerScanner.scanForEventHandlers(packages);
        logger.info("EventHandler scan is completed.");
        return eventHandlerMethods;
    }

    private Collection<Method> scanForAggregateEventHandlers(Class<?> applicationClass) throws Exception {
        String[] packages = aggregateEventHandlerPackages(applicationClass);
        String packagesAsStr = merge(packages, ",");
        logger.info("ApplicationEventListener is starting scanning for aggregate event handlers. Packages are being scanned {}", packagesAsStr);
        Collection<Method> methods = aggregateEventHandlerScanner.scanForAggregateEventHandlers(packages);
        logger.info("Aggregate scan is completed.");
        return methods;
    }

    private String[] eventPackages(Class<?> applicationClass) {
        EventScan eventScan = applicationClass.getDeclaredAnnotation(EventScan.class);
        if (nonNull(eventScan)) {
            return eventScan.basePackages();
        }

        return new String[] { applicationClass.getPackageName() };
    }

    private String[] eventHandlerPackages(Class<?> applicationClass) {
        EventHandlerScan eventHandlerScan = applicationClass.getDeclaredAnnotation(EventHandlerScan.class);
        if (nonNull(eventHandlerScan)) {
            return eventHandlerScan.basePackages();
        }

        return new String[] { applicationClass.getPackageName() };
    }

    private String[] aggregateEventHandlerPackages(Class<?> applicationClass) {
        AggregateEventHandlerScan aggregateEventHandlerScan = applicationClass.getDeclaredAnnotation(AggregateEventHandlerScan.class);
        if (nonNull(aggregateEventHandlerScan)) {
            return aggregateEventHandlerScan.basePackages();
        }

        return new String[] { applicationClass.getPackageName() };
    }

}

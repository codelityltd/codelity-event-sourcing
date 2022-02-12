package uk.co.codelity.event.sourcing.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables event-sourcing. All packages in the same package or sub-packages are scanned
 * for Events, EventHandlers, AggregateEventHandlers. If your classes are in different packages
 * then you must use @EventScan, @EventHandlerScan and @AggregateEventHandlerScan annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventSourcingEnabled {
}

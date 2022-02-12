package uk.co.codelity.event.sourcing.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation interface for event-handlers.
 * Event-handler methods must have one and only one parameter
 * and the type of that parameter must be the type of the event to be handled.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
}

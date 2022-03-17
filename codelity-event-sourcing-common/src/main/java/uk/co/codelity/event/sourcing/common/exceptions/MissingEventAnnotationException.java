package uk.co.codelity.event.sourcing.common.exceptions;

/**
 * Thrown when event handler argument is not annotated with Event
 */
public class MissingEventAnnotationException extends Exception {
    public MissingEventAnnotationException() {
    }

    public MissingEventAnnotationException(String className) {
        super(String.format("Event (%s) must be annotated with @Event", className));
    }
}

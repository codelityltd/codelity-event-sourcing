package uk.co.codelity.event.sourcing.common.exceptions;

public class MissingEventAnnotationException extends Exception {
    public MissingEventAnnotationException() {
    }

    public MissingEventAnnotationException(String className) {
        super(String.format("Event (%s) must be annotated with @Event", className));
    }
}

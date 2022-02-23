package uk.co.codelity.event.sourcing.core.exceptions;

public class AggregateEventHandlerNotFoundException extends Exception {
    public AggregateEventHandlerNotFoundException(String message) {
        super(message);
    }
}

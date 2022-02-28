package uk.co.codelity.event.sourcing.core.exceptions;

public class EventHandlerNotFoundException extends Exception {
    public EventHandlerNotFoundException(String message) {
        super(message);
    }
}

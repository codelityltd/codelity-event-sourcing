package uk.co.codelity.event.sourcing.core.exceptions;

public class EventHandlerArgumentException extends Exception {
    public EventHandlerArgumentException(String message) {
        super(message);
    }
}

package uk.co.codelity.event.sourcing.core.exceptions;

public class EventNotFoundException extends Exception {
    public EventNotFoundException(String message) {
        super(message);
    }
}

package uk.co.codelity.event.sourcing.common.exceptions;

public class EventLoadException extends Exception {
    public EventLoadException(String message) {
        super(message);
    }

    public EventLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

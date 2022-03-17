package uk.co.codelity.event.sourcing.common.exceptions;

/**
 * Thrown when an error occurred while loading events.
 */
public class EventLoadException extends Exception {
    public EventLoadException(String message) {
        super(message);
    }

    public EventLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

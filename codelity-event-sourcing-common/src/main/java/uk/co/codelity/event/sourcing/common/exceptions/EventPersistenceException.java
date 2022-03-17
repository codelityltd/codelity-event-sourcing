package uk.co.codelity.event.sourcing.common.exceptions;

/**
 * Thrown when an error occurred while persisting events.
 */
public class EventPersistenceException extends Exception {
    public EventPersistenceException() {
    }

    public EventPersistenceException(String message) {
        super(message);
    }

    public EventPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

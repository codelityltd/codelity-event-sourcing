package uk.co.codelity.event.sourcing.common.exceptions;

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

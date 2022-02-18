package uk.co.codelity.event.sourcing.common.exceptions;

public class EventPublishException extends Exception {
    public EventPublishException() {
    }

    public EventPublishException(String message) {
        super(message);
    }

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}

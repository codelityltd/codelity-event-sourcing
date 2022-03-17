package uk.co.codelity.event.sourcing.common.exceptions;

/**
 * Thrown when an error occurred in an event-handler
 */
public class EventHandlerException extends Exception {
    public EventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

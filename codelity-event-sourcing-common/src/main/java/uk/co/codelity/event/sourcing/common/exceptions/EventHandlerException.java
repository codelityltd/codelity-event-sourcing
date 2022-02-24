package uk.co.codelity.event.sourcing.common.exceptions;

public class EventHandlerException extends Exception {
    public EventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

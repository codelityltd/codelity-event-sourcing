package uk.co.codelity.event.sourcing.core.exceptions;

public class AggregateLoadException extends Exception {
    public AggregateLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

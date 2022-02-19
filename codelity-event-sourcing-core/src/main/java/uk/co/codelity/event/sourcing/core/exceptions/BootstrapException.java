package uk.co.codelity.event.sourcing.core.exceptions;

public class BootstrapException extends Exception {
    public BootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}

package app.util;

/**
 * Domain-specific checked exception used by persistence code.
 * Wrapping IO and class-loading failures in this type lets the UI handle them consistently.
 */
public class ValidationException extends Exception {
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

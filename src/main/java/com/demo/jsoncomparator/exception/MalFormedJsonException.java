package com.demo.jsoncomparator.exception;

/**
 * Thrown when a malformed json is encountered
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
public class MalFormedJsonException extends DiffException {
    public MalFormedJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalFormedJsonException(String message) {
        super(message);
    }

    public MalFormedJsonException() {
        super("Parameter is not a valid json");
    }
}

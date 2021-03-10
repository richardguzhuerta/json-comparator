package com.demo.jsoncomparator.exception;

/**
 * Thrown when expected data is not present
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
public class NoDataFoundException extends DiffException {
    public NoDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDataFoundException(String message) {
        super(message);
    }
}

package com.demo.jsoncomparator.exception;

/**
 * Diff generic exception
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
public class DiffException extends RuntimeException {
    public DiffException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiffException(String message) {
        super(message);
    }
}

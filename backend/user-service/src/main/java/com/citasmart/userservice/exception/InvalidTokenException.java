package com.citasmart.userservice.exception;

/**
 * Exception thrown when a token is invalid
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

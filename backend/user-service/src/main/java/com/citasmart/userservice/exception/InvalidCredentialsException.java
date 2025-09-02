package com.citasmart.userservice.exception;

/**
 * Exception thrown when credentials are invalid
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

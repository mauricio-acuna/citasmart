package com.citasmart.userservice.exception;

/**
 * Exception thrown when a user is not found
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

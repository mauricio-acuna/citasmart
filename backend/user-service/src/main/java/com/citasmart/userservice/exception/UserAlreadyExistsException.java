package com.citasmart.userservice.exception;

/**
 * Exception thrown when a user already exists
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

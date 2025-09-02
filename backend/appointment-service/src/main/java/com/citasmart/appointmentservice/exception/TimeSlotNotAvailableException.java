package com.citasmart.appointmentservice.exception;

public class TimeSlotNotAvailableException extends RuntimeException {
    public TimeSlotNotAvailableException(String message) {
        super(message);
    }
    
    public TimeSlotNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

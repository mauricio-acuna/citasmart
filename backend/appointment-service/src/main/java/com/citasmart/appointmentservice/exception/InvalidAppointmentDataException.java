package com.citasmart.appointmentservice.exception;

public class InvalidAppointmentDataException extends RuntimeException {
    
    public InvalidAppointmentDataException(String message) {
        super(message);
    }
    
    public InvalidAppointmentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

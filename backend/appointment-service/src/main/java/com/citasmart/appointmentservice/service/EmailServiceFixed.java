package com.citasmart.appointmentservice.service;

import com.citasmart.appointmentservice.dto.UserResponse;
import com.citasmart.appointmentservice.model.Appointment;

public interface EmailService {
    
    void sendAppointmentConfirmationEmail(Appointment appointment, UserResponse patient, UserResponse doctor);
    
    void sendAppointmentReminderEmail(Appointment appointment, UserResponse patient, UserResponse doctor);
    
    void sendAppointmentCancellationEmail(Appointment appointment, UserResponse patient, UserResponse doctor);
    
    void sendAppointmentUpdateEmail(Appointment appointment, UserResponse patient, UserResponse doctor);
    
    void sendAppointmentRescheduleEmail(Appointment appointment, UserResponse patient, UserResponse doctor);
}

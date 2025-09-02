package com.citasmart.appointmentservice.service;

import com.citasmart.appointmentservice.model.Appointment;

public interface EmailService {
    
    void sendAppointmentConfirmation(Appointment appointment);
    
    void sendAppointmentReminder(Appointment appointment);
    
    void sendAppointmentCancellation(Appointment appointment);
    
    void sendAppointmentRescheduled(Appointment appointment);
    
    void sendAppointmentUpdated(Appointment appointment);
}

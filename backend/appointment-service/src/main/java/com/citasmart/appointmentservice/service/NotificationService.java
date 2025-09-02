package com.citasmart.appointmentservice.service;

import com.citasmart.appointmentservice.model.Appointment;

public interface NotificationService {
    
    void sendAppointmentCreatedNotification(Appointment appointment);
    
    void sendAppointmentUpdatedNotification(Appointment appointment);
    
    void sendAppointmentCancelledNotification(Appointment appointment);
    
    void sendAppointmentReminderNotification(Appointment appointment);
    
    void sendSmsNotification(String phoneNumber, String message);
    
    void sendPushNotification(Long userId, String title, String message);
}

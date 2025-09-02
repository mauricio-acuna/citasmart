package com.citasmart.appointmentservice.service;

import com.citasmart.appointmentservice.dto.AppointmentCreateRequest;
import com.citasmart.appointmentservice.dto.AppointmentUpdateRequest;
import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    
    // CRUD Operations
    Appointment createAppointment(AppointmentCreateRequest request, String createdBy);
    
    Appointment updateAppointment(Long id, AppointmentUpdateRequest request, String updatedBy);
    
    Appointment cancelAppointment(Long id, String reason, String cancelledBy);
    
    Appointment confirmAppointment(String confirmationToken);
    
    Appointment getAppointmentById(Long id);
    
    // Query Operations
    Page<Appointment> getAppointmentsByPatientId(Long patientId, Pageable pageable);
    
    Page<Appointment> getAppointmentsByDoctorId(Long doctorId, Pageable pageable);
    
    Page<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Page<Appointment> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);
    
    List<Appointment> getUpcomingAppointments();
    
    // Availability and Scheduling
    boolean checkDoctorAvailability(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    
    // Background Operations
    void sendAppointmentReminders();
}

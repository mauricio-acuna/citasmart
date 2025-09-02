package com.citasmart.appointmentservice.service;

import com.citasmart.appointmentservice.dto.*;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    
    // CRUD Operations
    AppointmentResponseDto createAppointment(AppointmentCreateDto createDto, String createdBy);
    
    AppointmentResponseDto getAppointmentById(Long id);
    
    AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto updateDto, String updatedBy);
    
    void deleteAppointment(Long id, String deletedBy);
    
    // Business Operations
    AppointmentResponseDto cancelAppointment(Long id, AppointmentCancelDto cancelDto, String cancelledBy);
    
    AppointmentResponseDto rescheduleAppointment(Long id, LocalDateTime newDate, String updatedBy);
    
    AppointmentResponseDto confirmAppointment(String confirmationToken);
    
    AppointmentResponseDto completeAppointment(Long id, String doctorNotes, String completedBy);
    
    AppointmentResponseDto markAsNoShow(Long id, String updatedBy);
    
    // Query Operations
    Page<AppointmentResponseDto> getAppointmentsByPatient(Long patientId, Pageable pageable);
    
    Page<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId, Pageable pageable);
    
    Page<AppointmentResponseDto> getAppointmentsByMedicalCenter(Long medicalCenterId, Pageable pageable);
    
    Page<AppointmentResponseDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);
    
    Page<AppointmentResponseDto> getAppointmentsByDateRange(
        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    List<AppointmentResponseDto> getDoctorSchedule(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Validation Operations
    boolean isTimeSlotAvailable(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    
    List<LocalDateTime> getAvailableTimeSlots(Long doctorId, LocalDateTime date, Integer durationMinutes);
    
    // Reminder Operations
    void sendAppointmentReminders();
    
    void sendAppointmentConfirmation(Long appointmentId);
    
    // Statistics
    Long getAppointmentCountByStatus(AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    Long getDoctorAppointmentCount(Long doctorId, AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    Long getPatientAppointmentCount(Long patientId, AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    List<AppointmentResponseDto> getTodaysAppointments();
    
    Long getTodaysCreatedAppointments();
}

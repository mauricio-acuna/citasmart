package com.citasmart.appointmentservice.service.impl;

import com.citasmart.appointmentservice.client.UserServiceClient;
import com.citasmart.appointmentservice.dto.AppointmentCreateRequest;
import com.citasmart.appointmentservice.dto.AppointmentUpdateRequest;
import com.citasmart.appointmentservice.dto.UserResponse;
import com.citasmart.appointmentservice.exception.AppointmentConflictException;
import com.citasmart.appointmentservice.exception.AppointmentNotFoundException;
import com.citasmart.appointmentservice.exception.InvalidAppointmentDataException;
import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.model.AppointmentHistory;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.repository.AppointmentHistoryRepository;
import com.citasmart.appointmentservice.repository.AppointmentRepository;
import com.citasmart.appointmentservice.service.AppointmentService;
import com.citasmart.appointmentservice.service.EmailService;
import com.citasmart.appointmentservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentHistoryRepository historyRepository;
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Override
    public Appointment createAppointment(AppointmentCreateRequest request, String createdBy) {
        log.info("Creating appointment for patient {} with doctor {}", request.getPatientId(), request.getDoctorId());
        
        // Validate appointment date is in the future
        if (request.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentDataException("Appointment date must be in the future");
        }
        
        // Check for conflicts
        LocalDateTime endTime = request.getAppointmentDate().plusMinutes(request.getDurationMinutes());
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointmentsSimple(
                request.getDoctorId(), request.getAppointmentDate(), endTime);
        
        if (!conflicts.isEmpty()) {
            throw new AppointmentConflictException("Doctor is not available at the requested time");
        }
        
        // Get patient and doctor details
        UserResponse patient = userServiceClient.getUserById(request.getPatientId());
        UserResponse doctor = userServiceClient.getUserById(request.getDoctorId());
        
        // Create appointment
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .medicalCenterId(request.getMedicalCenterId())
                .specialityId(request.getSpecialityId())
                .appointmentDate(request.getAppointmentDate())
                .durationMinutes(request.getDurationMinutes())
                .status(AppointmentStatus.SCHEDULED)
                .type(request.getType())
                .reason(request.getReason())
                .notes(request.getNotes())
                .patientPhone(request.getPatientPhone() != null ? request.getPatientPhone() : patient.getPhone())
                .patientEmail(request.getPatientEmail() != null ? request.getPatientEmail() : patient.getEmail())
                .confirmationToken(generateConfirmationToken())
                .createdBy(createdBy)
                .build();
        
        // Set transient fields for display purposes
        appointment.setPatientName(patient.getFirstName() + " " + patient.getLastName());
        appointment.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        
        appointment = appointmentRepository.save(appointment);
        
        // Create history record
        createHistoryRecord(appointment, "CREATED", "Appointment created", createdBy);
        
        // Send notifications asynchronously
        sendAppointmentNotifications(appointment, patient, doctor);
        
        log.info("Appointment created successfully with ID: {}", appointment.getId());
        return appointment;
    }

    @Override
    public Appointment updateAppointment(Long id, AppointmentUpdateRequest request, String updatedBy) {
        log.info("Updating appointment with ID: {}", id);
        
        Appointment appointment = getAppointmentById(id);
        
        // Validate that appointment can be updated
        if (appointment.getStatus() == AppointmentStatus.CANCELLED || 
            appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentDataException("Cannot update cancelled or completed appointment");
        }
        
        // Check for conflicts if date/time is being changed
        if (request.getAppointmentDate() != null && 
            !request.getAppointmentDate().equals(appointment.getAppointmentDate())) {
            
            LocalDateTime endTime = request.getAppointmentDate().plusMinutes(
                request.getDurationMinutes() != null ? request.getDurationMinutes() : appointment.getDurationMinutes());
            
            List<Appointment> conflicts = appointmentRepository.findConflictingAppointmentsSimple(
                    appointment.getDoctorId(), request.getAppointmentDate(), endTime);
            
            // Remove current appointment from conflicts
            conflicts.removeIf(conflict -> conflict.getId().equals(appointment.getId()));
            
            if (!conflicts.isEmpty()) {
                throw new AppointmentConflictException("Doctor is not available at the requested time");
            }
        }
        
        // Update fields
        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getDurationMinutes() != null) {
            appointment.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getReason() != null) {
            appointment.setReason(request.getReason());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getPatientPhone() != null) {
            appointment.setPatientPhone(request.getPatientPhone());
        }
        if (request.getPatientEmail() != null) {
            appointment.setPatientEmail(request.getPatientEmail());
        }
        
        appointment.setUpdatedBy(updatedBy);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Create history record
        createHistoryRecord(savedAppointment, "UPDATED", "Appointment updated", updatedBy);
        
        // Send update notifications
        UserResponse patient = userServiceClient.getUserById(savedAppointment.getPatientId());
        UserResponse doctor = userServiceClient.getUserById(savedAppointment.getDoctorId());
        sendUpdateNotifications(savedAppointment, patient, doctor);
        
        log.info("Appointment updated successfully with ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Override
    public Appointment cancelAppointment(Long id, String reason, String cancelledBy) {
        log.info("Cancelling appointment with ID: {}", id);
        
        Appointment appointment = getAppointmentById(id);
        
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentDataException("Appointment is already cancelled");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentDataException("Cannot cancel completed appointment");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledBy(cancelledBy);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setUpdatedBy(cancelledBy);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Create history record
        createHistoryRecord(savedAppointment, "CANCELLED", "Appointment cancelled: " + reason, cancelledBy);
        
        // Send cancellation notifications
        UserResponse patient = userServiceClient.getUserById(savedAppointment.getPatientId());
        UserResponse doctor = userServiceClient.getUserById(savedAppointment.getDoctorId());
        sendCancellationNotifications(savedAppointment, patient, doctor);
        
        log.info("Appointment cancelled successfully with ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Override
    public Appointment confirmAppointment(String confirmationToken) {
        log.info("Confirming appointment with token: {}", confirmationToken);
        
        Appointment appointment = appointmentRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new AppointmentNotFoundException("Invalid confirmation token"));
        
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new InvalidAppointmentDataException("Appointment cannot be confirmed in current status");
        }
        
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmationToken(null); // Remove token after confirmation
        appointment.setUpdatedBy("PATIENT_CONFIRMATION");
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Create history record
        createHistoryRecord(savedAppointment, "CONFIRMED", "Appointment confirmed by patient", "PATIENT_CONFIRMATION");
        
        log.info("Appointment confirmed successfully with ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByPatientId(Long patientId, Pageable pageable) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByDoctorId(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDate(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatusOrderByAppointmentDateDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkDoctorAvailability(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointmentsSimple(doctorId, startTime, endTime);
        return conflicts.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findUpcomingAppointments(LocalDateTime.now());
    }

    @Override
    @Async
    public void sendAppointmentReminders() {
        log.info("Sending appointment reminders");
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime tomorrowEnd = tomorrow.plusDays(1);
        
        List<Appointment> appointmentsForReminder = appointmentRepository
                .findAppointmentsForReminder(tomorrow, tomorrowEnd);
        
        for (Appointment appointment : appointmentsForReminder) {
            try {
                UserResponse patient = userServiceClient.getUserById(appointment.getPatientId());
                UserResponse doctor = userServiceClient.getUserById(appointment.getDoctorId());
                
                emailService.sendAppointmentReminderEmail(appointment, patient, doctor);
                notificationService.sendAppointmentReminderNotification(appointment);
                
                appointment.setReminderSent(true);
                appointmentRepository.save(appointment);
                
                log.info("Reminder sent for appointment ID: {}", appointment.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment ID: {}", appointment.getId(), e);
            }
        }
    }

    private void createHistoryRecord(Appointment appointment, String action, String description, String performedBy) {
        AppointmentHistory history = AppointmentHistory.builder()
                .appointment(appointment)
                .action(action)
                .description(description)
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .build();
        
        historyRepository.save(history);
    }

    private void sendAppointmentNotifications(Appointment appointment, UserResponse patient, UserResponse doctor) {
        try {
            emailService.sendAppointmentConfirmationEmail(appointment, patient, doctor);
            notificationService.sendAppointmentCreatedNotification(appointment);
        } catch (Exception e) {
            log.error("Failed to send appointment notifications for ID: {}", appointment.getId(), e);
        }
    }

    private void sendUpdateNotifications(Appointment appointment, UserResponse patient, UserResponse doctor) {
        try {
            emailService.sendAppointmentUpdateEmail(appointment, patient, doctor);
            notificationService.sendAppointmentUpdatedNotification(appointment);
        } catch (Exception e) {
            log.error("Failed to send update notifications for appointment ID: {}", appointment.getId(), e);
        }
    }

    private void sendCancellationNotifications(Appointment appointment, UserResponse patient, UserResponse doctor) {
        try {
            emailService.sendAppointmentCancellationEmail(appointment, patient, doctor);
            notificationService.sendAppointmentCancelledNotification(appointment);
        } catch (Exception e) {
            log.error("Failed to send cancellation notifications for appointment ID: {}", appointment.getId(), e);
        }
    }

    private String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }
}

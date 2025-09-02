package com.citasmart.appointmentservice.service.impl;

import com.citasmart.appointmentservice.client.UserDto;
import com.citasmart.appointmentservice.client.UserServiceClient;
import com.citasmart.appointmentservice.dto.*;
import com.citasmart.appointmentservice.exception.AppointmentNotFoundException;
import com.citasmart.appointmentservice.exception.BusinessValidationException;
import com.citasmart.appointmentservice.exception.TimeSlotNotAvailableException;
import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.model.AppointmentHistory;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.repository.AppointmentRepository;
import com.citasmart.appointmentservice.service.AppointmentService;
import com.citasmart.appointmentservice.service.EmailService;
import com.citasmart.appointmentservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    @Value("${app.business.working-hours.start:08:00}")
    private String workingHoursStart;
    
    @Value("${app.business.working-hours.end:18:00}")
    private String workingHoursEnd;
    
    @Value("${app.business.appointment-duration:30}")
    private Integer defaultDurationMinutes;
    
    @Value("${app.business.advance-booking-days:30}")
    private Integer advanceBookingDays;
    
    @Value("${app.business.cancellation-hours:24}")
    private Integer cancellationHours;

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto createAppointment(AppointmentCreateDto createDto, String createdBy) {
        log.info("Creating appointment for patient {} with doctor {} at {}", 
                createDto.getPatientId(), createDto.getDoctorId(), createDto.getAppointmentDate());
        
        // Validate business rules
        validateAppointmentCreation(createDto);
        
        // Check time slot availability
        if (!isTimeSlotAvailable(createDto.getDoctorId(), 
                createDto.getAppointmentDate(), 
                createDto.getAppointmentDate().plusMinutes(createDto.getDurationMinutes()))) {
            throw new TimeSlotNotAvailableException("Time slot is not available");
        }
        
        // Get user details
        UserDto patient = userServiceClient.getUserById(createDto.getPatientId());
        // TODO: Get doctor and medical center details from respective services
        
        // Create appointment
        Appointment appointment = Appointment.builder()
                .patientId(createDto.getPatientId())
                .patientName(patient.getFullName())
                .patientPhone(createDto.getPatientPhone() != null ? createDto.getPatientPhone() : patient.getPhone())
                .patientEmail(createDto.getPatientEmail() != null ? createDto.getPatientEmail() : patient.getEmail())
                .doctorId(createDto.getDoctorId())
                .doctorName("Dr. TODO") // TODO: Get from doctor service
                .doctorSpeciality("TODO Speciality") // TODO: Get from doctor service
                .medicalCenterId(createDto.getMedicalCenterId())
                .medicalCenterName("TODO Medical Center") // TODO: Get from medical center service
                .specialityId(createDto.getSpecialityId())
                .specialityName("TODO Speciality Name") // TODO: Get from speciality service
                .appointmentDate(createDto.getAppointmentDate())
                .durationMinutes(createDto.getDurationMinutes())
                .type(createDto.getType())
                .reason(createDto.getReason())
                .notes(createDto.getNotes())
                .status(AppointmentStatus.SCHEDULED)
                .confirmationToken(UUID.randomUUID().toString())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
        
        // Calculate end time
        appointment.setEndTime(appointment.getAppointmentDate().plusMinutes(appointment.getDurationMinutes()));
        
        // Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send confirmation email
        try {
            emailService.sendAppointmentConfirmation(savedAppointment);
        } catch (Exception e) {
            log.error("Failed to send confirmation email for appointment {}", savedAppointment.getId(), e);
        }
        
        // Send notification
        try {
            notificationService.sendAppointmentCreatedNotification(savedAppointment);
        } catch (Exception e) {
            log.error("Failed to send notification for appointment {}", savedAppointment.getId(), e);
        }
        
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @Cacheable(value = "appointments", key = "#id")
    public AppointmentResponseDto getAppointmentById(Long id) {
        log.debug("Fetching appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        return mapToResponseDto(appointment);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto updateDto, String updatedBy) {
        log.info("Updating appointment {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "UPDATE", updatedBy);
        
        // Update fields
        if (updateDto.getAppointmentDate() != null) {
            // Validate new time slot if date changed
            if (!updateDto.getAppointmentDate().equals(appointment.getAppointmentDate())) {
                LocalDateTime endTime = updateDto.getAppointmentDate()
                        .plusMinutes(updateDto.getDurationMinutes() != null ? 
                                updateDto.getDurationMinutes() : appointment.getDurationMinutes());
                
                if (!isTimeSlotAvailable(appointment.getDoctorId(), updateDto.getAppointmentDate(), endTime)) {
                    throw new TimeSlotNotAvailableException("New time slot is not available");
                }
                
                history.setPreviousDate(appointment.getAppointmentDate());
                history.setNewDate(updateDto.getAppointmentDate());
                appointment.setAppointmentDate(updateDto.getAppointmentDate());
            }
        }
        
        if (updateDto.getDurationMinutes() != null) {
            appointment.setDurationMinutes(updateDto.getDurationMinutes());
        }
        
        if (updateDto.getType() != null) {
            appointment.setType(updateDto.getType());
        }
        
        if (updateDto.getReason() != null) {
            appointment.setReason(updateDto.getReason());
        }
        
        if (updateDto.getNotes() != null) {
            appointment.setNotes(updateDto.getNotes());
        }
        
        if (updateDto.getDoctorNotes() != null) {
            appointment.setDoctorNotes(updateDto.getDoctorNotes());
        }
        
        if (updateDto.getPatientPhone() != null) {
            appointment.setPatientPhone(updateDto.getPatientPhone());
        }
        
        if (updateDto.getPatientEmail() != null) {
            appointment.setPatientEmail(updateDto.getPatientEmail());
        }
        
        appointment.setUpdatedBy(updatedBy);
        
        // Recalculate end time
        appointment.setEndTime(appointment.getAppointmentDate().plusMinutes(appointment.getDurationMinutes()));
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment {} updated successfully", id);
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public void deleteAppointment(Long id, String deletedBy) {
        log.info("Deleting appointment {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (!appointment.canBeCancelled()) {
            throw new BusinessValidationException("Appointment cannot be deleted");
        }
        
        appointmentRepository.delete(appointment);
        log.info("Appointment {} deleted successfully", id);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto cancelAppointment(Long id, AppointmentCancelDto cancelDto, String cancelledBy) {
        log.info("Cancelling appointment {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (!appointment.canBeCancelled()) {
            throw new BusinessValidationException("Appointment cannot be cancelled at this time");
        }
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "CANCEL", cancelledBy);
        history.setNewStatus(AppointmentStatus.CANCELLED);
        history.setChangeReason(cancelDto.getCancellationReason());
        
        // Update appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancelDto.getCancellationReason());
        appointment.setCancelledBy(cancelledBy);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setUpdatedBy(cancelledBy);
        
        if (cancelDto.getNotes() != null) {
            appointment.setNotes(appointment.getNotes() + "\n[CANCELLATION] " + cancelDto.getNotes());
        }
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send cancellation notification
        try {
            emailService.sendAppointmentCancellation(savedAppointment);
        } catch (Exception e) {
            log.error("Failed to send cancellation email for appointment {}", savedAppointment.getId(), e);
        }
        
        log.info("Appointment {} cancelled successfully", id);
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto rescheduleAppointment(Long id, LocalDateTime newDate, String updatedBy) {
        log.info("Rescheduling appointment {} to {}", id, newDate);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (!appointment.canBeRescheduled()) {
            throw new BusinessValidationException("Appointment cannot be rescheduled");
        }
        
        // Check new time slot availability
        LocalDateTime endTime = newDate.plusMinutes(appointment.getDurationMinutes());
        if (!isTimeSlotAvailable(appointment.getDoctorId(), newDate, endTime)) {
            throw new TimeSlotNotAvailableException("New time slot is not available");
        }
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "RESCHEDULE", updatedBy);
        history.setPreviousDate(appointment.getAppointmentDate());
        history.setNewDate(newDate);
        history.setNewStatus(AppointmentStatus.RESCHEDULED);
        
        // Update appointment
        appointment.setAppointmentDate(newDate);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedBy(updatedBy);
        appointment.setReminderSent(false); // Reset reminder for new date
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send rescheduling notification
        try {
            emailService.sendAppointmentRescheduled(savedAppointment);
        } catch (Exception e) {
            log.error("Failed to send rescheduling email for appointment {}", savedAppointment.getId(), e);
        }
        
        log.info("Appointment {} rescheduled successfully", id);
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @CacheEvict(value = "appointments", key = "#confirmationToken")
    public AppointmentResponseDto confirmAppointment(String confirmationToken) {
        log.info("Confirming appointment with token: {}", confirmationToken);
        
        Appointment appointment = appointmentRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new AppointmentNotFoundException("Invalid confirmation token"));
        
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessValidationException("Appointment is not in schedulable state");
        }
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "CONFIRM", "PATIENT");
        history.setNewStatus(AppointmentStatus.CONFIRMED);
        
        // Update appointment
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedBy("PATIENT");
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment {} confirmed successfully", savedAppointment.getId());
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto completeAppointment(Long id, String doctorNotes, String completedBy) {
        log.info("Completing appointment {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS && 
            appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessValidationException("Appointment is not in a completable state");
        }
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "COMPLETE", completedBy);
        history.setNewStatus(AppointmentStatus.COMPLETED);
        
        // Update appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDoctorNotes(doctorNotes);
        appointment.setUpdatedBy(completedBy);
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment {} completed successfully", id);
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @CacheEvict(value = {"appointments", "doctorSchedule"}, allEntries = true)
    public AppointmentResponseDto markAsNoShow(Long id, String updatedBy) {
        log.info("Marking appointment {} as no-show", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        
        // Create history record
        AppointmentHistory history = createHistoryRecord(appointment, "NO_SHOW", updatedBy);
        history.setNewStatus(AppointmentStatus.NO_SHOW);
        
        // Update appointment
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setUpdatedBy(updatedBy);
        
        // Add history and save
        appointment.getHistory().add(history);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment {} marked as no-show", id);
        return mapToResponseDto(savedAppointment);
    }

    @Override
    @Cacheable(value = "patientAppointments", key = "#patientId")
    public Page<AppointmentResponseDto> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        log.debug("Fetching appointments for patient {}", patientId);
        
        Page<Appointment> appointments = appointmentRepository
                .findByPatientIdOrderByAppointmentDateDesc(patientId, pageable);
        
        return appointments.map(this::mapToResponseDto);
    }

    @Override
    @Cacheable(value = "doctorAppointments", key = "#doctorId")
    public Page<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId, Pageable pageable) {
        log.debug("Fetching appointments for doctor {}", doctorId);
        
        Page<Appointment> appointments = appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDesc(doctorId, pageable);
        
        return appointments.map(this::mapToResponseDto);
    }

    @Override
    public Page<AppointmentResponseDto> getAppointmentsByMedicalCenter(Long medicalCenterId, Pageable pageable) {
        log.debug("Fetching appointments for medical center {}", medicalCenterId);
        
        Page<Appointment> appointments = appointmentRepository
                .findByMedicalCenterIdOrderByAppointmentDateDesc(medicalCenterId, pageable);
        
        return appointments.map(this::mapToResponseDto);
    }

    @Override
    public Page<AppointmentResponseDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        log.debug("Fetching appointments with status {}", status);
        
        Page<Appointment> appointments = appointmentRepository
                .findByStatusOrderByAppointmentDateDesc(status, pageable);
        
        return appointments.map(this::mapToResponseDto);
    }

    @Override
    public Page<AppointmentResponseDto> getAppointmentsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching appointments between {} and {}", startDate, endDate);
        
        Page<Appointment> appointments = appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDate(startDate, endDate, pageable);
        
        return appointments.map(this::mapToResponseDto);
    }

    @Override
    @Cacheable(value = "doctorSchedule", key = "#doctorId + '_' + #startDate + '_' + #endDate")
    public List<AppointmentResponseDto> getDoctorSchedule(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching schedule for doctor {} between {} and {}", doctorId, startDate, endDate);
        
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, startDate, endDate);
        
        return appointments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTimeSlotAvailable(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Checking availability for doctor {} from {} to {}", doctorId, startTime, endTime);
        
        // Check business hours
        if (!isWithinBusinessHours(startTime)) {
            return false;
        }
        
        // Check for conflicts
        List<Appointment> conflictingAppointments = appointmentRepository
                .findConflictingAppointments(doctorId, startTime, endTime);
        
        return conflictingAppointments.isEmpty();
    }

    @Override
    public List<LocalDateTime> getAvailableTimeSlots(Long doctorId, LocalDateTime date, Integer durationMinutes) {
        // TODO: Implement algorithm to find available time slots
        // This is a complex algorithm that needs to consider:
        // - Business hours
        // - Existing appointments
        // - Break times
        // - Buffer time between appointments
        
        log.debug("Finding available time slots for doctor {} on {}", doctorId, date);
        return Arrays.asList(); // Placeholder
    }

    @Override
    public void sendAppointmentReminders() {
        log.info("Sending appointment reminders");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);
        
        List<AppointmentStatus> reminderStatuses = Arrays.asList(
                AppointmentStatus.SCHEDULED, 
                AppointmentStatus.CONFIRMED
        );
        
        List<Appointment> appointmentsForReminder = appointmentRepository
                .findUpcomingAppointmentsForReminder(reminderStatuses, now, tomorrow);
        
        for (Appointment appointment : appointmentsForReminder) {
            try {
                emailService.sendAppointmentReminder(appointment);
                appointment.setReminderSent(true);
                appointmentRepository.save(appointment);
                
                log.debug("Reminder sent for appointment {}", appointment.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment {}", appointment.getId(), e);
            }
        }
        
        log.info("Sent {} appointment reminders", appointmentsForReminder.size());
    }

    @Override
    public void sendAppointmentConfirmation(Long appointmentId) {
        log.info("Sending confirmation for appointment {}", appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));
        
        try {
            emailService.sendAppointmentConfirmation(appointment);
            log.info("Confirmation sent for appointment {}", appointmentId);
        } catch (Exception e) {
            log.error("Failed to send confirmation for appointment {}", appointmentId, e);
            throw new RuntimeException("Failed to send confirmation", e);
        }
    }

    // Statistics methods
    @Override
    public Long getAppointmentCountByStatus(AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countByStatusAndDateRange(status, startDate, endDate);
    }

    @Override
    public Long getDoctorAppointmentCount(Long doctorId, AppointmentStatus status, 
                                        LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countByDoctorAndStatusAndDateRange(doctorId, status, startDate, endDate);
    }

    @Override
    public Long getPatientAppointmentCount(Long patientId, AppointmentStatus status, 
                                         LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countByPatientAndStatusAndDateRange(patientId, status, startDate, endDate);
    }

    @Override
    public List<AppointmentResponseDto> getTodaysAppointments() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime tomorrow = today.plusDays(1);
        
        List<Appointment> appointments = appointmentRepository.findTodaysAppointments(today, tomorrow);
        
        return appointments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTodaysCreatedAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return appointmentRepository.countTodaysCreatedAppointments(startOfDay);
    }

    // Private helper methods
    private void validateAppointmentCreation(AppointmentCreateDto createDto) {
        // Validate appointment is in the future
        if (createDto.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException("Appointment date must be in the future");
        }
        
        // Validate appointment is within advance booking limit
        LocalDateTime maxDate = LocalDateTime.now().plusDays(advanceBookingDays);
        if (createDto.getAppointmentDate().isAfter(maxDate)) {
            throw new BusinessValidationException(
                    "Appointment cannot be booked more than " + advanceBookingDays + " days in advance");
        }
        
        // Validate business hours
        if (!isWithinBusinessHours(createDto.getAppointmentDate())) {
            throw new BusinessValidationException("Appointment must be within business hours");
        }
    }
    
    private boolean isWithinBusinessHours(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        LocalTime start = LocalTime.parse(workingHoursStart);
        LocalTime end = LocalTime.parse(workingHoursEnd);
        
        return !time.isBefore(start) && !time.isAfter(end);
    }
    
    private AppointmentHistory createHistoryRecord(Appointment appointment, String action, String changedBy) {
        return AppointmentHistory.builder()
                .appointment(appointment)
                .previousStatus(appointment.getStatus())
                .changeReason(action)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .build();
    }
    
    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatientName())
                .patientPhone(appointment.getPatientPhone())
                .patientEmail(appointment.getPatientEmail())
                .doctorId(appointment.getDoctorId())
                .doctorName(appointment.getDoctorName())
                .doctorSpeciality(appointment.getDoctorSpeciality())
                .medicalCenterId(appointment.getMedicalCenterId())
                .medicalCenterName(appointment.getMedicalCenterName())
                .medicalCenterAddress(appointment.getMedicalCenterAddress())
                .specialityId(appointment.getSpecialityId())
                .specialityName(appointment.getSpecialityName())
                .appointmentDate(appointment.getAppointmentDate())
                .endTime(appointment.getEndTime())
                .durationMinutes(appointment.getDurationMinutes())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .doctorNotes(appointment.getDoctorNotes())
                .reminderSent(appointment.getReminderSent())
                .confirmationToken(appointment.getConfirmationToken())
                .cancellationReason(appointment.getCancellationReason())
                .cancelledBy(appointment.getCancelledBy())
                .cancelledAt(appointment.getCancelledAt())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .createdBy(appointment.getCreatedBy())
                .updatedBy(appointment.getUpdatedBy())
                .version(appointment.getVersion())
                .canBeCancelled(appointment.canBeCancelled())
                .canBeRescheduled(appointment.canBeRescheduled())
                .isUpcoming(appointment.isUpcoming())
                .build();
    }
}

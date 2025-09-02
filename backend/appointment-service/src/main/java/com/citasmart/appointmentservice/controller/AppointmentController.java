package com.citasmart.appointmentservice.controller;

import com.citasmart.appointmentservice.dto.*;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointments", description = "Appointment management operations")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {
    
    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Create a new appointment", 
               description = "Creates a new appointment with the provided details")
    @ApiResponse(responseCode = "201", description = "Appointment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid appointment data")
    @ApiResponse(responseCode = "409", description = "Time slot not available")
    @PreAuthorize("hasAnyRole('PATIENT', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody AppointmentCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating appointment for patient {} with doctor {}", 
                createDto.getPatientId(), createDto.getDoctorId());
        
        AppointmentResponseDto response = appointmentService.createAppointment(
                createDto, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", 
               description = "Retrieves a specific appointment by its ID")
    @ApiResponse(responseCode = "200", description = "Appointment found")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> getAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        
        log.debug("Fetching appointment with ID: {}", id);
        
        AppointmentResponseDto response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", 
               description = "Updates an existing appointment")
    @ApiResponse(responseCode = "200", description = "Appointment updated successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @ApiResponse(responseCode = "400", description = "Invalid update data")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateDto updateDto,
            Authentication authentication) {
        
        log.info("Updating appointment {}", id);
        
        AppointmentResponseDto response = appointmentService.updateAppointment(
                id, updateDto, authentication.getName());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment", 
               description = "Deletes an appointment")
    @ApiResponse(responseCode = "204", description = "Appointment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Void> deleteAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deleting appointment {}", id);
        
        appointmentService.deleteAppointment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", 
               description = "Cancels an existing appointment")
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @ApiResponse(responseCode = "400", description = "Appointment cannot be cancelled")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Valid @RequestBody AppointmentCancelDto cancelDto,
            Authentication authentication) {
        
        log.info("Cancelling appointment {}", id);
        
        AppointmentResponseDto response = appointmentService.cancelAppointment(
                id, cancelDto, authentication.getName());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule appointment", 
               description = "Reschedules an appointment to a new date and time")
    @ApiResponse(responseCode = "200", description = "Appointment rescheduled successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @ApiResponse(responseCode = "409", description = "New time slot not available")
    @PreAuthorize("hasAnyRole('PATIENT', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Parameter(description = "New appointment date and time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate,
            Authentication authentication) {
        
        log.info("Rescheduling appointment {} to {}", id, newDate);
        
        AppointmentResponseDto response = appointmentService.rescheduleAppointment(
                id, newDate, authentication.getName());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/confirm/{token}")
    @Operation(summary = "Confirm appointment", 
               description = "Confirms an appointment using confirmation token")
    @ApiResponse(responseCode = "200", description = "Appointment confirmed successfully")
    @ApiResponse(responseCode = "404", description = "Invalid confirmation token")
    public ResponseEntity<AppointmentResponseDto> confirmAppointment(
            @Parameter(description = "Confirmation token") @PathVariable String token) {
        
        log.info("Confirming appointment with token: {}", token);
        
        AppointmentResponseDto response = appointmentService.confirmAppointment(token);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete appointment", 
               description = "Marks an appointment as completed")
    @ApiResponse(responseCode = "200", description = "Appointment completed successfully")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Parameter(description = "Doctor notes") @RequestParam(required = false) String doctorNotes,
            Authentication authentication) {
        
        log.info("Completing appointment {}", id);
        
        AppointmentResponseDto response = appointmentService.completeAppointment(
                id, doctorNotes, authentication.getName());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/no-show")
    @Operation(summary = "Mark as no-show", 
               description = "Marks an appointment as no-show")
    @ApiResponse(responseCode = "200", description = "Appointment marked as no-show")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> markAsNoShow(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Marking appointment {} as no-show", id);
        
        AppointmentResponseDto response = appointmentService.markAsNoShow(
                id, authentication.getName());
        
        return ResponseEntity.ok(response);
    }

    // Query endpoints
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient", 
               description = "Retrieves all appointments for a specific patient")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN') or #patientId == authentication.principal.id")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByPatient(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @PageableDefault(sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.debug("Fetching appointments for patient {}", patientId);
        
        Page<AppointmentResponseDto> appointments = appointmentService
                .getAppointmentsByPatient(patientId, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor", 
               description = "Retrieves all appointments for a specific doctor")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST', 'ADMIN') or #doctorId == authentication.principal.id")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @PageableDefault(sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching appointments for doctor {}", doctorId);
        
        Page<AppointmentResponseDto> appointments = appointmentService
                .getAppointmentsByDoctor(doctorId, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/medical-center/{medicalCenterId}")
    @Operation(summary = "Get appointments by medical center", 
               description = "Retrieves all appointments for a specific medical center")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByMedicalCenter(
            @Parameter(description = "Medical Center ID") @PathVariable Long medicalCenterId,
            @PageableDefault(sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching appointments for medical center {}", medicalCenterId);
        
        Page<AppointmentResponseDto> appointments = appointmentService
                .getAppointmentsByMedicalCenter(medicalCenterId, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get appointments by status", 
               description = "Retrieves all appointments with a specific status")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByStatus(
            @Parameter(description = "Appointment status") @PathVariable AppointmentStatus status,
            @PageableDefault(sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching appointments with status {}", status);
        
        Page<AppointmentResponseDto> appointments = appointmentService
                .getAppointmentsByStatus(status, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get appointments by date range", 
               description = "Retrieves appointments within a specific date range")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching appointments between {} and {}", startDate, endDate);
        
        Page<AppointmentResponseDto> appointments = appointmentService
                .getAppointmentsByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}/schedule")
    @Operation(summary = "Get doctor schedule", 
               description = "Retrieves the schedule for a specific doctor within a date range")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST', 'ADMIN') or #doctorId == authentication.principal.id")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorSchedule(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.debug("Fetching schedule for doctor {} between {} and {}", doctorId, startDate, endDate);
        
        List<AppointmentResponseDto> schedule = appointmentService
                .getDoctorSchedule(doctorId, startDate, endDate);
        
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/doctor/{doctorId}/availability")
    @Operation(summary = "Check time slot availability", 
               description = "Checks if a specific time slot is available for a doctor")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Start time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        log.debug("Checking availability for doctor {} from {} to {}", doctorId, startTime, endTime);
        
        boolean available = appointmentService.isTimeSlotAvailable(doctorId, startTime, endTime);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/doctor/{doctorId}/available-slots")
    @Operation(summary = "Get available time slots", 
               description = "Returns available time slots for a doctor on a specific date")
    public ResponseEntity<List<LocalDateTime>> getAvailableTimeSlots(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @Parameter(description = "Duration in minutes") 
            @RequestParam(defaultValue = "30") Integer durationMinutes) {
        
        log.debug("Getting available slots for doctor {} on {}", doctorId, date);
        
        List<LocalDateTime> availableSlots = appointmentService
                .getAvailableTimeSlots(doctorId, date, durationMinutes);
        
        return ResponseEntity.ok(availableSlots);
    }

    // Statistics endpoints
    @GetMapping("/statistics/count")
    @Operation(summary = "Get appointment statistics", 
               description = "Retrieves appointment count statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Long> getAppointmentCount(
            @Parameter(description = "Status") @RequestParam AppointmentStatus status,
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Long count = appointmentService.getAppointmentCountByStatus(status, startDate, endDate);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's appointments", 
               description = "Retrieves all appointments for today")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getTodaysAppointments() {
        
        log.debug("Fetching today's appointments");
        
        List<AppointmentResponseDto> appointments = appointmentService.getTodaysAppointments();
        return ResponseEntity.ok(appointments);
    }

    // Management endpoints
    @PostMapping("/send-reminders")
    @Operation(summary = "Send appointment reminders", 
               description = "Sends reminder notifications for upcoming appointments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendAppointmentReminders() {
        
        log.info("Manually triggering appointment reminders");
        
        appointmentService.sendAppointmentReminders();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/send-confirmation")
    @Operation(summary = "Send appointment confirmation", 
               description = "Sends confirmation email for a specific appointment")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Void> sendAppointmentConfirmation(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        
        log.info("Sending confirmation for appointment {}", id);
        
        appointmentService.sendAppointmentConfirmation(id);
        return ResponseEntity.ok().build();
    }
}

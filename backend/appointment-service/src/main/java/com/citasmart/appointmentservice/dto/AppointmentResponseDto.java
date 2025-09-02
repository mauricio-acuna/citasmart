package com.citasmart.appointmentservice.dto;

import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.model.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private String doctorSpeciality;
    private Long medicalCenterId;
    private String medicalCenterName;
    private String medicalCenterAddress;
    private Long specialityId;
    private String specialityName;
    private LocalDateTime appointmentDate;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private AppointmentType type;
    private String reason;
    private String notes;
    private String doctorNotes;
    private Boolean reminderSent;
    private String confirmationToken;
    private String cancellationReason;
    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    
    // Business logic flags
    private Boolean canBeCancelled;
    private Boolean canBeRescheduled;
    private Boolean isUpcoming;
}

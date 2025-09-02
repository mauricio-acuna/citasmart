package com.citasmart.appointmentservice.dto;

import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.model.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateDto {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Medical center ID is required")
    private Long medicalCenterId;
    
    @NotNull(message = "Speciality ID is required")
    private Long specialityId;
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Minimum duration is 15 minutes")
    @Max(value = 240, message = "Maximum duration is 240 minutes")
    private Integer durationMinutes;
    
    @NotNull(message = "Type is required")
    private AppointmentType type;
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String patientPhone;
    
    @Email(message = "Invalid email format")
    private String patientEmail;
}

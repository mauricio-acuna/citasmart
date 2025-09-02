package com.citasmart.appointmentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @Column(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @Column(name = "medical_center_id", nullable = false)
    @NotNull(message = "Medical center ID is required")
    private Long medicalCenterId;
    
    @Column(name = "speciality_id", nullable = false)
    @NotNull(message = "Speciality ID is required")
    private Long specialityId;
    
    @Column(name = "appointment_date", nullable = false)
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @Column(name = "duration_minutes", nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Minimum duration is 15 minutes")
    @Max(value = 240, message = "Maximum duration is 240 minutes")
    private Integer durationMinutes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private AppointmentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @NotNull(message = "Type is required")
    private AppointmentType type;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Doctor notes cannot exceed 1000 characters")
    private String doctorNotes;
    
    @Column(name = "patient_phone")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String patientPhone;
    
    @Column(name = "patient_email")
    @Email(message = "Invalid email format")
    private String patientEmail;
    
    // Additional fields for caching patient/doctor info
    @Transient
    private String patientName;
    
    @Transient
    private String doctorName;
    
    @Transient
    private String doctorSpeciality;
    
    @Transient
    private String medicalCenterName;
    
    @Transient
    private String medicalCenterAddress;
    
    @Transient
    private String specialityName;
    
    @Column(name = "reminder_sent")
    @Builder.Default
    private Boolean reminderSent = false;
    
    @Column(name = "confirmation_token")
    private String confirmationToken;
    
    @Column(name = "cancellation_reason")
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String cancellationReason;
    
    @Column(name = "cancelled_by")
    private String cancelledBy;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_by", nullable = false)
    @NotBlank(message = "Created by is required")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Version
    private Long version;
    
    // Relationship with appointment history
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AppointmentHistory> history;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean canBeCancelled() {
        return status != AppointmentStatus.CANCELLED 
            && status != AppointmentStatus.COMPLETED
            && appointmentDate.isAfter(LocalDateTime.now().plusHours(24));
    }
    
    public boolean canBeRescheduled() {
        return status == AppointmentStatus.SCHEDULED 
            && appointmentDate.isAfter(LocalDateTime.now().plusHours(24));
    }
    
    public boolean isUpcoming() {
        return appointmentDate.isAfter(LocalDateTime.now()) 
            && (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED);
    }
    
    public LocalDateTime getEndTime() {
        return appointmentDate.plusMinutes(durationMinutes);
    }
    
    public void setEndTime(LocalDateTime endTime) {
        // This is a calculated field, but we include setter for framework compatibility
        // The actual end time is always calculated from appointmentDate + durationMinutes
    }
}

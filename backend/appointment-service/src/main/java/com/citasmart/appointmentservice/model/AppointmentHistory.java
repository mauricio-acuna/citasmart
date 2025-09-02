package com.citasmart.appointmentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    @NotNull(message = "Appointment is required")
    private Appointment appointment;
    
    @Column(name = "action", nullable = false)
    @NotBlank(message = "Action is required")
    private String action;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "performed_by", nullable = false)
    @NotBlank(message = "Performed by is required")
    private String performedBy;
    
    @Column(name = "performed_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime performedAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private AppointmentStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private AppointmentStatus newStatus;
    
    @Column(name = "previous_date")
    private LocalDateTime previousDate;
    
    @Column(name = "new_date")
    private LocalDateTime newDate;
    
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}

package com.citasmart.appointmentservice.model;

public enum AppointmentStatus {
    SCHEDULED,    // Cita programada
    CONFIRMED,    // Cita confirmada por el paciente
    IN_PROGRESS,  // Cita en curso
    COMPLETED,    // Cita completada
    CANCELLED,    // Cita cancelada
    NO_SHOW,      // Paciente no se presentó
    RESCHEDULED   // Cita reprogramada
}

package com.citasmart.appointmentservice.repository;

import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find appointments by patient
    Page<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId, Pageable pageable);
    
    List<Appointment> findByPatientIdAndStatusIn(Long patientId, List<AppointmentStatus> statuses);
    
    // Find appointments by doctor
    Page<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId, Pageable pageable);
    
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
        Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find appointments by medical center
    Page<Appointment> findByMedicalCenterIdOrderByAppointmentDateDesc(Long medicalCenterId, Pageable pageable);
    
    // Find appointments by status
    Page<Appointment> findByStatusOrderByAppointmentDateDesc(AppointmentStatus status, Pageable pageable);
    
    List<Appointment> findByStatusAndAppointmentDateBetween(
        AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find appointments by date range
    Page<Appointment> findByAppointmentDateBetweenOrderByAppointmentDate(
        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find upcoming appointments for reminders
    @Query("SELECT a FROM Appointment a WHERE a.status IN (:statuses) " +
           "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
           "AND a.reminderSent = false")
    List<Appointment> findUpcomingAppointmentsForReminder(
        @Param("statuses") List<AppointmentStatus> statuses,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    // Check for conflicts
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'COMPLETED', 'NO_SHOW') " +
           "AND ((:startTime < a.appointmentDate + a.durationMinutes * 60000000000 " +
           "AND :endTime > a.appointmentDate) " +
           "OR (a.appointmentDate < :endTime AND a.appointmentDate + a.durationMinutes * 60000000000 > :startTime))")
    List<Appointment> findConflictingAppointments(
        @Param("doctorId") Long doctorId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    // Simplified version using method names
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND a.appointmentDate < :endTime " +
           "AND DATEADD(MINUTE, a.durationMinutes, a.appointmentDate) > :startTime")
    List<Appointment> findConflictingAppointmentsSimple(
        @Param("doctorId") Long doctorId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    // Find upcoming appointments
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :now " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);
    
    // Find appointments for reminder (24h advance)
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startTime AND :endTime " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') AND a.reminderSent = false")
    List<Appointment> findAppointmentsForReminder(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    // Statistics queries
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status " +
           "AND a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(
        @Param("status") AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.status = :status AND a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByDoctorAndStatusAndDateRange(
        @Param("doctorId") Long doctorId,
        @Param("status") AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patientId = :patientId " +
           "AND a.status = :status AND a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByPatientAndStatusAndDateRange(
        @Param("patientId") Long patientId,
        @Param("status") AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    // Find by confirmation token
    Optional<Appointment> findByConfirmationToken(String confirmationToken);
    
    // Dashboard queries
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' " +
           "AND a.appointmentDate BETWEEN :today AND :tomorrow")
    List<Appointment> findTodaysAppointments(
        @Param("today") LocalDateTime today,
        @Param("tomorrow") LocalDateTime tomorrow);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.createdAt >= :startOfDay")
    Long countTodaysCreatedAppointments(@Param("startOfDay") LocalDateTime startOfDay);
}

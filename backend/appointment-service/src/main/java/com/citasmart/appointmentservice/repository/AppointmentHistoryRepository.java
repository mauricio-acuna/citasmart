package com.citasmart.appointmentservice.repository;

import com.citasmart.appointmentservice.model.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {
    
    List<AppointmentHistory> findByAppointmentIdOrderByPerformedAtDesc(Long appointmentId);
    
    List<AppointmentHistory> findByAppointmentIdAndActionOrderByPerformedAtDesc(Long appointmentId, String action);
    
    List<AppointmentHistory> findByPerformedByOrderByPerformedAtDesc(String performedBy);
    
    @Query("SELECT h FROM AppointmentHistory h WHERE h.appointment.id = :appointmentId " +
           "AND h.performedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY h.performedAt DESC")
    List<AppointmentHistory> findByAppointmentIdAndDateRange(
        @Param("appointmentId") Long appointmentId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}

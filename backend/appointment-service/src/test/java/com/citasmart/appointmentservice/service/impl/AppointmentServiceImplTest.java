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
import com.citasmart.appointmentservice.model.AppointmentType;
import com.citasmart.appointmentservice.repository.AppointmentHistoryRepository;
import com.citasmart.appointmentservice.repository.AppointmentRepository;
import com.citasmart.appointmentservice.service.EmailService;
import com.citasmart.appointmentservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentHistoryRepository historyRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appointment;
    private AppointmentCreateRequest createRequest;
    private AppointmentUpdateRequest updateRequest;
    private UserResponse patientResponse;
    private UserResponse doctorResponse;

    @BeforeEach
    void setUp() {
        appointment = Appointment.builder()
                .id(1L)
                .patientId(100L)
                .doctorId(200L)
                .medicalCenterId(1L)
                .specialityId(1L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .durationMinutes(30)
                .status(AppointmentStatus.SCHEDULED)
                .type(AppointmentType.CONSULTATION)
                .reason("Consulta general")
                .patientPhone("+1234567890")
                .patientEmail("patient@test.com")
                .createdBy("system")
                .build();

        createRequest = AppointmentCreateRequest.builder()
                .patientId(100L)
                .doctorId(200L)
                .medicalCenterId(1L)
                .specialityId(1L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .durationMinutes(30)
                .type(AppointmentType.CONSULTATION)
                .reason("Consulta general")
                .patientPhone("+1234567890")
                .patientEmail("patient@test.com")
                .build();

        updateRequest = AppointmentUpdateRequest.builder()
                .appointmentDate(LocalDateTime.now().plusDays(2))
                .durationMinutes(45)
                .reason("Consulta especializada")
                .notes("Paciente con síntomas específicos")
                .build();

        patientResponse = UserResponse.builder()
                .id(100L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("patient@test.com")
                .phone("+1234567890")
                .build();

        doctorResponse = UserResponse.builder()
                .id(200L)
                .firstName("Dr. María")
                .lastName("González")
                .email("doctor@test.com")
                .phone("+0987654321")
                .build();
    }

    @Test
    void createAppointment_Success() {
        // Given
        when(userServiceClient.getUserById(100L)).thenReturn(patientResponse);
        when(userServiceClient.getUserById(200L)).thenReturn(doctorResponse);
        when(appointmentRepository.findConflictingAppointments(any(), any(), any())).thenReturn(Arrays.asList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When
        Appointment result = appointmentService.createAppointment(createRequest, "admin");

        // Then
        assertNotNull(result);
        assertEquals(appointment.getPatientId(), result.getPatientId());
        assertEquals(appointment.getDoctorId(), result.getDoctorId());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());

        verify(appointmentRepository).save(any(Appointment.class));
        verify(historyRepository).save(any(AppointmentHistory.class));
        verify(emailService).sendAppointmentConfirmationEmail(any(Appointment.class), any(UserResponse.class), any(UserResponse.class));
        verify(notificationService).sendAppointmentCreatedNotification(any(Appointment.class));
    }

    @Test
    void createAppointment_ConflictingAppointment_ThrowsException() {
        // Given
        Appointment conflictingAppointment = Appointment.builder()
                .id(2L)
                .doctorId(200L)
                .appointmentDate(createRequest.getAppointmentDate())
                .durationMinutes(30)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(userServiceClient.getUserById(100L)).thenReturn(patientResponse);
        when(userServiceClient.getUserById(200L)).thenReturn(doctorResponse);
        when(appointmentRepository.findConflictingAppointments(any(), any(), any()))
                .thenReturn(Arrays.asList(conflictingAppointment));

        // When & Then
        assertThrows(AppointmentConflictException.class, () -> {
            appointmentService.createAppointment(createRequest, "admin");
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(emailService, never()).sendAppointmentConfirmationEmail(any(), any(), any());
    }

    @Test
    void createAppointment_PastDate_ThrowsException() {
        // Given
        createRequest.setAppointmentDate(LocalDateTime.now().minusDays(1));

        // When & Then
        assertThrows(InvalidAppointmentDataException.class, () -> {
            appointmentService.createAppointment(createRequest, "admin");
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(userServiceClient.getUserById(100L)).thenReturn(patientResponse);
        when(userServiceClient.getUserById(200L)).thenReturn(doctorResponse);
        when(appointmentRepository.findConflictingAppointments(any(), any(), any())).thenReturn(Arrays.asList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When
        Appointment result = appointmentService.updateAppointment(1L, updateRequest, "admin");

        // Then
        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(historyRepository).save(any(AppointmentHistory.class));
        verify(emailService).sendAppointmentUpdateEmail(any(Appointment.class), any(UserResponse.class), any(UserResponse.class));
        verify(notificationService).sendAppointmentUpdatedNotification(any(Appointment.class));
    }

    @Test
    void cancelAppointment_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(userServiceClient.getUserById(100L)).thenReturn(patientResponse);
        when(userServiceClient.getUserById(200L)).thenReturn(doctorResponse);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When
        Appointment result = appointmentService.cancelAppointment(1L, "Conflicto de horarios", "admin");

        // Then
        assertNotNull(result);
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getCancelledAt());
        assertEquals("admin", result.getCancelledBy());
        assertEquals("Conflicto de horarios", result.getCancellationReason());

        verify(appointmentRepository).save(any(Appointment.class));
        verify(historyRepository).save(any(AppointmentHistory.class));
        verify(emailService).sendAppointmentCancellationEmail(any(Appointment.class), any(UserResponse.class), any(UserResponse.class));
        verify(notificationService).sendAppointmentCancelledNotification(any(Appointment.class));
    }

    @Test
    void confirmAppointment_Success() {
        // Given
        appointment.setConfirmationToken("token123");
        when(appointmentRepository.findByConfirmationToken("token123")).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When
        Appointment result = appointmentService.confirmAppointment("token123");

        // Then
        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        assertNull(result.getConfirmationToken());

        verify(appointmentRepository).save(any(Appointment.class));
        verify(historyRepository).save(any(AppointmentHistory.class));
    }

    @Test
    void getAppointmentById_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // When
        Appointment result = appointmentService.getAppointmentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
    }

    @Test
    void checkDoctorAvailability_Available_ReturnsTrue() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusMinutes(30);
        when(appointmentRepository.findConflictingAppointments(200L, startTime, endTime))
                .thenReturn(Arrays.asList());

        // When
        boolean result = appointmentService.checkDoctorAvailability(200L, startTime, endTime);

        // Then
        assertTrue(result);
    }

    @Test
    void getUpcomingAppointments_Success() {
        // Given
        when(appointmentRepository.findUpcomingAppointments(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(appointment));

        // When
        List<Appointment> result = appointmentService.getUpcomingAppointments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointment.getId(), result.get(0).getId());
    }
}
}
}

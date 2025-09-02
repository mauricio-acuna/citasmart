package com.citasmart.appointmentservice.controller;

import com.citasmart.appointmentservice.dto.AppointmentCreateRequest;
import com.citasmart.appointmentservice.dto.AppointmentUpdateRequest;
import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.model.AppointmentStatus;
import com.citasmart.appointmentservice.model.AppointmentType;
import com.citasmart.appointmentservice.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Appointment appointment;
    private AppointmentCreateRequest createRequest;
    private AppointmentUpdateRequest updateRequest;

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
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAppointment_Success() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class), any(String.class)))
                .thenReturn(appointment);

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(100L))
                .andExpect(jsonPath("$.doctorId").value(200L))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createAppointment_AsPatient_Success() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class), any(String.class)))
                .thenReturn(appointment);

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAppointment_Success() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentUpdateRequest.class), any(String.class)))
                .thenReturn(appointment);

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelAppointment_Success() throws Exception {
        // Given
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentService.cancelAppointment(eq(1L), any(String.class), any(String.class)))
                .thenReturn(appointment);

        // When & Then
        mockMvc.perform(delete("/api/appointments/1")
                        .with(csrf())
                        .param("reason", "Conflicto de horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAppointmentById_Success() throws Exception {
        // Given
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        // When & Then
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(100L));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAppointmentsByPatientId_Success() throws Exception {
        // Given
        Page<Appointment> appointmentPage = new PageImpl<>(Arrays.asList(appointment));
        when(appointmentService.getAppointmentsByPatientId(eq(100L), any()))
                .thenReturn(appointmentPage);

        // When & Then
        mockMvc.perform(get("/api/appointments/patient/100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAppointmentsByDoctorId_Success() throws Exception {
        // Given
        Page<Appointment> appointmentPage = new PageImpl<>(Arrays.asList(appointment));
        when(appointmentService.getAppointmentsByDoctorId(eq(200L), any()))
                .thenReturn(appointmentPage);

        // When & Then
        mockMvc.perform(get("/api/appointments/doctor/200")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void confirmAppointment_Success() throws Exception {
        // Given
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        when(appointmentService.confirmAppointment("token123")).thenReturn(appointment);

        // When & Then
        mockMvc.perform(post("/api/appointments/confirm/token123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void checkDoctorAvailability_Available() throws Exception {
        // Given
        when(appointmentService.checkDoctorAvailability(eq(200L), any(), any()))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/appointments/availability/doctor/200")
                        .param("startTime", "2024-01-15T10:00:00")
                        .param("endTime", "2024-01-15T10:30:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void checkDoctorAvailability_NotAvailable() throws Exception {
        // Given
        when(appointmentService.checkDoctorAvailability(eq(200L), any(), any()))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/appointments/availability/doctor/200")
                        .param("startTime", "2024-01-15T10:00:00")
                        .param("endTime", "2024-01-15T10:30:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void createAppointment_InvalidRequest_BadRequest() throws Exception {
        // Given - Invalid request with missing required fields
        AppointmentCreateRequest invalidRequest = AppointmentCreateRequest.builder()
                .patientId(null) // Missing required field
                .build();

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
}

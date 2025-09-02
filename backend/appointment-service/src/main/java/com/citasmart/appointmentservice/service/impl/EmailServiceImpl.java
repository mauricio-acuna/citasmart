package com.citasmart.appointmentservice.service.impl;

import com.citasmart.appointmentservice.model.Appointment;
import com.citasmart.appointmentservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@citasmart.com}")
    private String fromEmail;
    
    @Value("${app.name:CitaSmart}")
    private String appName;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        log.info("Sending confirmation email for appointment {}", appointment.getId());
        
        try {
            String subject = String.format("[%s] Confirmación de Cita - %s", 
                    appName, appointment.getAppointmentDate().format(DATE_FORMATTER));
            
            String content = buildConfirmationEmailContent(appointment);
            
            sendEmail(appointment.getPatientEmail(), subject, content);
            log.info("Confirmation email sent successfully for appointment {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send confirmation email for appointment {}", appointment.getId(), e);
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }

    @Override
    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        log.info("Sending reminder email for appointment {}", appointment.getId());
        
        try {
            String subject = String.format("[%s] Recordatorio de Cita - Mañana %s", 
                    appName, appointment.getAppointmentDate().format(DATE_FORMATTER));
            
            String content = buildReminderEmailContent(appointment);
            
            sendEmail(appointment.getPatientEmail(), subject, content);
            log.info("Reminder email sent successfully for appointment {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send reminder email for appointment {}", appointment.getId(), e);
            throw new RuntimeException("Failed to send reminder email", e);
        }
    }

    @Override
    @Async
    public void sendAppointmentCancellation(Appointment appointment) {
        log.info("Sending cancellation email for appointment {}", appointment.getId());
        
        try {
            String subject = String.format("[%s] Cita Cancelada - %s", 
                    appName, appointment.getAppointmentDate().format(DATE_FORMATTER));
            
            String content = buildCancellationEmailContent(appointment);
            
            sendEmail(appointment.getPatientEmail(), subject, content);
            log.info("Cancellation email sent successfully for appointment {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send cancellation email for appointment {}", appointment.getId(), e);
            throw new RuntimeException("Failed to send cancellation email", e);
        }
    }

    @Override
    @Async
    public void sendAppointmentRescheduled(Appointment appointment) {
        log.info("Sending rescheduled email for appointment {}", appointment.getId());
        
        try {
            String subject = String.format("[%s] Cita Reprogramada - Nueva Fecha: %s", 
                    appName, appointment.getAppointmentDate().format(DATE_FORMATTER));
            
            String content = buildRescheduledEmailContent(appointment);
            
            sendEmail(appointment.getPatientEmail(), subject, content);
            log.info("Rescheduled email sent successfully for appointment {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send rescheduled email for appointment {}", appointment.getId(), e);
            throw new RuntimeException("Failed to send rescheduled email", e);
        }
    }

    @Override
    @Async
    public void sendAppointmentUpdated(Appointment appointment) {
        log.info("Sending update email for appointment {}", appointment.getId());
        
        try {
            String subject = String.format("[%s] Cita Actualizada - %s", 
                    appName, appointment.getAppointmentDate().format(DATE_FORMATTER));
            
            String content = buildUpdatedEmailContent(appointment);
            
            sendEmail(appointment.getPatientEmail(), subject, content);
            log.info("Update email sent successfully for appointment {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send update email for appointment {}", appointment.getId(), e);
            throw new RuntimeException("Failed to send update email", e);
        }
    }
    
    private void sendEmail(String to, String subject, String content) {
        if (to == null || to.trim().isEmpty()) {
            log.warn("No email address provided, skipping email send");
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        
        mailSender.send(message);
        log.debug("Email sent to: {}", to);
    }
    
    private String buildConfirmationEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Estimado/a ").append(appointment.getPatientName()).append(",\n\n");
        content.append("Su cita ha sido confirmada con los siguientes detalles:\n\n");
        content.append("📅 Fecha y Hora: ").append(appointment.getAppointmentDate().format(DATE_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("🏥 Centro Médico: ").append(appointment.getMedicalCenterName()).append("\n");
        
        if (appointment.getMedicalCenterAddress() != null) {
            content.append("📍 Dirección: ").append(appointment.getMedicalCenterAddress()).append("\n");
        }
        
        content.append("🩺 Especialidad: ").append(appointment.getSpecialityName()).append("\n");
        content.append("⏱️ Duración: ").append(appointment.getDurationMinutes()).append(" minutos\n");
        
        if (appointment.getReason() != null) {
            content.append("📝 Motivo: ").append(appointment.getReason()).append("\n");
        }
        
        content.append("\n");
        
        if (appointment.getConfirmationToken() != null) {
            content.append("Para confirmar su asistencia, haga clic en el siguiente enlace:\n");
            content.append("🔗 Confirmar Cita: http://localhost:4200/appointments/confirm/").append(appointment.getConfirmationToken()).append("\n\n");
        }
        
        content.append("⚠️ IMPORTANTE:\n");
        content.append("- Llegue 15 minutos antes de su cita\n");
        content.append("- Traiga su documento de identidad\n");
        content.append("- Si necesita cancelar, hágalo con al menos 24 horas de anticipación\n\n");
        content.append("¡Gracias por confiar en ").append(appName).append("!\n\n");
        content.append("Saludos cordiales,\n");
        content.append("Equipo ").append(appName);
        
        return content.toString();
    }
    
    private String buildReminderEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Estimado/a ").append(appointment.getPatientName()).append(",\n\n");
        content.append("🔔 RECORDATORIO: Tiene una cita médica programada para mañana.\n\n");
        content.append("📅 Fecha y Hora: ").append(appointment.getAppointmentDate().format(DATE_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("🏥 Centro Médico: ").append(appointment.getMedicalCenterName()).append("\n");
        
        if (appointment.getMedicalCenterAddress() != null) {
            content.append("📍 Dirección: ").append(appointment.getMedicalCenterAddress()).append("\n");
        }
        
        content.append("\n");
        content.append("⚠️ RECORDATORIOS IMPORTANTES:\n");
        content.append("- Llegue 15 minutos antes de su cita\n");
        content.append("- Traiga su documento de identidad\n");
        content.append("- Traiga sus estudios médicos previos si los tiene\n");
        content.append("- Use mascarilla y mantenga el distanciamiento social\n\n");
        content.append("Si necesita cancelar o reprogramar su cita, por favor contacte con nosotros lo antes posible.\n\n");
        content.append("¡Nos vemos mañana!\n\n");
        content.append("Saludos cordiales,\n");
        content.append("Equipo ").append(appName);
        
        return content.toString();
    }
    
    private String buildCancellationEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Estimado/a ").append(appointment.getPatientName()).append(",\n\n");
        content.append("❌ Su cita ha sido CANCELADA.\n\n");
        content.append("Detalles de la cita cancelada:\n");
        content.append("📅 Fecha y Hora: ").append(appointment.getAppointmentDate().format(DATE_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("🏥 Centro Médico: ").append(appointment.getMedicalCenterName()).append("\n");
        
        if (appointment.getCancellationReason() != null) {
            content.append("\n📝 Motivo de cancelación: ").append(appointment.getCancellationReason()).append("\n");
        }
        
        content.append("\n");
        content.append("Si necesita programar una nueva cita, puede hacerlo a través de nuestra plataforma o contactando directamente con el centro médico.\n\n");
        content.append("Lamentamos cualquier inconveniente que esto pueda causarle.\n\n");
        content.append("Saludos cordiales,\n");
        content.append("Equipo ").append(appName);
        
        return content.toString();
    }
    
    private String buildRescheduledEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Estimado/a ").append(appointment.getPatientName()).append(",\n\n");
        content.append("📅 Su cita ha sido REPROGRAMADA.\n\n");
        content.append("Nuevos detalles de la cita:\n");
        content.append("📅 Nueva Fecha y Hora: ").append(appointment.getAppointmentDate().format(DATE_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("🏥 Centro Médico: ").append(appointment.getMedicalCenterName()).append("\n");
        content.append("🩺 Especialidad: ").append(appointment.getSpecialityName()).append("\n");
        content.append("⏱️ Duración: ").append(appointment.getDurationMinutes()).append(" minutos\n");
        
        content.append("\n");
        content.append("⚠️ RECORDATORIOS:\n");
        content.append("- Llegue 15 minutos antes de su nueva cita\n");
        content.append("- Traiga su documento de identidad\n");
        content.append("- La reprogramación es definitiva\n\n");
        content.append("¡Nos vemos en la nueva fecha!\n\n");
        content.append("Saludos cordiales,\n");
        content.append("Equipo ").append(appName);
        
        return content.toString();
    }
    
    private String buildUpdatedEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Estimado/a ").append(appointment.getPatientName()).append(",\n\n");
        content.append("ℹ️ Su cita ha sido ACTUALIZADA.\n\n");
        content.append("Detalles actuales de la cita:\n");
        content.append("📅 Fecha y Hora: ").append(appointment.getAppointmentDate().format(DATE_FORMATTER)).append("\n");
        content.append("👨‍⚕️ Doctor: ").append(appointment.getDoctorName()).append("\n");
        content.append("🏥 Centro Médico: ").append(appointment.getMedicalCenterName()).append("\n");
        content.append("🩺 Especialidad: ").append(appointment.getSpecialityName()).append("\n");
        content.append("⏱️ Duración: ").append(appointment.getDurationMinutes()).append(" minutos\n");
        
        if (appointment.getReason() != null) {
            content.append("📝 Motivo: ").append(appointment.getReason()).append("\n");
        }
        
        content.append("\n");
        content.append("Por favor, revise los detalles y asegúrese de que todo esté correcto.\n\n");
        content.append("Si tiene alguna pregunta, no dude en contactarnos.\n\n");
        content.append("Saludos cordiales,\n");
        content.append("Equipo ").append(appName);
        
        return content.toString();
    }
}

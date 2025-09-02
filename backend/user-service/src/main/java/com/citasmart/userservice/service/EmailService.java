package com.citasmart.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

/**
 * Email Service for sending notifications
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Send email verification
     * 
     * @param email User email
     * @param token Verification token
     */
    @Async
    public void sendEmailVerification(String email, String token) {
        log.info("Sending email verification to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("CitaSmart - Verificación de Email");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            String text = "Bienvenido a CitaSmart!\n\n" +
                         "Por favor, haz clic en el siguiente enlace para verificar tu email:\n" +
                         verificationUrl + "\n\n" +
                         "Si no solicitaste esta cuenta, puedes ignorar este email.\n\n" +
                         "Saludos,\n" +
                         "El equipo de CitaSmart";
            
            message.setText(text);
            mailSender.send(message);
            
            log.info("Email verification sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", email, e);
        }
    }

    /**
     * Send password reset email
     * 
     * @param email User email
     * @param token Reset token
     */
    @Async
    public void sendPasswordReset(String email, String token) {
        log.info("Sending password reset email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("CitaSmart - Restablecimiento de Contraseña");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            String text = "Hola,\n\n" +
                         "Recibimos una solicitud para restablecer tu contraseña en CitaSmart.\n\n" +
                         "Haz clic en el siguiente enlace para crear una nueva contraseña:\n" +
                         resetUrl + "\n\n" +
                         "Este enlace expirará en 1 hora por motivos de seguridad.\n\n" +
                         "Si no solicitaste este restablecimiento, puedes ignorar este email.\n\n" +
                         "Saludos,\n" +
                         "El equipo de CitaSmart";
            
            message.setText(text);
            mailSender.send(message);
            
            log.info("Password reset email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
        }
    }

    /**
     * Send welcome email
     * 
     * @param email User email
     * @param firstName User first name
     */
    @Async
    public void sendWelcomeEmail(String email, String firstName) {
        log.info("Sending welcome email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("¡Bienvenido a CitaSmart!");
            
            String text = "Hola " + firstName + ",\n\n" +
                         "¡Bienvenido a CitaSmart! Tu cuenta ha sido creada exitosamente.\n\n" +
                         "Con CitaSmart puedes:\n" +
                         "• Gestionar tus citas de manera eficiente\n" +
                         "• Acceder a una amplia gama de servicios\n" +
                         "• Recibir notificaciones y recordatorios\n" +
                         "• Gestionar tus pagos de forma segura\n\n" +
                         "Para comenzar, visita: " + frontendUrl + "\n\n" +
                         "Si tienes alguna pregunta, no dudes en contactarnos.\n\n" +
                         "Saludos,\n" +
                         "El equipo de CitaSmart";
            
            message.setText(text);
            mailSender.send(message);
            
            log.info("Welcome email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
        }
    }

    /**
     * Send account activation notification
     * 
     * @param email User email
     * @param firstName User first name
     */
    @Async
    public void sendAccountActivation(String email, String firstName) {
        log.info("Sending account activation email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("CitaSmart - Cuenta Activada");
            
            String text = "Hola " + firstName + ",\n\n" +
                         "Tu cuenta de CitaSmart ha sido activada exitosamente.\n\n" +
                         "Ya puedes acceder a todos nuestros servicios.\n\n" +
                         "Visita: " + frontendUrl + "\n\n" +
                         "Saludos,\n" +
                         "El equipo de CitaSmart";
            
            message.setText(text);
            mailSender.send(message);
            
            log.info("Account activation email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send account activation email to: {}", email, e);
        }
    }

    /**
     * Send account deactivation notification
     * 
     * @param email User email
     * @param firstName User first name
     */
    @Async
    public void sendAccountDeactivation(String email, String firstName) {
        log.info("Sending account deactivation email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("CitaSmart - Cuenta Desactivada");
            
            String text = "Hola " + firstName + ",\n\n" +
                         "Tu cuenta de CitaSmart ha sido desactivada.\n\n" +
                         "Si tienes alguna pregunta o crees que esto es un error, " +
                         "por favor contacta a nuestro equipo de soporte.\n\n" +
                         "Saludos,\n" +
                         "El equipo de CitaSmart";
            
            message.setText(text);
            mailSender.send(message);
            
            log.info("Account deactivation email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send account deactivation email to: {}", email, e);
        }
    }
}

-- V2__Insert_sample_appointment_data.sql
-- Sample data for testing and development

-- Insert sample appointments (assuming user IDs 1-10 exist from user-service)
INSERT INTO appointments (
    patient_id, patient_name, patient_phone, patient_email,
    doctor_id, doctor_name, doctor_speciality,
    medical_center_id, medical_center_name, medical_center_address,
    speciality_id, speciality_name,
    appointment_date, end_time, duration_minutes,
    status, type, reason, notes,
    created_by, updated_by
) VALUES
-- Upcoming appointments
(1, 'Juan Pérez', '+1234567890', 'juan.perez@email.com',
 101, 'Dr. María García', 'Cardiología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 1, 'Cardiología',
 CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '30 minutes', 30,
 'SCHEDULED', 'CONSULTATION', 'Consulta de control cardíaco', 'Primera visita',
 'system', 'system'),

(2, 'Ana López', '+1234567891', 'ana.lopez@email.com',
 102, 'Dr. Carlos Rodríguez', 'Neurología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 2, 'Neurología',
 CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '45 minutes', 45,
 'CONFIRMED', 'FOLLOW_UP', 'Seguimiento de tratamiento', 'Paciente con migraña crónica',
 'system', 'system'),

(3, 'Pedro Martínez', '+1234567892', 'pedro.martinez@email.com',
 103, 'Dr. Laura Fernández', 'Pediatría',
 2, 'Clínica San José', 'Calle Secundaria 456, Ciudad',
 3, 'Pediatría',
 CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '30 minutes', 30,
 'SCHEDULED', 'PREVENTIVE', 'Control de crecimiento', 'Niño de 5 años',
 'system', 'system'),

-- Today's appointments
(4, 'María González', '+1234567893', 'maria.gonzalez@email.com',
 101, 'Dr. María García', 'Cardiología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 1, 'Cardiología',
 CURRENT_DATE + TIME '09:00:00', CURRENT_DATE + TIME '09:30:00', 30,
 'CONFIRMED', 'CONSULTATION', 'Dolor en el pecho', 'Urgente',
 'system', 'system'),

(5, 'Roberto Silva', '+1234567894', 'roberto.silva@email.com',
 102, 'Dr. Carlos Rodríguez', 'Neurología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 2, 'Neurología',
 CURRENT_DATE + TIME '10:30:00', CURRENT_DATE + TIME '11:00:00', 30,
 'IN_PROGRESS', 'TREATMENT', 'Sesión de rehabilitación', 'Recuperación post-accidente',
 'system', 'system'),

-- Past appointments
(6, 'Carmen Ruiz', '+1234567895', 'carmen.ruiz@email.com',
 103, 'Dr. Laura Fernández', 'Pediatría',
 2, 'Clínica San José', 'Calle Secundaria 456, Ciudad',
 3, 'Pediatría',
 CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '30 minutes', 30,
 'COMPLETED', 'CONSULTATION', 'Consulta general', 'Niña de 3 años con fiebre',
 'system', 'system'),

(7, 'Luis Torres', '+1234567896', 'luis.torres@email.com',
 104, 'Dr. José Mendoza', 'Traumatología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 4, 'Traumatología',
 CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '45 minutes', 45,
 'COMPLETED', 'SURGERY', 'Cirugía de rodilla', 'Operación exitosa',
 'system', 'system'),

-- Cancelled appointment
(8, 'Isabel Morales', '+1234567897', 'isabel.morales@email.com',
 101, 'Dr. María García', 'Cardiología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 1, 'Cardiología',
 CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '30 minutes', 30,
 'CANCELLED', 'CONSULTATION', 'Consulta de rutina', 'Cancelada por el paciente',
 'system', 'system'),

-- No-show appointment
(9, 'Ricardo Vega', '+1234567898', 'ricardo.vega@email.com',
 102, 'Dr. Carlos Rodríguez', 'Neurología',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 2, 'Neurología',
 CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '30 minutes', 30,
 'NO_SHOW', 'CONSULTATION', 'Control neurológico', 'Paciente no se presentó',
 'system', 'system'),

-- Emergency appointment
(10, 'Sofia Herrera', '+1234567899', 'sofia.herrera@email.com',
 105, 'Dr. Antonio Jiménez', 'Urgencias',
 1, 'Hospital Central', 'Av. Principal 123, Ciudad',
 5, 'Medicina de Urgencias',
 CURRENT_TIMESTAMP + INTERVAL '6 hours', CURRENT_TIMESTAMP + INTERVAL '6 hours' + INTERVAL '60 minutes', 60,
 'SCHEDULED', 'EMERGENCY', 'Dolor abdominal agudo', 'Requiere atención inmediata',
 'system', 'system');

-- Insert some appointment history records
INSERT INTO appointment_history (
    appointment_id, previous_status, new_status, 
    change_reason, changed_by, notes
) VALUES
(2, 'SCHEDULED', 'CONFIRMED', 'Patient confirmed via email', 'PATIENT', 'Confirmation received'),
(5, 'CONFIRMED', 'IN_PROGRESS', 'Doctor started consultation', 'doctor102', 'Appointment started on time'),
(6, 'IN_PROGRESS', 'COMPLETED', 'Consultation finished', 'doctor103', 'Successful consultation'),
(7, 'SCHEDULED', 'COMPLETED', 'Surgery completed', 'doctor104', 'Surgery went well, patient recovering'),
(8, 'SCHEDULED', 'CANCELLED', 'Patient cancelled due to travel', 'PATIENT', 'Will reschedule when back'),
(9, 'SCHEDULED', 'NO_SHOW', 'Patient did not arrive', 'reception', 'Called patient, no response');

-- Update confirmation tokens for some appointments
UPDATE appointments 
SET confirmation_token = 'token-' || id || '-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::text
WHERE id IN (1, 3, 10);

-- Update cancellation details for cancelled appointment
UPDATE appointments 
SET cancellation_reason = 'Viaje de emergencia familiar',
    cancelled_by = 'PATIENT',
    cancelled_at = CURRENT_TIMESTAMP - INTERVAL '1 day'
WHERE id = 8;

-- Set reminder_sent to true for past appointments
UPDATE appointments 
SET reminder_sent = true
WHERE appointment_date < CURRENT_TIMESTAMP;

-- Add doctor notes for completed appointments
UPDATE appointments 
SET doctor_notes = CASE 
    WHEN id = 6 THEN 'Niña con fiebre leve, recetado paracetamol. Control en 1 semana.'
    WHEN id = 7 THEN 'Cirugía de rodilla exitosa. Rehabilitación en 2 semanas. Reposo absoluto.'
    ELSE doctor_notes
END
WHERE id IN (6, 7);

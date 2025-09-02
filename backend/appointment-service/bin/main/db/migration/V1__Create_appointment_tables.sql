-- V1__Create_appointment_tables.sql
-- Migration script to create appointment management tables

-- Create appointments table
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    patient_phone VARCHAR(20),
    patient_email VARCHAR(255),
    doctor_id BIGINT NOT NULL,
    doctor_name VARCHAR(200) NOT NULL,
    doctor_speciality VARCHAR(100),
    medical_center_id BIGINT NOT NULL,
    medical_center_name VARCHAR(200) NOT NULL,
    medical_center_address VARCHAR(500),
    speciality_id BIGINT NOT NULL,
    speciality_name VARCHAR(100) NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes >= 15 AND duration_minutes <= 240),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('CONSULTATION', 'FOLLOW_UP', 'EMERGENCY', 'PREVENTIVE', 'DIAGNOSTIC', 'TREATMENT', 'SURGERY', 'TELEMEDICINE')),
    reason TEXT,
    notes TEXT,
    doctor_notes TEXT,
    reminder_sent BOOLEAN DEFAULT FALSE,
    confirmation_token VARCHAR(255) UNIQUE,
    cancellation_reason TEXT,
    cancelled_by VARCHAR(100),
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version BIGINT DEFAULT 0
);

-- Create appointment_history table for audit trail
CREATE TABLE appointment_history (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    previous_status VARCHAR(20),
    new_status VARCHAR(20),
    previous_date TIMESTAMP,
    new_date TIMESTAMP,
    change_reason TEXT,
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

-- Create indexes for better performance
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX idx_appointments_medical_center_id ON appointments(medical_center_id);
CREATE INDEX idx_appointments_speciality_id ON appointments(speciality_id);
CREATE INDEX idx_appointments_appointment_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_type ON appointments(type);
CREATE INDEX idx_appointments_created_at ON appointments(created_at);
CREATE INDEX idx_appointments_confirmation_token ON appointments(confirmation_token);

-- Composite indexes for common queries
CREATE INDEX idx_appointments_doctor_date ON appointments(doctor_id, appointment_date);
CREATE INDEX idx_appointments_patient_status ON appointments(patient_id, status);
CREATE INDEX idx_appointments_status_date ON appointments(status, appointment_date);
CREATE INDEX idx_appointments_reminder ON appointments(reminder_sent, appointment_date, status);

-- Indexes for appointment_history table
CREATE INDEX idx_appointment_history_appointment_id ON appointment_history(appointment_id);
CREATE INDEX idx_appointment_history_changed_at ON appointment_history(changed_at);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_appointments_updated_at 
    BEFORE UPDATE ON appointments 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE appointments IS 'Main appointments table storing all appointment information';
COMMENT ON TABLE appointment_history IS 'Audit trail table for tracking appointment changes';

COMMENT ON COLUMN appointments.patient_id IS 'Reference to user ID from user-service';
COMMENT ON COLUMN appointments.doctor_id IS 'Reference to doctor ID from doctor-service';
COMMENT ON COLUMN appointments.medical_center_id IS 'Reference to medical center ID';
COMMENT ON COLUMN appointments.speciality_id IS 'Reference to medical speciality ID';
COMMENT ON COLUMN appointments.confirmation_token IS 'Unique token for appointment confirmation via email';
COMMENT ON COLUMN appointments.version IS 'Optimistic locking version field';

-- Create view for upcoming appointments (useful for reminders)
CREATE VIEW upcoming_appointments AS
SELECT 
    a.*,
    EXTRACT(EPOCH FROM (a.appointment_date - CURRENT_TIMESTAMP))/3600 AS hours_until_appointment
FROM appointments a
WHERE a.appointment_date > CURRENT_TIMESTAMP
  AND a.status IN ('SCHEDULED', 'CONFIRMED')
ORDER BY a.appointment_date;

-- Create view for today's appointments
CREATE VIEW todays_appointments AS
SELECT *
FROM appointments
WHERE DATE(appointment_date) = CURRENT_DATE
  AND status NOT IN ('CANCELLED')
ORDER BY appointment_date;

-- Grant permissions (adjust based on your user setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON appointments TO citasmart_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON appointment_history TO citasmart_app;
-- GRANT USAGE, SELECT ON SEQUENCE appointments_id_seq TO citasmart_app;
-- GRANT USAGE, SELECT ON SEQUENCE appointment_history_id_seq TO citasmart_app;

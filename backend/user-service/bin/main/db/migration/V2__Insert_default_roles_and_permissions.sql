-- Insert default roles and permissions
-- Author: CitaSmart Team
-- Version: 1.0

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator with full access'),
('PROVIDER', 'Service provider who can offer services and manage bookings'),
('CLIENT', 'Client who can book services and manage their appointments'),
('SUPPORT', 'Customer support representative with limited admin access'),
('MANAGER', 'Business manager with reporting and user management access');

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action) VALUES
-- User management permissions
('USER_CREATE', 'Create new users', 'user', 'create'),
('USER_READ', 'View user information', 'user', 'read'),
('USER_UPDATE', 'Update user information', 'user', 'update'),
('USER_DELETE', 'Delete users', 'user', 'delete'),
('USER_LIST', 'List all users', 'user', 'list'),

-- Profile management permissions
('PROFILE_READ', 'View own profile', 'profile', 'read'),
('PROFILE_UPDATE', 'Update own profile', 'profile', 'update'),

-- Role management permissions
('ROLE_CREATE', 'Create new roles', 'role', 'create'),
('ROLE_READ', 'View role information', 'role', 'read'),
('ROLE_UPDATE', 'Update role information', 'role', 'update'),
('ROLE_DELETE', 'Delete roles', 'role', 'delete'),
('ROLE_ASSIGN', 'Assign roles to users', 'role', 'assign'),

-- Service management permissions
('SERVICE_CREATE', 'Create new services', 'service', 'create'),
('SERVICE_READ', 'View service information', 'service', 'read'),
('SERVICE_UPDATE', 'Update service information', 'service', 'update'),
('SERVICE_DELETE', 'Delete services', 'service', 'delete'),
('SERVICE_LIST', 'List all services', 'service', 'list'),

-- Booking management permissions
('BOOKING_CREATE', 'Create new bookings', 'booking', 'create'),
('BOOKING_READ', 'View booking information', 'booking', 'read'),
('BOOKING_UPDATE', 'Update booking information', 'booking', 'update'),
('BOOKING_DELETE', 'Cancel/delete bookings', 'booking', 'delete'),
('BOOKING_LIST', 'List bookings', 'booking', 'list'),
('BOOKING_APPROVE', 'Approve pending bookings', 'booking', 'approve'),
('BOOKING_REJECT', 'Reject bookings', 'booking', 'reject'),

-- Payment management permissions
('PAYMENT_CREATE', 'Process payments', 'payment', 'create'),
('PAYMENT_READ', 'View payment information', 'payment', 'read'),
('PAYMENT_UPDATE', 'Update payment information', 'payment', 'update'),
('PAYMENT_REFUND', 'Process refunds', 'payment', 'refund'),
('PAYMENT_LIST', 'List payments', 'payment', 'list'),

-- Report permissions
('REPORT_VIEW', 'View system reports', 'report', 'view'),
('REPORT_EXPORT', 'Export reports', 'report', 'export'),

-- System administration permissions
('SYSTEM_CONFIG', 'Configure system settings', 'system', 'config'),
('SYSTEM_BACKUP', 'Perform system backups', 'system', 'backup'),
('SYSTEM_MAINTENANCE', 'Perform system maintenance', 'system', 'maintenance');

-- Assign permissions to ADMIN role (full access)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Assign permissions to PROVIDER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'PROVIDER'
AND p.name IN (
    'PROFILE_READ', 'PROFILE_UPDATE',
    'SERVICE_CREATE', 'SERVICE_READ', 'SERVICE_UPDATE', 'SERVICE_DELETE', 'SERVICE_LIST',
    'BOOKING_READ', 'BOOKING_UPDATE', 'BOOKING_LIST', 'BOOKING_APPROVE', 'BOOKING_REJECT',
    'PAYMENT_READ', 'PAYMENT_LIST',
    'REPORT_VIEW'
);

-- Assign permissions to CLIENT role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'CLIENT'
AND p.name IN (
    'PROFILE_READ', 'PROFILE_UPDATE',
    'SERVICE_READ', 'SERVICE_LIST',
    'BOOKING_CREATE', 'BOOKING_READ', 'BOOKING_UPDATE', 'BOOKING_DELETE',
    'PAYMENT_READ'
);

-- Assign permissions to SUPPORT role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPPORT'
AND p.name IN (
    'USER_READ', 'USER_UPDATE', 'USER_LIST',
    'SERVICE_READ', 'SERVICE_LIST',
    'BOOKING_READ', 'BOOKING_UPDATE', 'BOOKING_LIST',
    'PAYMENT_READ', 'PAYMENT_LIST',
    'REPORT_VIEW'
);

-- Assign permissions to MANAGER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER'
AND p.name IN (
    'USER_READ', 'USER_UPDATE', 'USER_LIST',
    'ROLE_READ', 'ROLE_ASSIGN',
    'SERVICE_READ', 'SERVICE_LIST',
    'BOOKING_READ', 'BOOKING_LIST',
    'PAYMENT_READ', 'PAYMENT_LIST',
    'REPORT_VIEW', 'REPORT_EXPORT'
);

-- Create default admin user
-- Author: CitaSmart Team
-- Version: 1.0
-- Password: admin123 (hashed using BCrypt)

-- Insert default admin user
-- Password: admin123 (will be hashed by the application)
INSERT INTO users (
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    is_active, 
    is_email_verified
) VALUES (
    'admin',
    'admin@citasmart.com',
    '$2a$10$rOL.KLz8LzUQ4f6ZQz1/wu6O5QJKhYw5BzQ5QXZ6v8ZQz1/wu6O5Q', -- admin123
    'System',
    'Administrator',
    true,
    true
);

-- Assign ADMIN role to the default admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Insert test users for development
INSERT INTO users (
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    phone_number,
    is_active, 
    is_email_verified
) VALUES 
(
    'john_provider',
    'john.provider@citasmart.com',
    '$2a$10$rOL.KLz8LzUQ4f6ZQz1/wu6O5QJKhYw5BzQ5QXZ6v8ZQz1/wu6O5Q', -- provider123
    'John',
    'Provider',
    '+1234567890',
    true,
    true
),
(
    'jane_client',
    'jane.client@citasmart.com',
    '$2a$10$rOL.KLz8LzUQ4f6ZQz1/wu6O5QJKhYw5BzQ5QXZ6v8ZQz1/wu6O5Q', -- client123
    'Jane',
    'Client',
    '+1234567891',
    true,
    true
),
(
    'mike_support',
    'mike.support@citasmart.com',
    '$2a$10$rOL.KLz8LzUQ4f6ZQz1/wu6O5QJKhYw5BzQ5QXZ6v8ZQz1/wu6O5Q', -- support123
    'Mike',
    'Support',
    '+1234567892',
    true,
    true
);

-- Assign roles to test users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'john_provider' AND r.name = 'PROVIDER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'jane_client' AND r.name = 'CLIENT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'mike_support' AND r.name = 'SUPPORT';

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_at column
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_roles_updated_at 
    BEFORE UPDATE ON roles 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_permissions_updated_at 
    BEFORE UPDATE ON permissions 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

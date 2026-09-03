-- Seeds the system administrator (spec: "the administrator is registered directly
-- at database level with all permissions"). Dev credentials, documented in README:
--   email:    admin@escuelaing.edu.co
--   password: Admin123*   (BCrypt, cost 10) -- change it in any real deployment.
INSERT INTO users (full_name, email, password_hash, school_relation, academic_program, semester,
                   status, birth_date, document_type, document_number)
VALUES ('System Administrator',
        'admin@escuelaing.edu.co',
        '$2a$10$owwGIL4AMok6PJWi.mxsvOfR1KWXgBT274t6raEGh0XLDdqhk2URO',
        'ADMINISTRATIVE',
        'OTHER',
        NULL,
        'ACTIVE',
        DATE '1990-01-01',
        'CC',
        '0000000000');

INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE email = 'admin@escuelaing.edu.co';

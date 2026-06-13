INSERT INTO role (role_name) VALUES ('ROLE_ADMIN') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO role (role_name) VALUES ('ROLE_PENDING_OWNER') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO role (role_name) VALUES ('ROLE_GYM_OWNER') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO role (role_name) VALUES ('ROLE_MEMBER') ON CONFLICT (role_name) DO NOTHING;

INSERT INTO users (full_name, user_name, email, password, phone_number, status,type, role_id)
VALUES ('Platform Admin', 'admin', 'admin@fitcloud.com', '$2a$10$GTE5JC8qoTfJ4vBfSBQK3ekBIEaGtSz4wd5Y6gx7T89ucOpiR50MS', '9999999999', 'ACTIVE', 'ADMIN',(SELECT id FROM role WHERE role_name = 'ROLE_ADMIN'))
ON CONFLICT (email) DO NOTHING;
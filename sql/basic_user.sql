-- CREATE ROLES
-- INSERT INTO role (id, description, name) VALUES (1, 'Perfil usuário', 'ROLE_USER');
-- INSERT INTO role (id, description, name) VALUES (2, 'Perfil Administrador', 'ROLE_ADMIN');
-- INSERT INTO role (id, description, name) VALUES (3, 'Perfil Operador', 'ROLE_OPERATOR');

-- CREATE ACCCESS_CARD
INSERT INTO access_card (id, account_non_expired, account_non_locked, created_at, credentials_non_expired, enabled, password, updated_at, username) VALUES (1, true, true, '2019-10-23', true, true, '$2a$10$lNU7iTnSBhW7LdUAd/YWuO5v5rnfwd8wMfvMhrwQOFfk7cJyJvsDa', '2019-10-23', 'vinicius_user');
INSERT INTO access_card (id, account_non_expired, account_non_locked, created_at, credentials_non_expired, enabled, password, updated_at, username) VALUES (153, true, true, '2019-10-23', true, true, '$2a$10$SLrYFBh/iwchBsaGPVz2yeSnS86KzpUwWcbvt50Gc5vXdoyRbE2Su', '2019-10-23', 'vinicius_admin');
INSERT INTO access_card (id, account_non_expired, account_non_locked, created_at, credentials_non_expired, enabled, password, updated_at, username) VALUES (154, true, true, '2019-10-23', true, true, '$2a$10$pnRtQY39UnQsnO6uuaEmFuQokyx6DxWPwoN6R0jlXHFJPaCUqnHOy', '2019-10-23', 'vinicius_operator');

INSERT INTO public.access_card_has_roles (access_card_id, profile_id) VALUES (1, 1);

INSERT INTO public.access_card_has_roles (access_card_id, profile_id) VALUES (153, 1);
INSERT INTO public.access_card_has_roles (access_card_id, profile_id) VALUES (153, 2);

INSERT INTO public.access_card_has_roles (access_card_id, profile_id) VALUES (154, 1);
INSERT INTO public.access_card_has_roles (access_card_id, profile_id) VALUES (154, 3);

-- CREATE USER
INSERT INTO users (authorised_access, created_at, name, number_access, type, updated_at, access_card_id) VALUES (true, '2019-10-23', 'Vinicius', 1693228998, 'Aluno', '2019-10-23', 1);
INSERT INTO users (authorised_access, created_at, name, number_access, type, updated_at, access_card_id) VALUES (true, '2019-10-23', 'ViniciusAdmin', 144412489, 'Servidor', '2019-10-23', 153);
INSERT INTO users (authorised_access, created_at, name, number_access, type, updated_at, access_card_id) VALUES (true, '2019-10-23', 'ViniciusOperador', 99101783, 'Operador', '2019-10-23', 154);

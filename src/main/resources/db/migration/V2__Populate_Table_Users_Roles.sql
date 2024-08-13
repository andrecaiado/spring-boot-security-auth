INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password, last_login) VALUES ('admin', '$2a$12$AmhuSLLeR90pgejeAUwq4eqnT7GreehMCkENZlZ7fgs.aYW5f/esu', '2024-06-19T23:43:54.378693Z');

INSERT INTO user_roles (user_id, role_id) VALUES (
    (select id from users where username = 'admin'),
    (select id from roles where name = 'ROLE_USER')
);
INSERT INTO user_roles (user_id, role_id) VALUES (
    (select id from users where username = 'admin'),
    (select id from roles where name = 'ROLE_ADMIN')
);
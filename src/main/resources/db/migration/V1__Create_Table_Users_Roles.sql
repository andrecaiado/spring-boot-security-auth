CREATE TABLE if NOT EXISTS users (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username varchar(250) NOT NULL UNIQUE,
    password varchar(250) NOT NULL,
    last_login timestamp with time zone
);

CREATE TABLE if NOT EXISTS roles (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(250) NOT NULL UNIQUE
);

CREATE TABLE if NOT EXISTS user_roles (
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);
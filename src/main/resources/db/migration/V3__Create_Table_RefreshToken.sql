CREATE TABLE if not exists refresh_token (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    expiration_date timestamp NOT NULL,
    token varchar(255) NOT NULL,
    user_id integer NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
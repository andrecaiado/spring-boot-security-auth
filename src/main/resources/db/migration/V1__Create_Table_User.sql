CREATE TABLE if not exists "user" (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username varchar(250) NOT NULL UNIQUE,
    password varchar(250) NOT NULL,
    last_login timestamp with time zone,
    roles varchar(250)
);
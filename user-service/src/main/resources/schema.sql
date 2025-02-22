CREATE TABLE users
(
    id         UUID NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255),
    oauth_id   VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);
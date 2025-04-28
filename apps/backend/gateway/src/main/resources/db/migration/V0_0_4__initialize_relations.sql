CREATE TABLE IF NOT EXISTS users
(
    id            UUID    NOT NULL,
    email         VARCHAR(255),
    password_hash VARCHAR(72),
    oauth_id      VARCHAR(255),
    name          VARCHAR(255),
    verified      BOOLEAN NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tokens
(
    id         UUID                        NOT NULL,
    token_hash VARCHAR(512)                NOT NULL,
    is_used    BOOLEAN                     NOT NULL,
    type       VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_tokens PRIMARY KEY (id)
);

DO '
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = ''uc_users_email''
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT uc_users_email UNIQUE (email);
    END IF;

    IF NOT EXISTS (SELECT 1
                   FROM pg_constraint
                   WHERE conname = ''uc_tokens_token_hash'') THEN
        ALTER TABLE tokens
            ADD CONSTRAINT uc_tokens_token_hash UNIQUE (token_hash);
    END IF;

    IF NOT EXISTS (SELECT 1
                   FROM pg_constraint
                   WHERE conname = ''fk_tokens_on_user'') THEN
        ALTER TABLE tokens
            ADD CONSTRAINT fk_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id);
    END IF;
END
';

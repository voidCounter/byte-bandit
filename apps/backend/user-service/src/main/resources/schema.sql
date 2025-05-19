-- CREATE TABLE if not exists users
-- (
--     id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
--     email         VARCHAR(255) UNIQUE NOT NULL,
--     password_hash VARCHAR(72),
--     name          VARCHAR(255),
--     oauth_id      VARCHAR(255),
--     verified      BOOLEAN             NOT NULL DEFAULT FALSE,
--     created_at    TIMESTAMP(6)                 DEFAULT CURRENT_TIMESTAMP,
--     updated_at    TIMESTAMP(6)                 DEFAULT CURRENT_TIMESTAMP,
--     CONSTRAINT users_email_unique UNIQUE (email)
-- );
--
-- CREATE TABLE if not exists tokens
-- (
--     id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
--     token_hash VARCHAR(72)  NOT NULL UNIQUE,
--     is_used    BOOLEAN      NOT NULL DEFAULT FALSE,
--     created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     expires_at TIMESTAMP(6) NOT NULL,
--     type       VARCHAR(255) NOT NULL CHECK (type IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET', 'REFRESH')),
--     user_id    UUID         NOT NULL,
--     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
-- );

CREATE INDEX if not exists idx_tokens_user_id ON tokens (user_id);
CREATE INDEX if not exists idx_tokens_type ON tokens (type);

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- to insert both user and token in a single transaction
CREATE OR REPLACE FUNCTION create_user_and_token(
    p_email TEXT,
    p_password_hash TEXT,
    p_full_name TEXT,
    p_token_hash TEXT,
    p_token_type TEXT,
    p_token_expires_at TIMESTAMP
)
    RETURNS TABLE
            (
                id          UUID,
                "fullName"  TEXT,
                email       TEXT,
                verified    BOOLEAN,
                "createdAt" TIMESTAMP
            )
AS
'
    DECLARE
        new_user_id    UUID;
        new_name       TEXT;
        new_email      TEXT;
        new_verified   BOOLEAN;
        new_created_at TIMESTAMP;
    BEGIN
        INSERT INTO users (id,
                           email,
                           password_hash,
                           oauth_id,
                           name,
                           verified,
                           created_at,
                           updated_at)
        VALUES (gen_random_uuid(),
                p_email,
                p_password_hash,
                null,
                p_full_name,
                false,
                now(),
                now())
        RETURNING users.id, users.name, users.email, users.verified, users.created_at
            INTO new_user_id, new_name, new_email, new_verified, new_created_at;

        INSERT INTO tokens (id, user_id, token_hash, type, expires_at, is_used, created_at)
        VALUES (gen_random_uuid(),
                new_user_id,
                p_token_hash,
                p_token_type,
                p_token_expires_at,
                false,
                now());

        RETURN QUERY
            SELECT new_user_id    AS id,
                   new_name       AS "fullName",
                   new_email      AS email,
                   new_verified   AS verified,
                   new_created_at AS "createdAt";
    END;
' LANGUAGE plpgsql;

-- -- trigger to automatically update of verified field in users table
-- CREATE OR REPLACE FUNCTION update_user_verified_func()
--     RETURNS TRIGGER AS
-- '
--     BEGIN
--         IF NEW.type = ''EMAIL_VERIFICATION'' AND NEW.is_used = TRUE THEN
--             UPDATE users
--             SET verified   = TRUE,
--                 updated_at = CURRENT_TIMESTAMP
--             WHERE id = NEW.user_id
--               AND verified = FALSE;
--         END IF;
--
--         RETURN NEW;
--     END;
-- ' LANGUAGE plpgsql;

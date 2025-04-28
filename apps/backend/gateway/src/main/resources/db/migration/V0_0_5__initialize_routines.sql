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

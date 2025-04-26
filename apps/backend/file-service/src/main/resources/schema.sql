CREATE OR REPLACE FUNCTION share_item_private(
    input_item_id UUID,
    shared_by_user_id UUID,
    input_shared_to_emails TEXT[],
    input_permissions TEXT[]
)
    RETURNS TEXT[] AS '
DECLARE
    is_owner BOOLEAN;
    result_permissions TEXT[];
    target_user_ids UUID[];
BEGIN

    SELECT CASE
               WHEN COUNT(*) > 0 THEN TRUE
               ELSE FALSE
               END
    INTO is_owner
    FROM file_system_items
    WHERE id = input_item_id AND owner = shared_by_user_id;

    SELECT ARRAY(
               SELECT t_user.user_id
               FROM UNNEST(input_shared_to_emails) WITH ORDINALITY AS e_email(email, email_index)
                        LEFT JOIN users_snapshot AS t_user
                                  ON t_user.email = e_email.email
               ORDER BY e_email.email_index
       )
    INTO target_user_ids;

    IF is_owner THEN
        -- If the user is the owner, grant all permissions as "ALLOWED"
        SELECT ARRAY(SELECT ''ALLOWED''
                     FROM generate_series(1, array_length(input_shared_to_emails, 1)))
        INTO result_permissions;
    ELSE
        -- Step 2: Check permission validity for each user and populate result_permissions
        result_permissions := ARRAY(
                SELECT CASE
                       WHEN EXISTS (
                           SELECT 1
                           FROM shared_items_private
                           WHERE item_id = input_item_id
                             AND shared_with = target_user_id
                             AND (
                               (input_permissions[ARRAY_POSITION(input_shared_to_emails, shared_to_email)] = ''EDITOR''
                                   AND permission IN (''EDITOR'', ''VIEWER''))
--                                (input_permissions[ARRAY_POSITION(input_shared_to_emails, shared_to_email)] = ''VIEWER''
--                                    AND permission = ''VIEWER'')
                               )
                       )
                           THEN ''ALLOWED''
                       ELSE ''NOT_ALLOWED''
                       END
                FROM UNNEST(target_user_ids) WITH ORDINALITY AS t_user(target_user_id, user_index),
                     UNNEST(input_shared_to_emails) WITH ORDINALITY AS e_email(shared_to_email, email_index),
                     UNNEST(input_permissions) WITH ORDINALITY AS e_perm(permission, perm_index)
                WHERE t_user.user_index = e_email.email_index
                  AND e_email.email_index = e_perm.perm_index
                              );
    END IF;

    FOR i IN 1..array_length(input_shared_to_emails, 1)
        LOOP
            IF target_user_ids[i] = shared_by_user_id THEN
                result_permissions[i] = ''NOT_ALLOWED'';
            END IF;
            IF result_permissions[i] = ''ALLOWED'' THEN
                INSERT
                INTO shared_items_private (id, created_at, permission, shared_with, updated_at, user_id, item_id)
                VALUES (gen_random_uuid(), NOW(), input_permissions[i], target_user_ids[i], NOW(), shared_by_user_id,
                        input_item_id)
                ON CONFLICT (item_id, shared_with) DO UPDATE
                    SET permission = EXCLUDED.permission, updated_at = NOW();
            END IF;
        END LOOP;
    RETURN result_permissions;
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION share_item_public(
    p_user_id UUID,
    p_item_id UUID,
    p_permission VARCHAR(255),
    p_password_hash VARCHAR(255) DEFAULT NULL
)
    RETURNS TABLE
            (
                public_link_id UUID,
                error_message  VARCHAR(255)
            )
    LANGUAGE plpgsql
AS
'
    DECLARE
        v_public_link_id UUID;
        v_error_message  VARCHAR(255);
        v_is_owner       BOOLEAN := FALSE;
    BEGIN
        -- Validate user_id
        IF NOT EXISTS (SELECT 1
                       FROM users_snapshot
                       WHERE user_id = p_user_id) THEN
            v_error_message := ''User does not exist.'';
            RETURN QUERY SELECT NULL::UUID      AS public_link_id,
                                v_error_message AS error_message;
            RETURN;
        END IF;

        -- Validate permission
        IF p_permission NOT IN (''VIEWER'', ''EDITOR'') THEN
            v_error_message := ''Invalid permission. Must be VIEWER or EDITOR.'';
            RETURN QUERY SELECT NULL::UUID      AS public_link_id,
                                v_error_message AS error_message;
            RETURN;
        END IF;

        -- Check if the item exists
        IF NOT EXISTS (SELECT 1
                       FROM file_system_items
                       WHERE id = p_item_id) THEN
            v_error_message := ''Item does not exist.'';
            RETURN QUERY SELECT NULL::UUID      AS public_link_id,
                                v_error_message AS error_message;
            RETURN;
        END IF;

        -- Authorization check
        IF NOT EXISTS (
            -- Check if user is the owner
            SELECT 1
            FROM file_system_items
            WHERE id = p_item_id
              AND owner = p_user_id
            UNION
            -- Check if user is a private editor
            SELECT 1
            FROM shared_items_private
            WHERE item_id = p_item_id
              AND shared_with = p_user_id
              AND permission = ''EDITOR''
            UNION
            -- Check if item is publicly shared with EDITOR permission
            SELECT 1
            FROM shared_items_public
            WHERE item_id = p_item_id
              AND permission = ''EDITOR'') THEN
            v_error_message := ''User is not authorized to share this item.'';
            RETURN QUERY SELECT NULL::UUID      AS public_link_id,
                                v_error_message AS error_message;
            RETURN;
        END IF;

        -- Check if the user is the owner
        SELECT (owner = p_user_id)
        INTO v_is_owner
        from file_system_items
        WHERE id = p_item_id;

        -- Check if a public link already exists
        IF EXISTS (SELECT 1
                   FROM shared_items_public
                   WHERE item_id = p_item_id) THEN
            -- Update existing public link
            UPDATE shared_items_public
            SET permission    = p_permission,
                password_hash = CASE
                                    WHEN v_is_owner THEN p_password_hash
                                    ELSE password_hash
                    END,
                updated_at    = CURRENT_TIMESTAMP,
                shared_by     = p_user_id
            WHERE item_id = p_item_id
            RETURNING id INTO v_public_link_id;
        ELSE
            -- Create new public link
            INSERT INTO shared_items_public (id, created_at, password_hash, permission, shared_by, updated_at, item_id)
            VALUES (gen_random_uuid(), CURRENT_TIMESTAMP, CASE WHEN v_is_owner THEN p_password_hash ELSE NULL END,
                    p_permission, p_user_id, CURRENT_TIMESTAMP,
                    p_item_id)
            RETURNING id INTO v_public_link_id;
        END IF;

        -- Return success result
        v_error_message := NULL;
        RETURN QUERY SELECT v_public_link_id AS public_link_id,
                            v_error_message  AS error_message;
    EXCEPTION
        WHEN OTHERS THEN
            v_error_message := ''An error occurred: '' || SQLERRM;
            RETURN QUERY SELECT NULL::UUID      AS public_link_id,
                                v_error_message AS error_message;
    END;
';
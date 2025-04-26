CREATE OR REPLACE PROCEDURE share_item_public(
    p_user_id UUID,
    p_item_id UUID,
    p_permission VARCHAR(255),
    p_password_hash VARCHAR(255) DEFAULT NULL,
    OUT p_public_link_id UUID DEFAULT NULL,
    OUT p_error_message VARCHAR(255))
    LANGUAGE plpgsql
AS
'
    BEGIN
        -- validate user_id
        IF NOT EXISTS (SELECT 1
                       FROM users_snapshot
                       WHERE user_id = p_user_id) THEN
            p_error_message := ''User does not exist.'';
            RETURN;
        END IF;

        -- Validate permission
        IF p_permission NOT IN (''VIEWER'', ''EDITOR'') THEN
            p_error_message := ''Invalid permission. Must be VIEWER or EDITOR.'';
            RETURN;
        END IF;

        -- Check if the item exists
        IF NOT EXISTS (SELECT 1
                       FROM file_system_items
                       WHERE id = p_item_id) THEN
            p_error_message := ''Item does not exist.'';
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
            p_error_message := ''User is not authorized to share this item.'';
            RETURN;
        END IF;

        -- Check if a public link already exists
        IF EXISTS (SELECT 1
                   FROM shared_items_public
                   WHERE item_id = p_item_id) THEN
            -- Update existing public link
            UPDATE shared_items_public
            SET permission    = p_permission,
                password_hash = p_password_hash,
                updated_at    = CURRENT_TIMESTAMP,
                shared_by     = p_user_id
            WHERE item_id = p_item_id
            RETURNING id INTO p_public_link_id;
        ELSE
            -- Create new public link
            INSERT INTO shared_items_public (id, created_at, password_hash, permission, shared_by, updated_at, item_id)
            VALUES (gen_random_uuid(), CURRENT_TIMESTAMP, p_password_hash, p_permission, p_user_id, CURRENT_TIMESTAMP,
                    p_item_id)
            RETURNING id INTO p_public_link_id;
        END IF;

        -- No error
        p_error_message := NULL;
    EXCEPTION
        WHEN OTHERS THEN
            p_error_message := ''An error occurred: '' || SQLERRM;
            p_public_link_id := NULL;
    END;
';
call share_item_public(
        '58a8c434-1ac2-4fd0-91f7-12e8688eb6d2',
        '16de7a1b-ab44-4119-adbb-6b377af2cee2',
        'EDITOR',
        NULL,
        NULL,
        'password_hash');
CREATE OR REPLACE FUNCTION get_permission_recursive(
    input_item_id UUID,
    input_user_id UUID
)
RETURNS TEXT AS '
DECLARE
    current_item_id UUID := input_item_id;
    permission_level TEXT := ''NO_ACCESS'';
    owner_check BOOLEAN;
    non_existent_user_id BOOLEAN;
    found_permission TEXT;
BEGIN

    SELECT CASE
           WHEN COUNT(*) > 0 THEN TRUE
           ELSE FALSE
           END
    INTO non_existent_user_id
    FROM users_snapshot
    WHERE user_id = input_user_id;

    IF NOT non_existent_user_id THEN
        RETURN ''NO_USER_FOUND'';
    END IF;

    WHILE current_item_id IS NOT NULL LOOP
            -- Check for ownership at the current level
            SELECT CASE
                       WHEN COUNT(*) > 0 THEN TRUE
                       ELSE FALSE
                       END
            INTO owner_check
            FROM file_system_items
            WHERE id = current_item_id AND owner = input_user_id;

            IF owner_check THEN
                RETURN ''OWNER'';
            END IF;

            -- Check for EDITOR permission in private shares at the current level
            SELECT isp.permission INTO found_permission
            FROM shared_items_private isp
            WHERE isp.item_id = current_item_id
              AND isp.shared_with = input_user_id
              AND isp.permission = ''EDITOR'';

            IF FOUND THEN
                IF permission_level = ''NO_ACCESS'' OR permission_level = ''VIEWER'' THEN
                    permission_level := ''EDITOR'';
                END IF;
            END IF;

            -- Check for EDITOR permission in public shares at the current level
            SELECT isp.permission INTO found_permission
            FROM shared_items_public isp
            WHERE isp.item_id = current_item_id
              AND isp.permission = ''EDITOR''
              AND (isp.expires_at IS NULL OR isp.expires_at > NOW());

            IF FOUND THEN
                IF permission_level = ''NO_ACCESS'' OR permission_level = ''VIEWER'' THEN
                    permission_level := ''EDITOR'';
                END IF;
            END IF;

            -- Check for VIEWER permission in private shares at the current level
            SELECT isp.permission INTO found_permission
            FROM shared_items_private isp
            WHERE isp.item_id = current_item_id
              AND isp.shared_with = input_user_id
              AND isp.permission = ''VIEWER'';

            IF FOUND THEN
                IF permission_level = ''NO_ACCESS'' THEN
                    permission_level := ''VIEWER'';
                END IF;
            END IF;

            -- Check for VIEWER permission in public shares at the current level
            SELECT isp.permission INTO found_permission
            FROM shared_items_public isp
            WHERE isp.item_id = current_item_id
              AND isp.permission = ''VIEWER''
              AND (isp.expires_at IS NULL OR isp.expires_at > NOW());

            IF FOUND THEN
                IF permission_level = ''NO_ACCESS'' THEN
                    permission_level := ''VIEWER'';
                END IF;
            END IF;

            -- Move to the parent item
            SELECT parent_id INTO current_item_id
            FROM file_system_items
            WHERE id = current_item_id;
        END LOOP;

    RETURN permission_level;
END;
' LANGUAGE plpgsql;
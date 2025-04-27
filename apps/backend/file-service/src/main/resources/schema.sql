CREATE OR REPLACE FUNCTION share_item_private(
    input_item_id UUID,
    shared_by_user_id UUID,
    input_shared_to_emails TEXT[],
    input_permissions TEXT[]
)
    RETURNS TEXT[] AS
'
    DECLARE
        is_owner           BOOLEAN;
        result_permissions TEXT[];
        target_user_ids    UUID[];
    BEGIN

        SELECT CASE
                   WHEN COUNT(*) > 0 THEN TRUE
                   ELSE FALSE
                   END
        INTO is_owner
        FROM file_system_items
        WHERE id = input_item_id
          AND owner = shared_by_user_id;

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
                               WHEN EXISTS (SELECT 1
                                            FROM shared_items_private
                                            WHERE item_id = input_item_id
                                              AND shared_with = target_user_id
                                              AND (
                                                (input_permissions[ARRAY_POSITION(input_shared_to_emails, shared_to_email)] =
                                                 ''EDITOR''
                                                    AND permission IN (''EDITOR'', ''VIEWER''))
                                                --                                (input_permissions[ARRAY_POSITION(input_shared_to_emails, shared_to_email)] = ''VIEWER''
                                                --                                    AND permission = ''VIEWER'')
                                                ))
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
                    INTO shared_items_private (id, created_at, permission, shared_with, updated_at, user_id,
                                               item_id)
                    VALUES (gen_random_uuid(), NOW(), input_permissions[i], target_user_ids[i], NOW(),
                            shared_by_user_id,
                            input_item_id)
                    ON CONFLICT (item_id, shared_with) DO UPDATE
                        SET permission = EXCLUDED.permission,
                            updated_at = NOW();
                END IF;
            END LOOP;
        RETURN result_permissions;
    END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION SHARE_ITEM_PUBLIC(
    P_USER_ID UUID,
    P_ITEM_ID UUID,
    P_PERMISSION VARCHAR(255),
    P_PERMISSION_LEVEL VARCHAR(255),
    P_PASSWORD_HASH VARCHAR(255) DEFAULT NULL,
    P_EXPIRES_AT TIMESTAMP DEFAULT NULL)
    RETURNS TABLE
            (
                PUBLIC_LINK_ID UUID,
                STATUS         VARCHAR(255)
            )
    LANGUAGE PLPGSQL
AS
'
    DECLARE
        V_PUBLIC_LINK_ID UUID;
        V_STATUS         VARCHAR(255);
        V_IS_OWNER       BOOLEAN := FALSE;
    BEGIN
        -- VALIDATE USER_ID
        IF NOT EXISTS (SELECT 1
                       FROM USERS_SNAPSHOT
                       WHERE USER_ID = P_USER_ID) THEN
            V_STATUS := ''USER DOES NOT EXIST.'';
            RETURN QUERY SELECT NULL::UUID AS PUBLIC_LINK_ID,
                                V_STATUS   AS STATUS;
            RETURN;
        END IF;

        -- VALIDATE PERMISSION
        IF P_PERMISSION NOT IN (''VIEWER'', ''EDITOR'') THEN
            V_STATUS := ''INVALID PERMISSION. MUST BE VIEWER OR EDITOR.'';
            RETURN QUERY SELECT NULL::UUID AS PUBLIC_LINK_ID,
                                V_STATUS   AS STATUS;
            RETURN;
        END IF;

        -- CHECK IF THE ITEM EXISTS
        IF NOT EXISTS (SELECT 1
                       FROM FILE_SYSTEM_ITEMS
                       WHERE ID = P_ITEM_ID) THEN
            V_STATUS := ''ITEM DOES NOT EXIST.'';
            RETURN QUERY SELECT NULL::UUID AS PUBLIC_LINK_ID,
                                V_STATUS   AS STATUS;
            RETURN;
        END IF;

        -- CHECK USER''S PERMISSION LEVEL
        IF P_PERMISSION_LEVEL NOT IN (''OWNER'', ''EDITOR'') THEN
            V_STATUS := ''USER IS NOT AUTHORIZED TO SHARE THIS ITEM.'';
            RETURN QUERY SELECT NULL::UUID AS PUBLIC_LINK_ID,
                                V_STATUS   AS STATUS;
            RETURN;
        END IF;

        -- SET OWNER FLAG
        V_IS_OWNER := (P_PERMISSION_LEVEL = ''OWNER'');

        -- CHECK IF A PUBLIC LINK ALREADY EXISTS
        IF EXISTS (SELECT 1
                   FROM SHARED_ITEMS_PUBLIC
                   WHERE ITEM_ID = P_ITEM_ID) THEN
            -- UPDATE EXISTING PUBLIC LINK
            UPDATE SHARED_ITEMS_PUBLIC
            SET PERMISSION    = P_PERMISSION,
                PASSWORD_HASH = CASE
                                    WHEN V_IS_OWNER AND P_PASSWORD_HASH IS NOT NULL THEN P_PASSWORD_HASH
                                    ELSE PASSWORD_HASH
                    END,
                EXPIRES_AT    = CASE
                                    WHEN P_EXPIRES_AT IS NOT NULL AND V_IS_OWNER THEN P_EXPIRES_AT
                                    ELSE EXPIRES_AT
                    END,
                UPDATED_AT    = CURRENT_TIMESTAMP,
                SHARED_BY     = P_USER_ID
            WHERE ITEM_ID = P_ITEM_ID
            RETURNING ID INTO V_PUBLIC_LINK_ID;
        ELSE
            -- CREATE NEW PUBLIC LINK
            INSERT INTO SHARED_ITEMS_PUBLIC (ID, CREATED_AT, PASSWORD_HASH, PERMISSION, SHARED_BY, UPDATED_AT, ITEM_ID)
            VALUES (GEN_RANDOM_UUID(), CURRENT_TIMESTAMP, CASE WHEN V_IS_OWNER THEN P_PASSWORD_HASH ELSE NULL END,
                    P_PERMISSION, P_USER_ID, CURRENT_TIMESTAMP,
                    P_ITEM_ID)
            RETURNING ID INTO V_PUBLIC_LINK_ID;
        END IF;

        -- SET SUCCESS STATUS
        IF V_IS_OWNER THEN
            IF P_PASSWORD_HASH IS NOT NULL AND P_EXPIRES_AT IS NOT NULL THEN
                V_STATUS := ''Item shared successfully with password and expiration time.'';
            ELSIF P_PASSWORD_HASH IS NOT NULL THEN
                V_STATUS := ''Item shared successfully with password.'';
            ELSIF P_EXPIRES_AT IS NOT NULL THEN
                V_STATUS := ''Item shared successfully with expiration time.'';
            ELSE
                V_STATUS := ''Item shared successfully.'';
            END IF;
        ELSE
            V_STATUS := ''Item shared successfully.'';
        END IF;

        -- RETURN SUCCESS RESULT
        RETURN QUERY SELECT V_PUBLIC_LINK_ID AS PUBLIC_LINK_ID,
                            V_STATUS         AS STATUS;
    EXCEPTION
        WHEN OTHERS THEN
            V_STATUS := ''AN ERROR OCCURRED: '' || SQLERRM;
            RETURN QUERY SELECT NULL::UUID AS PUBLIC_LINK_ID,
                                V_STATUS   AS STATUS;
    END;
';

CREATE OR REPLACE FUNCTION get_permission_recursive(
    input_item_id UUID,
    input_user_id UUID
)
    RETURNS TEXT AS
'
    DECLARE
        current_item_id  UUID := input_item_id;
        permission_level TEXT := ''NO_ACCESS'';
        owner_check      BOOLEAN;
        found_permission TEXT;
    BEGIN
        WHILE current_item_id IS NOT NULL
            LOOP
                -- Check for ownership at the current level
                SELECT CASE
                           WHEN COUNT(*) > 0 THEN TRUE
                           ELSE FALSE
                           END
                INTO owner_check
                FROM file_system_items
                WHERE id = current_item_id
                  AND owner = input_user_id;

                IF owner_check THEN
                    RETURN ''OWNER'';
                END IF;

                -- Check for EDITOR permission in private shares at the current level
                SELECT isp.permission
                INTO found_permission
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
                SELECT isp.permission
                INTO found_permission
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
                SELECT isp.permission
                INTO found_permission
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
                SELECT isp.permission
                INTO found_permission
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
                SELECT parent_id
                INTO current_item_id
                FROM file_system_items
                WHERE id = current_item_id;
            END LOOP;

        RETURN permission_level;
    END;
' LANGUAGE plpgsql;
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

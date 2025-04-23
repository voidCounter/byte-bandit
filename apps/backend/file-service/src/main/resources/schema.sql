CREATE OR REPLACE FUNCTION share_item_private(
    input_item_id UUID,
    shared_by_email TEXT,
    input_shared_to_emails TEXT[],
    input_permissions TEXT[]
)
    RETURNS TEXT[] AS '
DECLARE
    shared_by_user_id UUID;
    is_owner BOOLEAN;
    result_permissions TEXT[];
    target_user_ids UUID[];
BEGIN
    -- Check if the user (shared_by_email) is the owner of the item
    SELECT user_id
    INTO shared_by_user_id
    FROM users_snapshot
    WHERE email = shared_by_email;

    SELECT CASE
               WHEN COUNT(*) > 0 THEN TRUE
               ELSE FALSE
               END
    INTO is_owner
    FROM file_system_item
    WHERE id = input_item_id AND owner = shared_by_user_id;

    IF is_owner THEN
        -- If the user is the owner, grant all permissions as "ALLOWED"
        SELECT ARRAY(SELECT ''ALLOWED''
                     FROM generate_series(1, array_length(input_shared_to_emails, 1)))
        INTO result_permissions;
    ELSE

        SELECT ARRAY(
                       SELECT user_id
                       FROM users_snapshot
                       WHERE email = ANY(input_shared_to_emails)
               )
        INTO target_user_ids;

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
                                   AND permission IN (''EDITOR'', ''VIEWER'')) OR
                               (input_permissions[ARRAY_POSITION(input_shared_to_emails, shared_to_email)] = ''VIEWER''
                                   AND permission = ''VIEWER'')
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

    RETURN result_permissions;
END;
' LANGUAGE plpgsql;
create or replace function shared_with_user(
    input_user_id uuid
) returns table (
                    output_item_id uuid,
                    output_created_at timestamp,
                    output_updated_at timestamp,
                    output_owner_email text,
                    output_shared_by_email text,
                    output_item_type text,
                    output_is_item_password_protected boolean,
                    output_name text,
                    output_s3Url text,
                    output_mime_type text,
                    output_is_starred text,
                    output_parent_id uuid,
                    output_permission text,
                    output_children jsonb,
                    output_size bigint
                ) as '
    declare
        basic_record record;
    begin
        -- Fetch parent info
        select fsi.id as id,
               fsi.created_at as created_at,
               fsi.updated_at as updated_at,
               type,
               name,
               s3url,
               mime_type,
               parent_id,
               owner,
               size
        into basic_record
        from file_system_items as fsi join shared_items_private as sip
        on fsi.id = sip.item_id
        where sip.shared_with = input_user_id;

        loop
            output_item_id := basic_record.id;
            output_created_at := basic_record.created_at;
            output_updated_at := basic_record.updated_at;
            output_item_type := basic_record.type;
            output_name := basic_record.name;
            output_s3Url := basic_record.s3url;
            output_mime_type := basic_record.mime_type;
            output_parent_id := basic_record.parent_id;
            output_size := basic_record.size;

            -- Use helper functions
            output_owner_email := get_owner_email(basic_record.owner);
            output_shared_by_email := get_shared_by_email(basic_record.id);
            output_is_item_password_protected := is_password_protected(basic_record.id);
            output_is_starred := is_item_starred(basic_record.id, input_user_id);
            output_permission := get_permission_recursive(basic_record.id, input_user_id);
            output_children := to_jsonb(get_children(basic_record.id, input_user_id));

            return next;
        end loop;

    end;
    ' language plpgsql;
drop function if exists item_view;

create or replace function item_view(
    input_item_id uuid,
    input_user_id uuid,
    input_user_permission text
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
        select id,
               created_at,
               updated_at,
               type,
               name,
               s3url,
               mime_type,
               parent_id,
               owner,
               size
        into basic_record
        from file_system_items
        where id = input_item_id;

        if basic_record.id is null then
            raise exception ''Item not found with ID %'', input_item_id
                using errcode = ''P0002'';
        end if;

        -- Set fields
        output_item_id := basic_record.id;
        output_created_at := basic_record.created_at;
        output_updated_at := basic_record.updated_at;
        output_item_type := basic_record.type;
        output_name := basic_record.name;
        output_s3url := basic_record.s3url;
        output_mime_type := basic_record.mime_type;
        output_parent_id := basic_record.parent_id;
        output_size := basic_record.size;

        -- get owner email
        output_owner_email := get_owner_email(basic_record.owner);

        -- Shared by email
        output_shared_by_email := get_shared_by_email(input_item_id);

        -- Password protected
        output_is_item_password_protected := is_password_protected(input_item_id);

        -- Is starred
        output_is_starred := is_item_starred(input_item_id, input_user_id);

        -- Permission
        output_permission := get_permission_recursive(input_item_id, input_user_id);

        -- Children
        output_children := to_jsonB(get_children(input_item_id, input_user_id));

        return next;
    end;
' language plpgsql;
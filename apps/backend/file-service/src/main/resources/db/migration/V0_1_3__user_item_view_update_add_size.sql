drop function if exists user_items(input_user_id uuid);

create or replace function user_items(
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
        where owner = input_user_id and parent_id is null
        limit 1;

        if basic_record.id is null then
            raise exception ''No item is found for user''
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
        --         output_shared_by_email := get_shared_by_email(input_item_id);

        -- Password protected
        --         output_is_item_password_protected := is_password_protected(input_item_id);

        -- Is starred
        --         output_is_starred := is_item_starred(input_item_id, input_user_id);

        -- Permission
        --         output_permission := get_permission_recursive(input_item_id, input_user_id);

        -- Children
        output_children := to_jsonB(get_children(basic_record.id, input_user_id));

        return next;
    end;
' language plpgsql;

drop type if exists child cascade;
create type child as (
                         item_id uuid,
                         created_at timestamp,
                         updated_at timestamp,
                         owner_email text,
                         shared_by_email text,
                         item_type text,
                         is_item_password_protected boolean,
                         name text,
                         s3Url text,
                         mime_type text,
                         is_starred text,
                         parent_id uuid,
                         permission text,
                         size bigint
                     );

drop function if exists get_children(input_parent_id uuid, input_user_id uuid);

create or replace function get_children(
    input_parent_id uuid,
    input_user_id uuid
)
    returns child[] AS '
    declare
        children_list child[];
        c record;
        owner_email text;
        shared_by_email text;
        permission text;
    begin
        children_list := ARRAY[]::child[];

        for c in
            select * from file_system_items where parent_id = input_parent_id
            loop
                owner_email := get_owner_email(c.owner);
                shared_by_email := get_shared_by_email(c.id);
                permission := get_permission_recursive(
                        c.id, input_user_id
                              );

                children_list := array_append(children_list, row(
                    c.id,
                    c.created_at,
                    c.updated_at,
                    owner_email,
                    shared_by_email,
                    c.type,
                    is_password_protected(c.id),
                    c.name,
                    c.s3url,
                    c.mime_type,
                    is_item_starred(c.id, input_user_id),
                    c.parent_id,
                    permission,
                    c.size
                    )::child);
            end loop;

        return children_list;
    end;
' language plpgsql;
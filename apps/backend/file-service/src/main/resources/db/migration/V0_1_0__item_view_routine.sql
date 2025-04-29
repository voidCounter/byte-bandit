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
     permission text
);

create or replace function get_file_item(p_item_id uuid)
    returns table (
                      output_item_id uuid,
                      output_created_at timestamp,
                      output_updated_at timestamp,
                      output_item_type text,
                      output_name text,
                      output_s3Url text,
                      output_mime_type text,
                      output_parent_id uuid
                  ) AS '
begin
    return query
        select
            id,
            created_at,
            updated_at,
            type,
            name,
            s3url,
            mime_type,
            parent_id
        from
            file_system_items
        where
            id = p_item_id;
end;
' language plpgsql;

create or replace function get_owner_email(
    p_owner_id uuid
)
    returns text as $$
begin
    return (select u.email from users_snapshot u where u.user_id = p_owner_id);
end;
$$ language plpgsql;

create or replace function get_shared_by_email(
    input_item_id uuid
)
    returns text as '
begin
    return (select u.email
            from shared_items_public sp
                     join users_snapshot u on u.user_id = sp.shared_by
            where sp.item_id = input_item_id
            limit 1);
end;
' language plpgsql;

create or replace function is_password_protected(
    input_item_id uuid
)
    returns boolean as '
begin
    return exists(
        select 1
        from shared_items_public
        where item_id = input_item_id
          and password_hash is not null
    );
end;
' language plpgsql;

create or replace function is_item_starred(
    input_item_id uuid,
    input_user_id uuid
)
    returns boolean as $$
begin
    return exists(
        select 1
        from items_starred
        where item_id = input_item_id
          and user_id = input_user_id
    );
end;
$$ language plpgsql;

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
                permission
                )::child);
        end loop;

    return children_list;
end;
' language plpgsql;


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
                output_children jsonb
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
           owner
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
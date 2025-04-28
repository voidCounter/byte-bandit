CREATE TABLE file_system_items
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    size       BIGINT,
    mime_type  VARCHAR(255),
    owner      UUID         NOT NULL,
    status     VARCHAR(255) NOT NULL,
    type       VARCHAR(255) NOT NULL,
    chunks     JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    s3url      VARCHAR(255),
    parent_id  UUID,
    CONSTRAINT pk_file_system_items PRIMARY KEY (id)
);

CREATE TABLE item_views
(
    id             UUID NOT NULL,
    last_viewed_at BIGINT,
    item_id        UUID NOT NULL,
    CONSTRAINT pk_item_views PRIMARY KEY (id)
);

CREATE TABLE items_starred
(
    id      UUID NOT NULL,
    user_id UUID NOT NULL,
    item_id UUID NOT NULL,
    CONSTRAINT pk_items_starred PRIMARY KEY (id)
);

CREATE TABLE shared_items_private
(
    id          UUID                        NOT NULL,
    shared_with UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    permission  VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id     UUID                        NOT NULL,
    CONSTRAINT pk_shared_items_private PRIMARY KEY (id)
);

CREATE TABLE shared_items_public
(
    id            UUID                        NOT NULL,
    permission    VARCHAR(255)                NOT NULL,
    shared_by     UUID                        NOT NULL,
    password_hash VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at    TIMESTAMP WITHOUT TIME ZONE,
    item_id       UUID                        NOT NULL,
    CONSTRAINT pk_shared_items_public PRIMARY KEY (id)
);

CREATE TABLE users_snapshot
(
    user_id UUID         NOT NULL,
    email   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users_snapshot PRIMARY KEY (user_id)
);

ALTER TABLE shared_items_private
    ADD CONSTRAINT uc_4414a5084af3c5c5283a4b889 UNIQUE (item_id);

ALTER TABLE item_views
    ADD CONSTRAINT uc_item_views_item UNIQUE (item_id);

ALTER TABLE items_starred
    ADD CONSTRAINT uc_items_starred_item UNIQUE (item_id);

ALTER TABLE shared_items_public
    ADD CONSTRAINT uc_shared_items_public_item UNIQUE (item_id);

CREATE INDEX idx_email_index ON users_snapshot (email);

ALTER TABLE file_system_items
    ADD CONSTRAINT FK_FILE_SYSTEM_ITEMS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES file_system_items (id);

ALTER TABLE items_starred
    ADD CONSTRAINT FK_ITEMS_STARRED_ON_ITEM FOREIGN KEY (item_id) REFERENCES file_system_items (id);

ALTER TABLE item_views
    ADD CONSTRAINT FK_ITEM_VIEWS_ON_ITEM FOREIGN KEY (item_id) REFERENCES file_system_items (id);

ALTER TABLE shared_items_private
    ADD CONSTRAINT FK_SHARED_ITEMS_PRIVATE_ON_ITEM FOREIGN KEY (item_id) REFERENCES file_system_items (id);

ALTER TABLE shared_items_public
    ADD CONSTRAINT FK_SHARED_ITEMS_PUBLIC_ON_ITEM FOREIGN KEY (item_id) REFERENCES file_system_items (id);

ALTER TABLE shared_items_private
    ADD CONSTRAINT uc_shared_items_private_sharedwith_itemid UNIQUE (shared_with, item_id);

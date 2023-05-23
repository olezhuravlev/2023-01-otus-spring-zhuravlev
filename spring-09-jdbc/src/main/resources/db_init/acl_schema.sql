DROP TABLE IF EXISTS acl_entry;
DROP TABLE IF EXISTS acl_object_identity;
DROP TABLE IF EXISTS acl_class;
DROP TABLE IF EXISTS acl_sid;

CREATE TABLE IF NOT EXISTS acl_class
(
    id    bigint UNIQUE NOT NULL,
    class varchar(255)  NOT NULL,
    CONSTRAINT acl_class_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS acl_sid
(
    id        bigint UNIQUE NOT NULL,
    sid       varchar(100)  NOT NULL,
    principal smallint      NOT NULL,
    CONSTRAINT acl_sid_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id                 bigint UNIQUE NOT NULL,
    object_id_class    bigint        NOT NULL,
    object_id_identity bigint        NOT NULL,
    parent_object      bigint DEFAULT NULL,
    owner_sid          bigint DEFAULT NULL,
    entries_inheriting smallint      NOT NULL,
    CONSTRAINT acl_object_identity_pkey PRIMARY KEY (id)
);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (object_id_class) REFERENCES acl_class (id);
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id);
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (owner_sid) REFERENCES acl_sid (id);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id                  bigint UNIQUE NOT NULL,
    acl_object_identity bigint        NOT NULL,
    ace_order           integer       NOT NULL,
    sid                 bigint        NOT NULL,
    mask                integer       NOT NULL,
    granting            smallint      NOT NULL,
    audit_success       smallint      NOT NULL,
    audit_failure       smallint      NOT NULL,
    CONSTRAINT acl_entry_pkey PRIMARY KEY (id)
);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id);
ALTER TABLE acl_entry
    ADD FOREIGN KEY (sid) REFERENCES acl_sid (id);

DROP TABLE IF EXISTS acl_entry;
DROP TABLE IF EXISTS acl_object_identity;
DROP TABLE IF EXISTS acl_class;
DROP TABLE IF EXISTS acl_sid;

CREATE TABLE IF NOT EXISTS acl_class
(
    id    bigint UNIQUE NOT NULL,
    --id bigint NOT NULL DEFAULT nextval('acl_class_id_seq'::regclass),
    class varchar(255)  NOT NULL,
    class_id_type varchar(255),
    CONSTRAINT acl_class_pkey PRIMARY KEY (id)
);

--CREATE SEQUENCE IF NOT EXISTS public.acl_class_id_seq
--    INCREMENT 1
--    START 100
--    MINVALUE 100
--    MAXVALUE 9223372036854775807
--    CACHE 1
--    OWNED BY acl_class.id;
--ALTER SEQUENCE public.acl_class_id_seq OWNER TO librarydb;

CREATE TABLE IF NOT EXISTS acl_sid
(
    id    bigint UNIQUE NOT NULL,
    --id bigint NOT NULL DEFAULT nextval('acl_sid_id_seq'::regclass),
    sid       varchar(100)  NOT NULL,
    principal BOOLEAN      NOT NULL,
    CONSTRAINT acl_sid_pkey PRIMARY KEY (id)
);

--CREATE SEQUENCE IF NOT EXISTS public.acl_sid_id_seq
--    INCREMENT 1
--    START 100
--    MINVALUE 100
--    MAXVALUE 9223372036854775807
--    CACHE 1
--    OWNED BY acl_sid.id;
--ALTER SEQUENCE public.acl_sid_id_seq OWNER TO librarydb;

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id    bigint UNIQUE NOT NULL,
    --id bigint NOT NULL DEFAULT nextval('acl_object_identity_id_seq'::regclass),
    object_id_class    bigint        NOT NULL,
    object_id_identity varchar(36)        NOT NULL,
    parent_object      bigint DEFAULT NULL,
    owner_sid          bigint DEFAULT NULL,
    entries_inheriting BOOLEAN      NOT NULL,
    CONSTRAINT acl_object_identity_pkey PRIMARY KEY (id)
);

--CREATE SEQUENCE IF NOT EXISTS public.acl_object_identity_id_seq
--    INCREMENT 1
--    START 100
--    MINVALUE 100
--    MAXVALUE 9223372036854775807
--    CACHE 1
--    OWNED BY acl_object_identity.id;
--ALTER SEQUENCE public.acl_object_identity_id_seq OWNER TO librarydb;

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (object_id_class) REFERENCES acl_class (id);
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id);
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (owner_sid) REFERENCES acl_sid (id);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id    bigint UNIQUE NOT NULL,
    --id bigint NOT NULL DEFAULT nextval('acl_entry_identity_id_seq'::regclass),
    acl_object_identity bigint        NOT NULL,
    ace_order           integer       NOT NULL,
    sid                 bigint        NOT NULL,
    mask                integer       NOT NULL,
    granting            BOOLEAN      NOT NULL,
    audit_success       BOOLEAN      NOT NULL,
    audit_failure       BOOLEAN      NOT NULL,
    CONSTRAINT acl_entry_pkey PRIMARY KEY (id)
);

--CREATE SEQUENCE IF NOT EXISTS public.acl_entry_identity_id_seq
--    INCREMENT 1
--    START 100
--    MINVALUE 100
--    MAXVALUE 9223372036854775807
--    CACHE 1
--    OWNED BY acl_entry.id;
--ALTER SEQUENCE public.acl_entry_identity_id_seq OWNER TO librarydb;

ALTER TABLE acl_entry
    ADD FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id);
ALTER TABLE acl_entry
    ADD FOREIGN KEY (sid) REFERENCES acl_sid (id);

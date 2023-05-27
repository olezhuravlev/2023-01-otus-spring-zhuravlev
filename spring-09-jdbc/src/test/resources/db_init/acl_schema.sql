DROP TABLE IF EXISTS acl_entry;
DROP TABLE IF EXISTS acl_object_identity;
DROP TABLE IF EXISTS acl_class;
DROP TABLE IF EXISTS acl_sid;

CREATE TABLE IF NOT EXISTS acl_sid
(
    id        bigserial    NOT NULL PRIMARY KEY,
    sid       varchar(100) NOT NULL,
    principal BOOLEAN      NOT NULL,
    CONSTRAINT unique_uk_1 UNIQUE (sid, principal)
);

CREATE TABLE IF NOT EXISTS acl_class
(
    id    bigserial    NOT NULL PRIMARY KEY,
    class varchar(255) NOT NULL,
    CONSTRAINT unique_uk_2 UNIQUE (class)
);

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id                 bigserial   NOT NULL PRIMARY KEY,
    object_id_class    bigint      NOT NULL,
    object_id_identity varchar(36) NOT NULL,
    parent_object      bigint,
    owner_sid          bigint,
    entries_inheriting BOOLEAN     NOT NULL,
    CONSTRAINT unique_uk_3 UNIQUE (object_id_class, object_id_identity),
    CONSTRAINT foreign_fk_1 FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
    CONSTRAINT foreign_fk_2 FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
    CONSTRAINT foreign_fk_3 FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id                  bigserial NOT NULL PRIMARY KEY,
    acl_object_identity bigint    NOT NULL,
    ace_order           int       NOT NULL,
    sid                 bigint    NOT NULL,
    mask                integer   NOT NULL,
    granting            BOOLEAN   NOT NULL,
    audit_success       BOOLEAN   NOT NULL,
    audit_failure       BOOLEAN   NOT NULL,
    CONSTRAINT unique_uk_4 UNIQUE (acl_object_identity, ace_order, sid),
    CONSTRAINT foreign_fk_4 FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
    CONSTRAINT foreign_fk_5 FOREIGN KEY (sid) REFERENCES acl_sid (id)
);

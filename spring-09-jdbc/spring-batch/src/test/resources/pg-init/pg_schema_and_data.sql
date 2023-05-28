CREATE TABLE IF NOT EXISTS public.genres
(
    id   bigserial,
    name character varying(255) NOT NULL,
    CONSTRAINT genres_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.authors
(
    id   bigserial,
    name character varying(255) NOT NULL,
    CONSTRAINT authors_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.books
(
    id        bigserial,
    title     character varying(255) NOT NULL,
    author_id bigint                 NOT NULL,
    genre_id  bigint                 NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY (id),
    CONSTRAINT fk9hsvoalyniowgt8fbufidqj3x FOREIGN KEY (genre_id)
        REFERENCES public.genres (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkfjixh2vym2cvfj3ufxj91jem7 FOREIGN KEY (author_id)
        REFERENCES public.authors (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.book_comments
(
    id      bigserial,
    book_id bigint                 NOT NULL,
    text    character varying(255) NOT NULL,
    CONSTRAINT book_comments_pkey PRIMARY KEY (id)
);

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

INSERT INTO public.acl_sid(id, sid, principal) VALUES (1, 'admin', true);
INSERT INTO public.acl_class(id, class) VALUES (1, 'ru.otus.spring.model.Book');

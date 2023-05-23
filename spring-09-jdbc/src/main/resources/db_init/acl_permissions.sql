INSERT INTO acl_class (id, class)
VALUES (1, 'ru.otus.spring.model.Book');

INSERT INTO acl_sid (id, principal, sid)
VALUES (1, 1, 'admin'),
       (2, 1, 'commenter'),
       (3, 1, 'reader');

-- Book ID=1 has some restrictions.
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 2, 0),
       (2, 1, 1, NULL, 3, 0);

-- Neither 'commenter' nor 'reader' cannot read book ID=1.
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 2, 0, 0, 1, 1),
       (2, 2, 1, 3, 0, 0, 1, 1);

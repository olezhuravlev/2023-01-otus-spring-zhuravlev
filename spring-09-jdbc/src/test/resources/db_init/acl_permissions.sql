-- Principals and their SIDs.
INSERT INTO acl_sid (id, principal, sid)
VALUES (1, TRUE, 'admin'),
       (2, TRUE, 'non-admin');

-- Object classes, covered with ACL protection.
INSERT INTO acl_class (id, class)
VALUES (1, 'ru.otus.spring.model.Book');

-- Objects, covered with ACL protection.
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 1, TRUE),
       (2, 1, 2, NULL, 1, TRUE),
       (3, 1, 3, NULL, 1, TRUE);

-- ACL restrictions (ACE): Only user "Admin" can read book ID#1.
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 0, 1, 1, TRUE, TRUE, TRUE),
       (2, 1, 0, 2, 0, TRUE, TRUE, TRUE),
       (3, 2, 0, 1, 1, TRUE, TRUE, TRUE),
       (4, 2, 0, 2, 1, TRUE, TRUE, TRUE),
       (5, 3, 0, 1, 1, TRUE, TRUE, TRUE),
       (6, 3, 0, 2, 1, TRUE, TRUE, TRUE);

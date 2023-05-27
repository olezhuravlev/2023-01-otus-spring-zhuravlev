-- Principals and their SIDs.
INSERT INTO acl_sid (id, principal, sid)
VALUES (1, TRUE, 'admin'),
       (2, TRUE, 'commenter'),
       (3, TRUE, 'reader');

-- Object classes, covered with ACL protection.
INSERT INTO acl_class (id, class)
VALUES (1, 'ru.otus.spring.model.Book');

-- Objects, covered with ACL protection.
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 1, TRUE),
       (2, 1, 2, NULL, 1, TRUE),
       (3, 1, 3, NULL, 1, TRUE),
       (4, 1, 4, NULL, 1, TRUE),
       (5, 1, 5, NULL, 1, TRUE),
       (6, 1, 6, NULL, 1, TRUE),
       (7, 1, 7, NULL, 1, TRUE),
       (8, 1, 8, NULL, 1, TRUE),
       (9, 1, 9, NULL, 1, TRUE),
       (10, 1, 10, NULL, 1, TRUE);

-- ACL restrictions (ACE): Only user "Admin" can read book ID#1.
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 0, 1, 1, TRUE, TRUE, TRUE),
       (2, 1, 0, 2, 0, TRUE, TRUE, TRUE),
       (3, 1, 0, 3, 0, TRUE, TRUE, TRUE),
       (4, 2, 0, 1, 1, TRUE, TRUE, TRUE),
       (5, 2, 0, 2, 1, TRUE, TRUE, TRUE),
       (6, 2, 0, 3, 1, TRUE, TRUE, TRUE),
       (7, 3, 0, 1, 1, TRUE, TRUE, TRUE),
       (8, 3, 0, 2, 1, TRUE, TRUE, TRUE),
       (9, 3, 0, 3, 1, TRUE, TRUE, TRUE),
       (10, 4, 0, 1, 1, TRUE, TRUE, TRUE),
       (11, 4, 0, 2, 1, TRUE, TRUE, TRUE),
       (12, 4, 0, 3, 1, TRUE, TRUE, TRUE),
       (13, 5, 0, 1, 1, TRUE, TRUE, TRUE),
       (14, 5, 0, 2, 1, TRUE, TRUE, TRUE),
       (15, 5, 0, 3, 1, TRUE, TRUE, TRUE);

---- Query from BasicLookupStrategy:
-- SELECT ACL_OBJECT_IDENTITY.OBJECT_ID_IDENTITY,
--        ACL_ENTRY.ACE_ORDER,
--        ACL_OBJECT_IDENTITY.ID AS ACL_ID,
--        ACL_OBJECT_IDENTITY.PARENT_OBJECT,
--        ACL_OBJECT_IDENTITY.ENTRIES_INHERITING,
--        ACL_ENTRY.ID AS ACE_ID,
--        ACL_ENTRY.MASK,
--        ACL_ENTRY.GRANTING,
--        ACL_ENTRY.AUDIT_SUCCESS,
--        ACL_ENTRY.AUDIT_FAILURE,
--        ACL_SID.PRINCIPAL AS ACE_PRINCIPAL,
--        ACL_SID.SID AS ACE_SID,
--        ACLI_SID.PRINCIPAL AS ACL_PRINCIPAL,
--        ACLI_SID.SID AS ACL_SID,
--        ACL_CLASS.CLASS --,
--        --ACL_CLASS.CLASS_ID_TYPE
-- FROM ACL_OBJECT_IDENTITY
--          LEFT JOIN ACL_SID ACLI_SID ON ACLI_SID.ID = ACL_OBJECT_IDENTITY.OWNER_SID
--          LEFT JOIN ACL_CLASS ON ACL_CLASS.ID = ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS
--          LEFT JOIN ACL_ENTRY ON ACL_OBJECT_IDENTITY.ID = ACL_ENTRY.ACL_OBJECT_IDENTITY
--          LEFT JOIN ACL_SID ON ACL_ENTRY.SID = ACL_SID.ID
-- --WHERE ((ACL_OBJECT_IDENTITY.OBJECT_ID_IDENTITY = '1' AND ACL_CLASS.CLASS = 'ru.otus.spring.model.Book'))
-- ORDER BY ACL_OBJECT_IDENTITY.OBJECT_ID_IDENTITY ASC, ACL_ENTRY.ACE_ORDER ASC;

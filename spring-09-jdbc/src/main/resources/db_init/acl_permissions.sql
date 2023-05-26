INSERT INTO acl_class (id, class, class_id_type)
VALUES (1, 'ru.otus.spring.model.Book', 'bigserial');

-- Sid
INSERT INTO acl_sid (id, principal, sid)
VALUES (1, TRUE, 'admin'),
       (2, TRUE, 'commenter'),
       (3, TRUE, 'reader');

-- Acl: Book ID=1 has 2 restrictions (specified in table `acl_entry`).
-- ObjectIdentity
-- AclService
-- LookupStrategy
-- MutableAclService
-- AclEntryVoter
-- AclEntryAfterInvocationProvider
-- AclEntryAfterInvocationCollectionFilteringProvider
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 1, FALSE);

-- AccessControlEntry (ACE): Neither 'commenter' nor 'reader' cannot read book ID=1.
-- Permission
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 1, 31, TRUE, TRUE, TRUE);

---- Query to show imposed restrictions:
--SELECT s.id, s.sid, s.principal,
--        e.id rule_id, e.ace_order, e.mask, e.granting, e.audit_success, e.audit_failure,
--		c.class, i.object_id_identity class_obj_id, i.parent_object, i.entries_inheriting
--FROM acl_sid s
--          LEFT JOIN acl_entry e ON s.id=e.sid
--		  LEFT JOIN acl_object_identity i ON e.id=i.id
--		  LEFT JOIN acl_class c ON i.object_id_class=c.id
--ORDER BY s.id, e.id, e.ace_order;

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
--        ACL_CLASS.CLASS, ACL_CLASS.CLASS_ID_TYPE
-- FROM ACL_OBJECT_IDENTITY
--          LEFT JOIN ACL_SID ACLI_SID ON ACLI_SID.ID = ACL_OBJECT_IDENTITY.OWNER_SID
--          LEFT JOIN ACL_CLASS ON ACL_CLASS.ID = ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS
--          LEFT JOIN ACL_ENTRY ON ACL_OBJECT_IDENTITY.ID = ACL_ENTRY.ACL_OBJECT_IDENTITY
--          LEFT JOIN ACL_SID ON ACL_ENTRY.SID = ACL_SID.ID
-- WHERE ((ACL_OBJECT_IDENTITY.OBJECT_ID_IDENTITY = '1' AND ACL_CLASS.CLASS = 'ru.otus.spring.model.Book'))
-- ORDER BY ACL_OBJECT_IDENTITY.OBJECT_ID_IDENTITY ASC, ACL_ENTRY.ACE_ORDER ASC

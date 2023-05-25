INSERT INTO acl_class (id, class, class_id_type)
VALUES (1, 'ru.otus.spring.model.Book', 'bigint');

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
VALUES (1, 1, 1, NULL, 1, FALSE),
       (2, 1, 1, NULL, 1, FALSE),
       (3, 1, 1, NULL, 2, FALSE),
       (4, 1, 1, NULL, 3, FALSE);

-- AccessControlEntry (ACE): Neither 'commenter' nor 'reader' cannot read book ID=1.
-- Permission
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 1,  0, TRUE, TRUE, TRUE),
       (2, 1, 2, 1, 0, TRUE, TRUE, TRUE),
       (3, 1, 1, 2,  7, TRUE, TRUE, TRUE),
       (4, 1, 1, 3,  1, TRUE, TRUE, TRUE);

---- Query to show imposed restrictions:
--SELECT s.id, s.sid, s.principal,
--        e.id rule_id, e.ace_order, e.mask, e.granting, e.audit_success, e.audit_failure,
--		c.class, i.object_id_identity class_obj_id, i.parent_object, i.entries_inheriting
--FROM acl_sid s
--          LEFT JOIN acl_entry e ON s.id=e.sid
--		  LEFT JOIN acl_object_identity i ON e.id=i.id
--		  LEFT JOIN acl_class c ON i.object_id_class=c.id
--ORDER BY s.id, e.id, e.ace_order;

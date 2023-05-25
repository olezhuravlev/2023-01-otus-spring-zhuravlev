INSERT INTO acl_class (id, class)
VALUES (1, 'ru.otus.spring.model.Book');

-- Sid
INSERT INTO acl_sid (id, principal, sid)
VALUES (1, 1, 'admin'),
       (2, 1, 'commenter'),
       (3, 1, 'reader');

-- Acl: Book ID=1 has 2 restrictions (specified in table `acl_entry`).
-- ObjectIdentity
-- AclService
-- LookupStrategy
-- MutableAclService
-- AclEntryVoter
-- AclEntryAfterInvocationProvider
-- AclEntryAfterInvocationCollectionFilteringProvider
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 1, 0),
       (2, 1, 1, NULL, 2, 0),
       (3, 1, 1, NULL, 3, 0);

-- AccessControlEntry (ACE): Neither 'commenter' nor 'reader' cannot read book ID=1.
-- Permission
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 1,  1, 1, 1, 1),
       (2, 1, 2, 1, 30, 1, 1, 1),
       (3, 1, 1, 2,  7, 1, 1, 1),
       (4, 1, 1, 3,  1, 1, 1, 1);

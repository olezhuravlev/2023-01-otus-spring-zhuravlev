package ru.otus.spring.config;

import lombok.AllArgsConstructor;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("authorizationLogic")
@AllArgsConstructor
public class AuthorizationLogic {

    private AclService aclService;
    private PermissionFactory permissionFactory;

    public boolean hasPermission(Long id, String classCanonicalName, String... permission) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Sid> sids = List.of(new PrincipalSid(authentication));
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(classCanonicalName, id);

        try {
            Acl acl = aclService.readAclById(objectIdentity, sids);
            List<Permission> requiredMethodPermissions = Arrays.stream(permission).map(permissionFactory::buildFromName).toList();
            return acl.isGranted(requiredMethodPermissions, sids, false);
        } catch (final NotFoundException e) {
            return false;
        }
    }
}

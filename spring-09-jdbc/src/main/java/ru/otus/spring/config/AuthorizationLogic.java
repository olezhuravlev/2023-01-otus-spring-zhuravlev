package ru.otus.spring.config;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authorizationLogic")
public class AuthorizationLogic {

    // @PreAuthorize: Invoked BEFORE AuthorizationManager<MethodInvocation>
    // @PostAuthorize: Invoked AFTER AuthorizationManager<MethodInvocation>
    // @PostFilter: Invoked for each returning item.
    // @PreFilter: Not invoked - Filter target must be a collection, array, map or stream type!
    public boolean decide(SecurityExpressionRoot expressionRoot, Long id, String classCanonicalName, Authentication authentication) {
        return true;
    }
}

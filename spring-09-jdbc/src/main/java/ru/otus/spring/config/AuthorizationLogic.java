package ru.otus.spring.config;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.stereotype.Component;

@Component("myAuth")
public class AuthorizationLogic {

    // @PreAuthorize: Invoked BEFORE AuthorizationManager<MethodInvocation>
    // @PostAuthorize: Invoked AFTER AuthorizationManager<MethodInvocation>
    // @PostFilter: Invoked for each returning item.
    // @PreFilter: Not invoked - Filter target must be a collection, array, map or stream type!
    public boolean decide(MethodSecurityExpressionOperations obj1) {
        return true; // Invoked @PostFilter("@myAuth.decide(#root)")!
    }

    public boolean decide(Object obj1, Object obj2) {
        return true;
    }

    public boolean decide(Object obj1, Object obj2, Object obj3) {
        return true;
    }

    public boolean decide(Object obj1, Object obj2, Object obj3, Object obj4) {
        return true;
    }
}

package ru.otus.spring.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclConfig {

    @Bean
    static RoleHierarchy roleHierarchy() {

        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        // Having role 'Admin' leads to having all READ-permissions.
        roleHierarchy.setHierarchy("ROLE_ADMIN > permission:read");
        return roleHierarchy;
    }

    // We expose MethodSecurityExpressionHandler using a static method to ensure
    // that Spring publishes it before it initializes Spring Security’s method security @Configuration classes!
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public AuthorizationManager<MethodInvocation> preAuthorizationManager() {

        return new AuthorizationManager<>() {
            @Override
            public void verify(Supplier<Authentication> authentication, MethodInvocation object) {
                AuthorizationManager.super.verify(authentication, object);
            }

            @Override
            public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation object) {
                // Invoked AFTER AuthorizationLogic!
                return new AuthorizationDecision(true); // Invoked and works with @PreAuthorize("hasRole('ROLE_ADMIN')")!
            }
        };
    }

    @Bean
    public AuthorizationManager<MethodInvocationResult> postAuthorizationManager() {

        return new AuthorizationManager<>() {

            @Override
            public void verify(Supplier<Authentication> authentication, MethodInvocationResult object) {
                AuthorizationManager.super.verify(authentication, object);
            }

            @Override
            public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocationResult object) {
                // Invoked BEFORE AuthorizationLogic!
                return new AuthorizationDecision(true); // Invoked and works with @PostAuthorize("hasRole('ROLE_ADMIN')")!
            }
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preAuthorize() {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(preAuthorizationManager());
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor postAuthorize() {
        return AuthorizationManagerAfterMethodInterceptor.postAuthorize(postAuthorizationManager());
    }


    //AuthorizationManager API instead of metadata sources, config attributes, decision managers, and voters.
//    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
//    @Autowired
//    private DataSource dataSource;
//
//    @Bean
//    public EhCacheBasedAclCache aclCache() {
//        return new EhCacheBasedAclCache(
//                Objects.requireNonNull(aclEhCacheFactoryBean().getObject()),
//                permissionGrantingStrategy(),
//                aclAuthorizationStrategy()
//        );
//    }
//
//    @Bean
//    public EhCacheFactoryBean aclEhCacheFactoryBean() {
//        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
//        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
//        ehCacheFactoryBean.setCacheName("aclCache");
//        return ehCacheFactoryBean;
//    }
//
//    @Bean
//    public EhCacheManagerFactoryBean aclCacheManager() {
//        return new EhCacheManagerFactoryBean();
//    }
//
//    @Bean
//    public PermissionGrantingStrategy permissionGrantingStrategy() {
//        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
//    }
//
//    @Bean
//    public AclAuthorizationStrategy aclAuthorizationStrategy() {
//        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_EDITOR"));
//    }
//
//    @Bean
//    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
//        expressionHandler.setPermissionEvaluator(permissionEvaluator);
//        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
//        return expressionHandler;
//    }
//
//    @Bean
//    public LookupStrategy lookupStrategy() {
//        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
//    }
//
//    @Bean
//    public JdbcMutableAclService aclService() {
//        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
//    }
}


//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//public class AclMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
//
//    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
//    @Autowired
//    MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;
//
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        return defaultMethodSecurityExpressionHandler;
//    }
//
//}

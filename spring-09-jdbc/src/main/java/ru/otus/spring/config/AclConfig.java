package ru.otus.spring.config;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class AclConfig {

    // IMPORTANT: Setting to use field "class_id_type varchar(255) NOT NULL" in table "acl_schema"!
    private static final boolean ACL_CLASS_ID_SUPPORTED = false;

    private final DataSource dataSource;

    public AclConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
    public MethodSecurityExpressionHandler expressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        handler.setPermissionEvaluator(new AclPermissionEvaluator(aclService()));
        handler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return handler;
    }

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public ConcurrentMapCache userCacheBackend() {
        return new ConcurrentMapCache("aclCache");
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public SpringCacheBasedAclCache aclCache() {
        return new SpringCacheBasedAclCache(userCacheBackend(), permissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    // Provides high-performance ACL retrieval capabilities
    @Bean
    public LookupStrategy lookupStrategy() {
        BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
        // Adds field "CLASS_ID_TYPE" to:
        // SELECT query from "ACL_OBJECT_IDENTITY -> ACL_SID -> ACL_CLASS -> ACL_ENTRY".
        lookupStrategy.setAclClassIdSupported(ACL_CLASS_ID_SUPPORTED);
        return lookupStrategy;
    }

    // Provides mutator capabilities.
    @Bean
    public JdbcMutableAclService aclService() {
        JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
        // Adds field "CLASS_ID_TYPE" to the queries:
        // SELECT query from "ACL_OBJECT_IDENTITY, ACL_CLASS",
        // INSERT query into "ACL_CLASS".
        aclService.setAclClassIdSupported(ACL_CLASS_ID_SUPPORTED);
        return aclService;
    }
}

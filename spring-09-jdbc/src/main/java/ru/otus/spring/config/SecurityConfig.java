package ru.otus.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collection;

@Configuration
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_COMMENTER = "ROLE_COMMENTER";
    private static final String COMMENT_URI = "COMMENTS";
    private static final String RM_SECRET = "rm_secret";
    private static final int RM_TOKEN_LIFETIME_SEC = 600;
    private static final String LOGIN_PAGE_URL = "/login";
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> authorizationManager) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.DELETE).access(authorizationManager)
                        .requestMatchers(HttpMethod.PUT).access(authorizationManager)
                        .requestMatchers(HttpMethod.POST).access(authorizationManager)
                        .anyRequest().authenticated())
                .rememberMe().key(RM_SECRET).tokenValiditySeconds(RM_TOKEN_LIFETIME_SEC)
                .and()
                .formLogin(form -> form.loginPage(LOGIN_PAGE_URL)
                        .usernameParameter(USERNAME_PARAMETER)
                        .passwordParameter(PASSWORD_PARAMETER)
                        .permitAll())
                .logout().permitAll();

        return http.build();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> authorizationManager() {

        return (supplier, context) -> {

            Authentication authentication = supplier.get();
            HttpServletRequest httpServletRequest = context.getRequest();
            String method = httpServletRequest.getMethod();

            // Only "Admin" allowed to delete something.
            // Only "Admin" and "Commenter" allowed to put/post something.
            // "Commenter" allowed to put/post comments only.
            AuthorizationDecision decision;
            if ("DELETE".equals(method)) {
                decision = new AuthorizationDecision(containsAnyRole(authentication, ROLE_ADMIN));
            } else if ("PUT".equals(method) || "POST".equals(method)) {
                if (isCommentsRequest(httpServletRequest)) {
                    decision = new AuthorizationDecision(containsAnyRole(authentication, ROLE_ADMIN, ROLE_COMMENTER));
                } else {
                    decision = new AuthorizationDecision(containsAnyRole(authentication, ROLE_ADMIN));
                }
            } else {
                decision = new AuthorizationDecision(true);
            }

            return decision;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    private boolean isCommentsRequest(HttpServletRequest httpServletRequest) {
        String requestURI = httpServletRequest.getRequestURI();
        return requestURI.toUpperCase().contains(COMMENT_URI);
    }

    private boolean containsAnyRole(Authentication authentication, String... roles) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(a -> Arrays.stream(roles).toList().contains(a.getAuthority()));
    }
}

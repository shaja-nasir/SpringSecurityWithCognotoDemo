package com.spring.cognito.springcognitodemoapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    private final CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

    @Value("${aws.cognito.logoutUrl}")
    private String logoutUrl;
    @Value("${aws.cognito.logout.success.redirectUrl}")
    private String logoutRedirectUrl;
    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;


    public SpringSecurityConfig(CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler) {
        this.customizeAuthenticationSuccessHandler = customizeAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers("/").permitAll()
                .requestMatchers("/admin/*").hasRole("ADMIN")
                .requestMatchers("/user/*").hasAnyRole("USER","ADMIN").anyRequest().authenticated())
                .oauth2Login(oauth ->
                        oauth.redirectionEndpoint(endPoint -> endPoint.baseUri("/login/oauth2/code/cognito"))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(userAuthoritiesMapper()))
                        .successHandler(customizeAuthenticationSuccessHandler))
                .logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer.logoutSuccessHandler(
                            new CustomLogoutHandler(logoutUrl, logoutRedirectUrl, clientId)
                    );
                });
            return  httpSecurity.build();

    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (grantedAuthorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            try {
                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) new ArrayList<>(grantedAuthorities).get(0);
                mappedAuthorities = ((ArrayList<?>) oidcUserAuthority.getAttributes().get("cognito:groups")).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet());
            }catch (Exception e) {
                System.out.println("Not Authorized!");
                System.out.println(e.getMessage());
            }
            return mappedAuthorities;
        };
    }
}

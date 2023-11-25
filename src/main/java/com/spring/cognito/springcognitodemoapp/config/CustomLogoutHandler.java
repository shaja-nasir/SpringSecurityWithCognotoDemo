package com.spring.cognito.springcognitodemoapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CustomLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private final String logoutUrl;
    private final String logoutRedirectUrl;
    private final String clientId;

    public CustomLogoutHandler(String logoutUrl, String logoutRedirectUrl, String clientId) {
        this.logoutUrl = logoutUrl;
        this.logoutRedirectUrl = logoutRedirectUrl;
        this.clientId = clientId;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return UriComponentsBuilder
                .fromUri(URI.create(logoutUrl))
                .queryParam("client_id", clientId)
                .queryParam("logout_uri")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}

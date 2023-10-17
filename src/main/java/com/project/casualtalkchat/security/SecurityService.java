package com.project.casualtalkchat.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SecurityService {

    private final AuthenticationContext authenticationContext;

    public Optional<CustomUserDetails> getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(CustomUserDetails.class);
    }

    public void logout() {
        authenticationContext.logout();
    }
}

package com.project.casualtalkchat.security;

import com.project.casualtalkchat.login_page.UserLoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    UserLoginService userLoginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";

        UserDetails userDetails = userLoginService.loadUserByUsername(email);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (userDetails == null) {
            log.debug("User with such email address not found.");
            throw new BadCredentialsException("1000");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.debug("Password doesn't match.");
            throw new BadCredentialsException("1000");
        }
        if (!userDetails.isEnabled()) {
            log.debug("User is disabled.");
            throw new DisabledException("1001");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, List.of(new SimpleGrantedAuthority("USER")));
    }
}

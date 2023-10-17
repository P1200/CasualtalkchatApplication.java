package com.project.casualtalkchat.security;

import com.project.casualtalkchat.login_page.UserEntity;
import com.project.casualtalkchat.login_page.UserLoginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationManagerTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String PASSWORD = "Lolek123*";
    public static final String WRONG_PASSWORD = "lolek12";
    public static final String BAD_CREDENTIALS_EXCEPTION_CODE = "1000";
    public static final String DISABLED_EXCEPTION_CODE = "1001";

    @Mock
    private UserLoginService userLoginService;

    @Test
    void shouldReturnAuthenticationToken() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(userLoginService.loadUserByUsername(anyString()))
                .thenReturn(prepareCustomUserDetails(true));

        //When
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //Then
        CustomUserDetails expected = prepareCustomUserDetails(true);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        assertEquals(expected.getId(), principal.getId());
        assertEquals(expected.getEmail(), principal.getEmail());
        assertEquals(expected.getUsername(), principal.getUsername());
        assertEquals(expected.isEnabled(), principal.isEnabled());
        assertEquals(expected.getAuthorities().toString(), principal.getAuthorities().toString());
        assertTrue(passwordEncoder.matches(PASSWORD, principal.getPassword()),
                "Encrypted password should match raw password.");
        assertNull(authentication.getCredentials());
        assertEquals(List.of(new SimpleGrantedAuthority("USER")).toString(), authentication.getAuthorities().toString());
    }

    @Test
    void shouldThrowExceptionWhenUserDetailsAreNull() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD);
        when(userLoginService.loadUserByUsername(anyString())).thenReturn(null);

        //When
        Exception exception = assertThrows(BadCredentialsException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals(BAD_CREDENTIALS_EXCEPTION_CODE, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, WRONG_PASSWORD);
        when(userLoginService.loadUserByUsername(anyString()))
                .thenReturn(prepareCustomUserDetails(true));

        //When
        Exception exception = assertThrows(BadCredentialsException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals(BAD_CREDENTIALS_EXCEPTION_CODE, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotConfirmed() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD);
        when(userLoginService.loadUserByUsername(anyString()))
                .thenReturn(prepareCustomUserDetails(false));

        //When
        Exception exception = assertThrows(DisabledException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals(DISABLED_EXCEPTION_CODE, exception.getMessage());
    }

    private CustomUserDetails prepareCustomUserDetails(boolean isAccountConfirmed) {
        UserEntity userEntity = UserEntity.builder()
                                            .id(USER_ID)
                                            .username(USERNAME)
                                            .email(EMAIL)
                                            .password(PASSWORD)
                                            .isAccountConfirmed(isAccountConfirmed)
                                            .build();
        return CustomUserDetails.build(userEntity);
    }
}
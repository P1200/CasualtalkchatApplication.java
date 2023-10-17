package com.project.casualtalkchat.security;

import com.project.casualtalkchat.login_page.UserEntity;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String PASSWORD = "Lolek123*";

    @Mock
    private AuthenticationContext authenticationContext;

    @Test
    void shouldGetAuthenticatedUser() {
        //Given
        SecurityService service = new SecurityService(authenticationContext);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(authenticationContext.getAuthenticatedUser(CustomUserDetails.class))
                .thenReturn(Optional.of(prepareCustomUserDetails()));

        //When
        Optional<CustomUserDetails> authenticatedUser = service.getAuthenticatedUser();

        //Then
        CustomUserDetails expected = prepareCustomUserDetails();
        CustomUserDetails principal = authenticatedUser.get();

        assertEquals(expected.getId(), principal.getId());
        assertEquals(expected.getEmail(), principal.getEmail());
        assertEquals(expected.getUsername(), principal.getUsername());
        assertEquals(expected.isEnabled(), principal.isEnabled());
        assertEquals(expected.getAuthorities().toString(), principal.getAuthorities().toString());
        assertTrue(passwordEncoder.matches(PASSWORD, principal.getPassword()),
                "Encrypted password should match raw password.");
    }

    @Test
    void shouldThrowNoSuchElementException() {
        //Given
        SecurityService service = new SecurityService(authenticationContext);
        when(authenticationContext.getAuthenticatedUser(CustomUserDetails.class))
                .thenReturn(Optional.empty());

        //When
        Optional<CustomUserDetails> authenticatedUser = service.getAuthenticatedUser();

        //Then
        assertThrows(NoSuchElementException.class, authenticatedUser::get);
    }

    @Test
    void shouldLogout() {
        //Given
        SecurityService service = new SecurityService(authenticationContext);

        //When
        service.logout();

        //Then
        verify(authenticationContext, times(1)).logout();
    }

    private CustomUserDetails prepareCustomUserDetails() {
        UserEntity userEntity = UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .isAccountConfirmed(true)
                .build();
        return CustomUserDetails.build(userEntity);
    }
}
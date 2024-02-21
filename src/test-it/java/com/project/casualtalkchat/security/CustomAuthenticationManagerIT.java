package com.project.casualtalkchat.security;

import com.project.casualtalkchat.login_page.UserEntity;
import com.project.casualtalkchat.login_page.UserLoginService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomAuthenticationManagerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String USER_ID = "2ce910d8-d9c5-4b59-ba9c-01bd7c7ab47e";
    public static final String USERNAME = "Jessica";
    public static final String EMAIL = "jessy@gmail.com";
    public static final String NOT_CONFIRMED_EMAIL = "pablo@gmail.com";
    public static final String NOT_EXISTING_EMAIL = "ssy@gmail.com";
    public static final String PASSWORD = "Password123*";
    public static final String WRONG_PASSWORD = "lolek12";
    public static final String BAD_CREDENTIALS_EXCEPTION_CODE = "1000";
    public static final String DISABLED_EXCEPTION_CODE = "1001";

    @Autowired
    private UserLoginService userLoginService;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/custom-authentication-manager-it.sql")
    @Transactional
    void shouldReturnAuthenticationToken() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //When
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //Then
        CustomUserDetails expected = prepareCustomUserDetails();
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
    @Sql("/db-scripts/custom-authentication-manager-it.sql")
    @Transactional
    void shouldThrowExceptionWhenUserNotFound() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(NOT_EXISTING_EMAIL, PASSWORD);

        //When
        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals("User not found with email: " + NOT_EXISTING_EMAIL, exception.getMessage());
    }

    @Test
    @Sql("/db-scripts/custom-authentication-manager-it.sql")
    @Transactional
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(EMAIL, WRONG_PASSWORD);

        //When
        Exception exception = assertThrows(BadCredentialsException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals(BAD_CREDENTIALS_EXCEPTION_CODE, exception.getMessage());
    }

    @Test
    @Sql("/db-scripts/custom-authentication-manager-it.sql")
    @Transactional
    void shouldThrowExceptionWhenUserIsNotConfirmed() {
        //Given
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(userLoginService);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(NOT_CONFIRMED_EMAIL, PASSWORD);

        //When
        Exception exception = assertThrows(DisabledException.class,
                () -> authenticationManager.authenticate(authenticationToken));

        //Then
        assertEquals(DISABLED_EXCEPTION_CODE, exception.getMessage());
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
package com.project.casualtalkchat.login_page;

import com.project.casualtalkchat.security.CustomUserDetails;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserLoginServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String USER_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    private static final String NOT_ENABLED_USER_ID = "64550bdc-9162-4efa-8304-2430a0ac8cd9";
    public static final String USERNAME = "Paul";
    public static final String NOT_ENABLED_USER_USERNAME = "Pablo";
    public static final String USER_EMAIL = "paul@gmail.com";
    public static final String NOT_ENABLED_USER_EMAIL = "pablo@gmail.com";
    public static final String PASSWORD = "Password123*";
    public static final String AVATAR_NAME = "aea2bac3c4933907e6be863efccd01b9.jpeg";

    @Autowired
    private UserRepository repository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/user-login-service-it.sql")
    @Transactional
    void shouldGetUserDetailsByUsernameWhenUserIsEnabled() {
        //Given
        UserLoginService service = new UserLoginService(repository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //When
        CustomUserDetails userDetails = (CustomUserDetails) service.loadUserByUsername(USER_EMAIL);

        //Then
        assertEquals(USERNAME, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(PASSWORD, userDetails.getPassword()), "Password should be correctly encrypted.");
        assertEquals(USER_EMAIL, userDetails.getEmail());
        assertEquals(USER_ID, userDetails.getId());
        assertEquals(AVATAR_NAME, userDetails.getAvatar());
        assertTrue(userDetails.isEnabled(), "User should be confirmed and enabled.");
        assertFalse(userDetails.isAccountNonExpired(), "Account shouldn't be expired.");
        assertFalse(userDetails.isAccountNonLocked(), "Account shouldn't be locked.");
        assertFalse(userDetails.isCredentialsNonExpired(), "Credentials shouldn't be expired.");
    }

    @Test
    @Sql("/db-scripts/user-login-service-it.sql")
    @Transactional
    void shouldGetUserDetailsByUsernameWhenUserIsNotEnabled() {
        //Given
        UserLoginService service = new UserLoginService(repository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //When
        CustomUserDetails userDetails = (CustomUserDetails) service.loadUserByUsername(NOT_ENABLED_USER_EMAIL);

        //Then
        assertEquals(NOT_ENABLED_USER_USERNAME, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(PASSWORD, userDetails.getPassword()), "Password should be correctly encrypted.");
        assertEquals(NOT_ENABLED_USER_EMAIL, userDetails.getEmail());
        assertEquals(NOT_ENABLED_USER_ID, userDetails.getId());
        assertEquals(AVATAR_NAME, userDetails.getAvatar());
        assertFalse(userDetails.isEnabled(), "User should be confirmed and enabled.");
        assertFalse(userDetails.isAccountNonExpired(), "Account shouldn't be expired.");
        assertFalse(userDetails.isAccountNonLocked(), "Account shouldn't be locked.");
        assertFalse(userDetails.isCredentialsNonExpired(), "Credentials shouldn't be expired.");
    }
}
package com.project.casualtalkchat.password_change_page;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChangePasswordServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String PROPER_USER_ID_AND_TOKEN_STRING =
            "a2c43ade-742e-40d6-b0b3-a933c29a9d7d/77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String NOT_EXISTING_USER_ID_AND_TOKEN_STRING = "abc1-ger348r/77c7eef7-16e4-938e4fb219ad";
    private static final String USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4\\77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final Long TOKEN_ID = 1L;
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    public static final String PASSWORD = "Password123*";
    public static final String NEW_PASSWORD = "Lolek123*";

    @Autowired
    private AccountRecoveryTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/user-change-password-service-it.sql")
    @Transactional
    void shouldGetTokenEntity() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(PROPER_USER_ID_AND_TOKEN_STRING);

        //Then
        AccountRecoveryTokenEntity expected = prepareRecoveryTokenEntity();
        assertEquals(expected.getToken(), tokenEntity.getToken());
        assertEquals(expected.getId(), tokenEntity.getId());
        UserEntity user = tokenEntity.getUser();
        assertTrue(passwordEncoder.matches(PASSWORD, user.getPassword()),"Password should be correctly encrypted.");
        assertEquals(expected.getUser()
                             .getId(), user.getId());
    }

    @Test
    @Sql("/db-scripts/user-change-password-service-it.sql")
    @Transactional
    void shouldNotGetTokenEntityWhenUserIdAndTokenDoNotMatchAnyDatabaseRow() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(NOT_EXISTING_USER_ID_AND_TOKEN_STRING);

        //Then
        assertNull(tokenEntity, "Should return null when database row not found.");
    }

    @Test
    @Sql("/db-scripts/user-change-password-service-it.sql")
    @Transactional
    void shouldNotGetTokenEntityWhenUserIdAndTokenInWrongFormat() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT);

        //Then
        assertNull(tokenEntity, "Should return null when method argument in wrong format.");
    }

    @Test
    @Sql("/db-scripts/user-change-password-service-it.sql")
    @Transactional
    void shouldSavePasswordAndDeleteToken() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //When
        service.acceptPasswordChange(prepareUserEntity(), prepareRecoveryTokenEntity());

        //Then
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, userRepository.getReferenceById(USER_ID).getPassword()),"Password should be correctly changed.");
        assertFalse(tokenRepository.findById(TOKEN_ID).isPresent());
    }

    private AccountRecoveryTokenEntity prepareRecoveryTokenEntity() {
        return AccountRecoveryTokenEntity.builder()
                .id(TOKEN_ID)
                .token(TOKEN)
                .user(prepareUserEntity())
                .build();
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .password(NEW_PASSWORD)
                .build();
    }
}
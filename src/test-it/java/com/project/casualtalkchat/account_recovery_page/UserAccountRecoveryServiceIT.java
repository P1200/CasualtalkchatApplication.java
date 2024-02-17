package com.project.casualtalkchat.account_recovery_page;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.UUID;
import java.util.function.Supplier;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static com.helger.commons.mock.CommonsAssert.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserAccountRecoveryServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String OLD_TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String NEW_TOKEN = "b2893d41-0aba-41d2-83f1-fcfd2fb99ec3";
    private static final String USER_WITH_TOKEN_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    private static final String USER_WITHOUT_TOKEN_ID = "64550bdc-9162-4efa-8304-2430a0ac8cd9";
    public static final String USERNAME = "Paul";
    public static final String EMAIL = "paul@gmail.com";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRecoveryTokenRepository tokenRepository;
    @Mock
    private Supplier uuidSupplier;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql(scripts = "/db-scripts/account-recovery-service-it.sql")
    @Transactional
    void shouldGetOldRecoveryTokenWhenFound() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);

        //When
        String recoveryToken = service.getRecoveryTokenForUser(prepareUserWithTokenEntity());

        //Then
        assertEquals(recoveryToken, OLD_TOKEN);
    }

    @Test
    @Sql(scripts = "/db-scripts/account-recovery-service-it.sql")
    @Transactional
    void shouldGetNewRecoveryTokenAndSaveItToDatabaseWhenOldTokenNotFound() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);
        ReflectionTestUtils.setField(service, "randomUUID", uuidSupplier);
        when(uuidSupplier.get()).thenReturn(UUID.fromString(NEW_TOKEN));

        //When
        String recoveryToken = service.getRecoveryTokenForUser(prepareUserWithoutTokenEntity());

        //Then
        assertEquals(recoveryToken, NEW_TOKEN);
        assertNotEquals(tokenRepository.findByUserId(USER_WITHOUT_TOKEN_ID), NEW_TOKEN);
    }

    @Test
    @Sql(scripts = "/db-scripts/account-recovery-service-it.sql")
    @Transactional
    void shouldGetUser() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);

        //When
        UserEntity user = service.getUser(EMAIL);

        //Then
        assertEquals(prepareUserWithTokenEntity(), user);
    }

    private UserEntity prepareUserWithTokenEntity() {
        return UserEntity.builder()
                            .id(USER_WITH_TOKEN_ID)
                            .username(USERNAME)
                            .email(EMAIL)
                            .build();
    }

    private UserEntity prepareUserWithoutTokenEntity() {
        return UserEntity.builder()
                .id(USER_WITHOUT_TOKEN_ID)
                .username(USERNAME)
                .email(EMAIL)
                .build();
    }
}
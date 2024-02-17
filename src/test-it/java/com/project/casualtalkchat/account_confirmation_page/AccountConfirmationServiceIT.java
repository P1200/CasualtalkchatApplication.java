package com.project.casualtalkchat.account_confirmation_page;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class AccountConfirmationServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String PROPER_USER_ID_AND_TOKEN_STRING =
            "a2c43ade-742e-40d6-b0b3-a933c29a9d7d/77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String NOT_EXISTING_USER_ID_AND_TOKEN_STRING = "abc1-ger348r/77c7eef7-16e4-938e4fb219ad";
    private static final String USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4\\77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final Long TOKEN_ID = 12L;
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";

    @Autowired
    private AccountConfirmationService service;

    @Autowired
    private VerificationTokenRepository tokenRepository;

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
    @Sql(scripts = "/db-scripts/account-confirmation-service-it.sql")
    @Transactional
    void userShouldBeConfirmed() {
        //Given

        //When
        boolean isUserConfirmed = service.confirmUser(PROPER_USER_ID_AND_TOKEN_STRING);

        //Then
        assertTrue(isUserConfirmed, "User should be confirmed.");
        UserEntity userEntity = userRepository.getReferenceById(USER_ID);
        assertTrue(userEntity.isAccountConfirmed(),
                "User should be confirmed in database.");
        assertFalse(tokenRepository.findById(TOKEN_ID).isPresent(), "Token should be deleted.");
    }

    @Test
    @Sql(scripts = "/db-scripts/account-confirmation-service-it.sql")
    @Transactional
    void userShouldNotBeConfirmedWhenUserIdAndTokenDoNotMatchAnyDatabaseRow() {
        //Given

        //When
        boolean isUserConfirmed = service.confirmUser(NOT_EXISTING_USER_ID_AND_TOKEN_STRING);

        //Then
        assertFalse(isUserConfirmed, "User shouldn't be confirmed");
        UserEntity userEntity = userRepository.getReferenceById(USER_ID);
        assertFalse(userEntity.isAccountConfirmed(),
                "User should not be confirmed in database.");
        assertTrue(tokenRepository.findById(TOKEN_ID).isPresent(), "Token should not be deleted.");
    }

    @Test
    @Sql(scripts = "/db-scripts/account-confirmation-service-it.sql")
    @Transactional
    void userShouldNotBeConfirmedWhenUserIdAndTokenInWrongFormat() {
        //Given

        //When
        boolean isUserConfirmed = service.confirmUser(USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT);

        //Then
        assertFalse(isUserConfirmed, "User shouldn't be confirmed");
        UserEntity userEntity = userRepository.getReferenceById(USER_ID);
        assertFalse(userEntity.isAccountConfirmed(),
                "User should not be confirmed in database.");
        assertTrue(tokenRepository.findById(TOKEN_ID).isPresent(), "Token should not be deleted.");
    }
}
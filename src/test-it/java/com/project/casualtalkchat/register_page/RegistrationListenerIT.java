package com.project.casualtalkchat.register_page;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RegistrationListenerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String PASSWORD = "Lolek123*";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String APP_URL ="localhost:8080/account-recovery";

    @Autowired
    private UserRegistrationService service;
    @Autowired
    private MessageSource messages;
    @Spy
    private JavaMailSender mailSender;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/registration-listener-it.sql")
    @Transactional
    void shouldSendMail() {
        //Given
        RegistrationListener registrationListener = new RegistrationListener(service, messages, mailSender);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        //When
        registrationListener.onApplicationEvent(new OnRegistrationCompleteEvent(prepareUserEntity(), APP_URL, LOCALE));

        //Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
}
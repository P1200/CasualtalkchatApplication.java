package com.project.casualtalkchat.account_recovery_page;

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
class AccountRecoveryListenerIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    public static final String EMAIL = "paul@gmail.com";
    public static final String NON_EXISTING_EMAIL = "nonexisting@gmail.com";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String APP_URL ="localhost:8080/account-recovery";

    @Autowired
    private UserAccountRecoveryService service;
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
    @Sql(scripts = "/db-scripts/account-recovery-listener-it.sql")
    @Transactional
    void shouldSendMail() {
        //Given
        AccountRecoveryListener accountRecoveryListener = new AccountRecoveryListener(service, messages, mailSender);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        //When
        accountRecoveryListener.onApplicationEvent(new OnAccountRecoveryRequestedEvent(EMAIL, APP_URL, LOCALE));

        //Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @Sql(scripts = "/db-scripts/account-recovery-listener-it.sql")
    @Transactional
    void shouldNotSendMailWhenUserNotFound() {
        //Given
        AccountRecoveryListener accountRecoveryListener = new AccountRecoveryListener(service, messages, mailSender);

        //When
        accountRecoveryListener.onApplicationEvent(new OnAccountRecoveryRequestedEvent(NON_EXISTING_EMAIL, APP_URL, LOCALE));

        //Then
        verify(mailSender, times(0)).send(any(MimeMessage.class));
    }
}
package com.project.casualtalkchat.account_recovery_page;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRecoveryListenerTest {

    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String APP_URL ="localhost:8080/account-recovery";
    public static final String ACCOUNT_RECOVERY_EMAIL_HEADER_ID = "account-recovery-email-header-text";
    public static final String ACCOUNT_RECOVERY_EMAIL_MAIN_TEXT_WELCOME_ID = "account-recovery-email-main-text-welcome";
    public static final String ACCOUNT_RECOVERY_EMAIL_MAIN_TEXT_MAIN_ID = "account-recovery-email-main-text-main";
    public static final String ACCOUNT_RECOVERY_EMAIL_BUTTON_TEXT_ID = "account-recovery-email-button-text";
    public static final String ACCOUNT_RECOVERY_EMAIL_END_TEXT_ID = "account-recovery-email-end-text";
    public static final String ACCOUNT_RECOVERY_HEADER = "Account recovery";
    public static final String ACCOUNT_RECOVERY_HELLO = "Hello";
    public static final String ACCOUNT_RECOVERY_MAIN_TEXT = "Click to a button below to change your account password.";
    public static final String ACCOUNT_RECOVERY_BUTTON_TEXT = "Change my password";
    public static final String ACCOUNT_RECOVERY_END_TEXT = "If you didn't request this email, there's nothing to worry about - you can safely ignore it.";

    @Mock
    private UserAccountRecoveryService service;
    @Mock
    private MessageSource messages;
    @Spy
    private JavaMailSender mailSender;

    @Test
    void shouldSendMail() { //TODO replace with integration test
        //Given
        AccountRecoveryListener accountRecoveryListener = new AccountRecoveryListener(service, messages, mailSender);
        when(service.getUser(anyString())).thenReturn(prepareUserEntity());
        when(service.getRecoveryTokenForUser(any(UserEntity.class))).thenReturn(TOKEN);

        when(messages.getMessage(ACCOUNT_RECOVERY_EMAIL_HEADER_ID, null, LOCALE))
                .thenReturn(ACCOUNT_RECOVERY_HEADER);
        when(messages.getMessage(ACCOUNT_RECOVERY_EMAIL_MAIN_TEXT_WELCOME_ID, null, LOCALE))
                .thenReturn(ACCOUNT_RECOVERY_HELLO);
        when(messages.getMessage(ACCOUNT_RECOVERY_EMAIL_MAIN_TEXT_MAIN_ID, null, LOCALE))
                .thenReturn(ACCOUNT_RECOVERY_MAIN_TEXT);
        when(messages.getMessage(ACCOUNT_RECOVERY_EMAIL_BUTTON_TEXT_ID, null, LOCALE))
                .thenReturn(ACCOUNT_RECOVERY_BUTTON_TEXT);
        when(messages.getMessage(ACCOUNT_RECOVERY_EMAIL_END_TEXT_ID, null, LOCALE))
                .thenReturn(ACCOUNT_RECOVERY_END_TEXT);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        //When
        accountRecoveryListener.onApplicationEvent(new OnAccountRecoveryRequestedEvent(EMAIL, APP_URL, LOCALE));

        //Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldNotSendMailWhenUserNotFound() {
        //Given
        AccountRecoveryListener accountRecoveryListener = new AccountRecoveryListener(service, messages, mailSender);
        when(service.getUser(anyString())).thenReturn(null);

        //When
        accountRecoveryListener.onApplicationEvent(new OnAccountRecoveryRequestedEvent(EMAIL, APP_URL, LOCALE));

        //Then
        verify(mailSender, times(0)).send(any(MimeMessage.class));
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .build();
    }
}
package com.project.casualtalkchat.account_recovery_page;

import com.project.casualtalkchat.emailing.EmailViewTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountRecoveryListener implements ApplicationListener<OnAccountRecoveryRequestedEvent> {

    @Autowired
    private UserAccountRecoveryService service;
    @Autowired
    private MessageSource messages;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(@NonNull OnAccountRecoveryRequestedEvent event) {

        UserEntity user = service.getUser(event.getEmail());

        if (user != null) {
            try {
                sendAccountRecoveryEmail(event, user);
                log.info("Account recovery email has been sent to: " + event.getEmail() + ".");
            } catch (MessagingException e) {
                throw new RuntimeException(e); //TODO
            }
        } else {
            log.debug("User with email: " + event.getEmail() + " doesn't exist.");
        }
    }

    private void sendAccountRecoveryEmail(OnAccountRecoveryRequestedEvent event, UserEntity user) throws MessagingException {

        String token = service.createRecoveryTokenForUser(user);

        mailSender.send(getMimeMessage(event, token, user));
    }

    private MimeMessage getMimeMessage(OnAccountRecoveryRequestedEvent event, String token,
                                       UserEntity user) throws MessagingException {

        String recipientAddress = user.getEmail();
        String subject = "Account Recovery";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(getEmailContent(user, token, event), true);
        helper.setTo(recipientAddress);
        helper.setSubject(subject);
        return mimeMessage;
    }

    private String getEmailContent(UserEntity user, String token, OnAccountRecoveryRequestedEvent event) {
        String recoveryUrl =
                "http://localhost:8080" + event.getAppUrl() + "/password-change/" + user.getId() + "/" + token;

        String headerText = messages.getMessage("account-recovery-email-header-text", null, event.getLocale());
        String welcomeText = messages.getMessage("account-recovery-email-main-text-welcome", null, event.getLocale());
        String mainText = messages.getMessage("account-recovery-email-main-text-main", null, event.getLocale());
        String buttonText = messages.getMessage("account-recovery-email-button-text", null, event.getLocale());
        String endText = messages.getMessage("account-recovery-email-end-text", null, event.getLocale());

        String mainEmailPart = welcomeText + " " + user.getUsername() + "! " + mainText;

        return new EmailViewTemplate(headerText, mainEmailPart, recoveryUrl, buttonText, endText).getElement()
                                                                                                    .toString();
    }
}

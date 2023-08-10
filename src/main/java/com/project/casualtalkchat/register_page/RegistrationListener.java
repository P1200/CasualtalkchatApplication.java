package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.emailing.EmailViewTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserRegistrationService service;
    @Autowired
    private MessageSource messages;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (MessagingException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException {
        UserEntity user = event.getUser();
        String token = UUID.randomUUID()
                            .toString();
        service.saveVerificationTokenForUser(user, token);

        mailSender.send(getMimeMessage(event, user, token));
    }

    private MimeMessage getMimeMessage(OnRegistrationCompleteEvent event, UserEntity user, String token) throws MessagingException {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(getEmailContent(user, token, event), true);
        helper.setTo(recipientAddress);
        helper.setSubject(subject);
        return mimeMessage;
    }

    private String getEmailContent(UserEntity user, String token, OnRegistrationCompleteEvent event) {
        String confirmationUrl
                = event.getAppUrl() + "/registration-confirm/" + user.getId() + "/" + token;
        confirmationUrl = "http://localhost:8080" + confirmationUrl;
        String headerText = messages.getMessage("confirmation-email-header-text", null, event.getLocale());
        String welcomeText = messages.getMessage("confirmation-email-main-text-welcome", null, event.getLocale());
        String mainText = messages.getMessage("confirmation-email-main-text-main", null, event.getLocale());
        String buttonText = messages.getMessage("confirmation-email-button-text", null, event.getLocale());
        String endText = messages.getMessage("confirmation-email-end-text", null, event.getLocale());

        String mainEmailPart = welcomeText + " " + user.getUsername() + "! " + mainText;

        return new EmailViewTemplate(headerText, mainEmailPart, confirmationUrl, buttonText, endText).getElement()
                                                                                                    .toString();
    }
}

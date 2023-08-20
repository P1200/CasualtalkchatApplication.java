package com.project.casualtalkchat.account_recovery_page;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnAccountRecoveryRequestedEvent extends ApplicationEvent {
    private String appUrl;
    private String email;
    private Locale locale;

    public OnAccountRecoveryRequestedEvent(String email, String appUrl, Locale locale) {
        super(email);

        this.email = email;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}

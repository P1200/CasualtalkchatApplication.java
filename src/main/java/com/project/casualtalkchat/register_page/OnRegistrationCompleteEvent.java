package com.project.casualtalkchat.register_page;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private UserEntity user;
    private Locale locale;

    public OnRegistrationCompleteEvent(UserEntity user, String appUrl, Locale locale) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}

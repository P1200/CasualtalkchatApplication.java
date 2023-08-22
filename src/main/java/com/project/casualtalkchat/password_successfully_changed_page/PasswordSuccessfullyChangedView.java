package com.project.casualtalkchat.password_successfully_changed_page;

import com.project.casualtalkchat.common.OperationStatusView;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("password-successfully-changed")
@AnonymousAllowed
public class PasswordSuccessfullyChangedView extends VerticalLayout {

    private static final String SUCCESS_HEADER_TEXT = "Your account has been successfully recovered";
    private static final String CHECK_YOUR_INBOX_TEXT = "Log in now";
    private static final String VERIFY_YOUR_ACCOUNT_TEXT = "We've changed your password. " +
            "Now you can log in to your CasualTalk account with new password.";
    private static final Icon STATUS_ICON = new Icon(VaadinIcon.CHECK_CIRCLE);
    private static final String STATUS_ICON_COLOR = "green";

    public PasswordSuccessfullyChangedView(SecurityService securityService) {
        this.getStyle()
                .setBackground("#f3f4f6");
        this.setPadding(false);

        STATUS_ICON.setColor(STATUS_ICON_COLOR);

        add(new OperationStatusView(securityService, STATUS_ICON, SUCCESS_HEADER_TEXT, CHECK_YOUR_INBOX_TEXT, VERIFY_YOUR_ACCOUNT_TEXT));
    }
}

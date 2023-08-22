package com.project.casualtalkchat.account_recovery_requested_page;

import com.project.casualtalkchat.common.OperationStatusView;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("account-recovery-successfully-requested")
@AnonymousAllowed
public class AccountRecoveryRequestedView extends VerticalLayout {

    private static final String SUCCESS_HEADER_TEXT = "Account recovery has been successfully requested";
    private static final String CHECK_YOUR_INBOX_TEXT = "Check your inbox now";
    private static final String VERIFY_YOUR_ACCOUNT_TEXT = "We've sent you a account recovery link to your email address. " +
            "Please check your email inbox and recover your account.";
    private static final Icon STATUS_ICON = VaadinIcon.CHECK_CIRCLE.create(); //TODO check if it really helps with 'Can't move a node from one state tree to another' exception
    private static final String STATUS_ICON_COLOR = "green";

    public AccountRecoveryRequestedView(SecurityService securityService) {
        this.getStyle()
                .setBackground("#f3f4f6");
        this.setPadding(false);

        STATUS_ICON.setColor(STATUS_ICON_COLOR);

        add(new OperationStatusView(securityService, STATUS_ICON, SUCCESS_HEADER_TEXT, CHECK_YOUR_INBOX_TEXT,
                VERIFY_YOUR_ACCOUNT_TEXT));
    }
}

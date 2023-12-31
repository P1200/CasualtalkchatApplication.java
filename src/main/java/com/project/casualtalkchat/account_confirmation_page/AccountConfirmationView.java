package com.project.casualtalkchat.account_confirmation_page;

import com.project.casualtalkchat.common.OperationStatusView;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("registration-confirm")
@AnonymousAllowed
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class AccountConfirmationView extends VerticalLayout implements HasUrlParameter<String> {

    private static final String SUCCESS_HEADER_TEXT = "Your account has been successfully confirmed";
    private static final String FAILURE_HEADER_TEXT = "Your account has not been successfully confirmed";
    private static final String OPERATION_SUCCEEDED_TEXT = "Operation has been succeeded";
    private static final String OPERATION_FAILED_TEXT = "Operation has been failed";
    private static final String ACCOUNT_VERIFIED_TEXT = "Your account has been successfully confirmed. " +
            "Now you can enjoy using our website and chat with every!";
    private static final String ACCOUNT_NOT_VERIFIED_TEXT = "Your account has not been successfully confirmed. " +
            "Please check if your verification code is valid and correctly typed.";
    private static final Icon FAILURE_STATUS_ICON = new Icon("lumo", "cross");
    private static final Icon SUCCESS_STATUS_ICON = VaadinIcon.CHECK_CIRCLE.create();
    private static final String FAILURE_STATUS_ICON_COLOR = "red";
    private static final String SUCCESS_STATUS_ICON_COLOR = "green";

    private final AccountConfirmationService confirmationService;
    private final SecurityService securityService;

    public AccountConfirmationView(AccountConfirmationService confirmationService, SecurityService securityService) {
        this.securityService = securityService;
        this.confirmationService = confirmationService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String userIdAndToken) {

        this.getStyle()
                .setBackground("#f3f4f6");
        this.setPadding(false);

        boolean isUserConfirmed = confirmationService.confirmUser(userIdAndToken);

        if (isUserConfirmed) {
            SUCCESS_STATUS_ICON.setColor(SUCCESS_STATUS_ICON_COLOR);

            add(new OperationStatusView(securityService, SUCCESS_STATUS_ICON, SUCCESS_HEADER_TEXT, OPERATION_SUCCEEDED_TEXT, ACCOUNT_VERIFIED_TEXT));
        } else {

            FAILURE_STATUS_ICON.setColor(FAILURE_STATUS_ICON_COLOR);

            add(new OperationStatusView(securityService, FAILURE_STATUS_ICON, FAILURE_HEADER_TEXT, OPERATION_FAILED_TEXT, ACCOUNT_NOT_VERIFIED_TEXT));
        }
    }
}

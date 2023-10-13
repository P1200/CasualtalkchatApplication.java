package com.project.casualtalkchat.password_change_page;

import com.project.casualtalkchat.common.BottomBar;
import com.project.casualtalkchat.common.OperationStatusView;
import com.project.casualtalkchat.common.PasswordValidator;
import com.project.casualtalkchat.password_successfully_changed_page.PasswordSuccessfullyChangedView;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("password-change")
@AnonymousAllowed
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class PasswordChangeView extends VerticalLayout implements HasUrlParameter<String> {

    private static final String FAILURE_HEADER_TEXT = "Your account has not been changed";
    private static final String OPERATION_FAILED_TEXT = "Operation has been failed";
    private static final String ACCOUNT_NOT_VERIFIED_TEXT = "Your account has not been successfully recovered. " +
            "Please check if your recovery code is valid and correctly typed.";
    private static final Icon FAILURE_STATUS_ICON = new Icon("lumo", "cross");
    private static final String FAILURE_STATUS_ICON_COLOR = "red";

    private final SecurityService securityService;
    private final ChangePasswordService service;

    private Span errorMessage;
    private Button submitButton;
    private PasswordField passwordField;
    private PasswordField repeatedPasswordField;

    private BeanValidationBinder<UserEntity> binder;

    public PasswordChangeView(ChangePasswordService service,
                              SecurityService securityService) {
        this.securityService = securityService;
        this.service = service;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String userIdAndToken) {

        this.getStyle()
                .setBackground("#f3f4f6");
        this.setMinHeight(100, Unit.VH);
        this.setPadding(false);

        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(userIdAndToken);

        if (tokenEntity != null) {

            UserEntity user = tokenEntity.getUser();

            Anchor logo = new Anchor("/", "CasualTalk");
            logo.setClassName("logo text-center my-4");
            logo.setWidth(100, Unit.PERCENTAGE);

            BottomBar bottomBar = new BottomBar();
            bottomBar.getStyle()
                    .setPosition(Style.Position.FIXED)
                    .setBottom("0");

            add(logo, createPageView(), bottomBar);

            attachFieldsToValidationBinder();

            acceptPasswordChange(user, tokenEntity);
        } else {

            FAILURE_STATUS_ICON.setColor(FAILURE_STATUS_ICON_COLOR);

            add(new OperationStatusView(securityService, FAILURE_STATUS_ICON, FAILURE_HEADER_TEXT, OPERATION_FAILED_TEXT, ACCOUNT_NOT_VERIFIED_TEXT));
        }

    }

    private Div createPageView() {

        Div card = new Div(getFormLayout());
        card.setClassName("card");
        card.setMaxWidth("500px");
        card.getStyle().set("margin", "0 auto");

        return card;
    }

    private FormLayout getFormLayout() {
        H2 title = new H2("Create an account");
        title.setClassName("text-center");

        passwordField = new PasswordField("Password");
        repeatedPasswordField = new PasswordField("Repeated password");

        errorMessage = new Span();

        submitButton = new Button("Register");
        submitButton.setClassName("register-button");

        FormLayout formLayout =
                new FormLayout(title, passwordField, repeatedPasswordField, errorMessage, submitButton);
        formLayout.setId("registration-form");
        formLayout.setClassName("card-body");

        formLayout.setResponsiveSteps(getResponsiveStep("0", 1),
                getResponsiveStep("490px", 2));

        formLayout.setColspan(title, 2);
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("padding", "15px 0");
        return formLayout;
    }

    private FormLayout.ResponsiveStep getResponsiveStep(String minWidth, int columns) {
        return new FormLayout.ResponsiveStep(minWidth, columns,
                FormLayout.ResponsiveStep
                        .LabelsPosition
                        .TOP);
    }

    private void attachFieldsToValidationBinder() {
        binder = new BeanValidationBinder<>(UserEntity.class);

        var passwordValidator = new PasswordValidator();

        binder.forField(passwordField)
                .withValidator(passwordValidator::validatePassword)
                .bind("password");

        binder.forField(repeatedPasswordField)
                .asRequired("Please repeat the password")
                .withValidator(passwordValidator::validateRepeatedPassword)
                .bind(userEntity -> "",
                        //Just a trick to avoid data binding
                        (user, repeatedPassword) -> user.skipDataBinding());

        binder.setStatusLabel(errorMessage);
    }

    private void acceptPasswordChange(UserEntity user, AccountRecoveryTokenEntity tokenEntity) {
        submitButton.addClickListener(e -> {
            try {
                UserEntity userEntity = new UserEntity();
                binder.writeBean(userEntity);

                user.setPasswordWithoutEncoding(userEntity.getPassword());

                service.acceptPasswordChange(user, tokenEntity);

                getUI().ifPresent(ui -> ui.navigate(PasswordSuccessfullyChangedView.class));
            } catch (ValidationException validationException) {
                errorMessage.setText(validationException.getMessage());
            }
        });
    }
}

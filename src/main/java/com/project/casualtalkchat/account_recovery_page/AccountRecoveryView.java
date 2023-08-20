package com.project.casualtalkchat.account_recovery_page;

import com.project.casualtalkchat.account_recovery_requested_page.AccountRecoveryRequestedView;
import com.project.casualtalkchat.common.BottomBar;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.context.ApplicationEventPublisher;

@Route("account-recovery")
@AnonymousAllowed
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class AccountRecoveryView extends VerticalLayout {

    private final ApplicationEventPublisher eventPublisher;

    private EmailField emailField;
    private Span errorMessage;
    private Button submitButton;
    private BeanValidationBinder<UserEntity> binder;

    public AccountRecoveryView(ApplicationEventPublisher eventPublisher) {

        this.eventPublisher = eventPublisher;

        this.setPadding(false);

        Anchor logo = new Anchor("/", "CasualTalk");
        logo.setClassName("logo text-center my-4");
        logo.setWidth(100, Unit.PERCENTAGE);

        BottomBar bottomBar = new BottomBar();
        bottomBar.getStyle()
                .setPosition(Style.Position.FIXED)
                .setBottom("0");

        add(logo, createPageView(), bottomBar);

        attachFieldsToValidationBinder();

        validateAndBindDataOrShowErrorMessage();
    }

    private Div createPageView() {

        Div card = new Div(getFormLayout());
        card.setClassName("card");
        card.setMaxWidth("500px");
        card.getStyle().set("margin", "0 auto");

        return card;
    }

    private FormLayout getFormLayout() {
        H2 title = new H2("Recover your account");
        title.setClassName("text-center");

        emailField = new EmailField("Email");

        errorMessage = new Span();

        submitButton = new Button("Send recovery email");
        submitButton.setClassName("register-button");

        FormLayout formLayout =
                new FormLayout(title, emailField, errorMessage, submitButton);
        formLayout.setId("registration-form");
        formLayout.setClassName("card-body");

        formLayout.setResponsiveSteps(getResponsiveStep("0", 1),
                getResponsiveStep("490px", 2));

        formLayout.setColspan(title, 2);
        formLayout.setColspan(emailField, 2);
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

        binder.forField(emailField)
                .asRequired(new EmailValidator("Value is not a valid email address"))
                .bind("email");

        binder.setStatusLabel(errorMessage);
    }

    private void validateAndBindDataOrShowErrorMessage() {
        submitButton.addClickListener(e -> {
            try {
                UserEntity userEntity = new UserEntity();
                binder.writeBean(userEntity);

                String appUrl = VaadinServlet.getCurrent()
                        .getServletContext()
                        .getContextPath();
                eventPublisher.publishEvent(new OnAccountRecoveryRequestedEvent(userEntity.getEmail(), appUrl,
                        VaadinService.getCurrentRequest()
                                .getLocale()));

                getUI().ifPresent(ui -> ui.navigate(AccountRecoveryRequestedView.class));
            } catch (ValidationException validationException) {
                errorMessage.setText(validationException.getMessage());
            }
        });
    }
}

package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.account_created_page.AccountSuccessfullyCreatedView;
import com.project.casualtalkchat.common.BottomBar;
import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.project.casualtalkchat.common.PasswordValidator;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;

@Route("register")
@AnonymousAllowed
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class RegisterView extends VerticalLayout {

    private static final String SERVICE_TERMS_TEXT = "I agree all statements in Terms of service";
    public static final String SERVICE_TERMS_NOT_ACCEPTED_ERROR_MESSAGE = "You have to accept service policy.";

    private final UserRegistrationService service;
    private final ApplicationEventPublisher eventPublisher;

    private TextField usernameField;
    private AvatarField avatarField;
    private EmailField emailField;
    private Span errorMessage;
    private Button submitButton;
    private CheckboxGroup<String> allowServiceTerms;
    private PasswordField passwordField;
    private PasswordField repeatedPasswordField;

    private BeanValidationBinder<UserEntity> binder;

    public RegisterView(UserRegistrationService service, ApplicationEventPublisher eventPublisher) {

        this.service = service;
        this.eventPublisher = eventPublisher;

        this.setPadding(false);

        Anchor logo = new Anchor("/", "CasualTalk");
        logo.setClassName("logo text-center my-4");
        logo.setWidth(100, Unit.PERCENTAGE);

        add(logo, createPageView(), new BottomBar());

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
        H2 title = new H2("Create an account");
        title.setClassName("text-center");

        usernameField = new TextField("Username");

        avatarField = new AvatarField("Select Avatar image");

        allowServiceTerms = new CheckboxGroup<>();
        allowServiceTerms.setItems(SERVICE_TERMS_TEXT);
        allowServiceTerms.getStyle().set("padding-top", "10px");

        emailField = new EmailField("Email");

        passwordField = new PasswordField("Password");
        repeatedPasswordField = new PasswordField("Repeated password");

        errorMessage = new Span();

        submitButton = new Button("Register");
        submitButton.setClassName("register-button");

        FormLayout formLayout =
                new FormLayout(title, usernameField, emailField, avatarField, passwordField, repeatedPasswordField,
                        allowServiceTerms, errorMessage, submitButton);
        formLayout.setId("registration-form");
        formLayout.setClassName("card-body");

        formLayout.setResponsiveSteps(getResponsiveStep("0", 1),
                getResponsiveStep("490px", 2));

        formLayout.setColspan(title, 2);
        formLayout.setColspan(avatarField, 2);
        formLayout.setColspan(allowServiceTerms, 2);
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        errorMessage.getStyle()
                    .set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle()
                    .set("padding", "15px 0");
        return formLayout;
    }

    private FormLayout.ResponsiveStep getResponsiveStep(String minWidth, int columns) {
        return new FormLayout.ResponsiveStep(minWidth, columns, FormLayout.ResponsiveStep
                                                                            .LabelsPosition
                                                                            .TOP);
    }

    private void attachFieldsToValidationBinder() {
        binder = new BeanValidationBinder<>(UserEntity.class);

        binder.forField(usernameField)
                .asRequired("Please type your username")
                .withValidator(new StringLengthValidator("Your username must have at least 1 letter " +
                        "and at most 32 letters", 1, 32))
                .bind("username");

        binder.forField(allowServiceTerms)
                .withValidator(this::serviceTermsValidator)
                .bind(userEntity -> Set.of(""),
                        (userEntity, strings) -> userEntity.setServiceTermsAccepted(strings.contains(SERVICE_TERMS_TEXT)));

        binder.forField(emailField)
                .asRequired(new EmailValidator("Value is not a valid email address"))
                .bind("email");

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

    private ValidationResult serviceTermsValidator(Set<String> areTermsAccepted, ValueContext ctx) {

        if (areTermsAccepted.contains(SERVICE_TERMS_TEXT)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(SERVICE_TERMS_NOT_ACCEPTED_ERROR_MESSAGE);
    }

    private void validateAndBindDataOrShowErrorMessage() {
        submitButton.addClickListener(e -> {
            try {
                UserEntity userEntity = new UserEntity();
                binder.writeBean(userEntity);

                if (avatarField.isEmpty()) {
                    service.registerNewUserAccount(userEntity);
                } else {
                    service.registerNewUserAccount(userEntity, avatarField.generateModelValue());
                }

                String appUrl = VaadinServlet.getCurrent()
                        .getServletContext()
                        .getContextPath();
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userEntity, appUrl,
                        VaadinService.getCurrentRequest()
                                .getLocale()));

                getUI().ifPresent(ui -> ui.navigate(AccountSuccessfullyCreatedView.class));
            } catch (ValidationException | UserAlreadyExistException validationException) {
                errorMessage.setText(validationException.getMessage());
            } catch (FileCouldNotBeSavedException notFoundException) {
                errorMessage.setText("Something went wrong with saving your avatar image, please try again");
            }
        });
    }
}

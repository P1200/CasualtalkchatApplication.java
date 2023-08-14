package com.project.casualtalkchat.login_page;

import com.project.casualtalkchat.common.BottomBar;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | CasualTalk")
@AnonymousAllowed
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView(){
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);

        Anchor logo = new Anchor("/", "CasualTalk");
        logo.setClassName("logo text-center my-4");
        logo.setWidth(100, Unit.PERCENTAGE);

        loginForm.setI18n(getLoginI18n());
        loginForm.setAction("login");
        loginForm.setClassName("card");

        BottomBar bottomBar = new BottomBar();
        bottomBar.getStyle()
                .setPosition(Style.Position.FIXED)
                .setBottom("0");

        add(logo, loginForm, bottomBar);
    }

    private static LoginI18n getLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setUsername("Email");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Incorrect email or password");
        i18nErrorMessage.setMessage(
                "Check that you have entered the correct email address and password and try again.");
        i18n.setErrorMessage(i18nErrorMessage);
        return i18n;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}

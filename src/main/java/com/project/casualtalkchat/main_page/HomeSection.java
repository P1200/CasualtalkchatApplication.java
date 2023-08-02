package com.project.casualtalkchat.main_page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.StreamResource;

@CssImport("./styles.css")
public class HomeSection extends Section {

    public HomeSection() {
        this.setClassName("container");
        this.setId("home");

        Div content = new Div(getIncentivePartComponent(), getImagePart());
        content.setClassName("row pt-5");

        add(content);
    }

    private Component getImagePart() {

        Div imagePart = new Div(getAnimatedImage());
        imagePart.setClassName("col-lg-6 order-1 order-lg-2 hero-img");

        return imagePart;
    }

    private Image getAnimatedImage() {
        StreamResource imageResource = new StreamResource("woaman_in_front_of_computer.png",
                () -> getClass().getResourceAsStream("/images/woaman_in_front_of_computer.png"));

        Image animatedImage = new Image(imageResource, "Woman chatting with someone");
        animatedImage.setClassName("img-fluid animated");
        return animatedImage;
    }

    private Component getIncentivePartComponent() {

        Div incentivePart = new Div(new H1("Chat with every!"),
                new H2("The best web chat application on the market"), getNavigationButtons());
        incentivePart.setClassName("col-lg-6 pt-5 pt-lg-0 order-2 order-lg-1 d-flex flex-column justify-content-center");

        return incentivePart;
    }

    private Div getNavigationButtons() {
        Anchor registrationButton = new Anchor("", "Register now!");
        registrationButton.setClassName("register-button");
        Anchor loginButton = new Anchor("", "Login now!");
        loginButton.setClassName("login-button");

        Div buttons = new Div(registrationButton, loginButton);
        buttons.setClassName("d-flex");
        return buttons;
    }
}

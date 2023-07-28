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

        StreamResource imageResource = new StreamResource("pngwing.com (1).png",
                () -> getClass().getResourceAsStream("/images/pngwing.com (1).png"));

        Image animatedImage = new Image(imageResource, "Woman chatting with someone");
        animatedImage.setClassName("img-fluid animated");

        Div imagePart = new Div(animatedImage);
        imagePart.setClassName("col-lg-6 order-1 order-lg-2 hero-img");

        return imagePart;
    }

    private Component getIncentivePartComponent() {

        NativeButton registrationButton = new NativeButton("Register now!");
        NativeButton loginButton = new NativeButton("Login now!");

        Div buttons = new Div(registrationButton, loginButton);
        buttons.setClassName("d-flex");

        H1 slogan = new H1("Chat with every!");
        H2 shortProductDescription = new H2("The best web chat application on the market");

        Div incentivePart = new Div(slogan, shortProductDescription, buttons);
        incentivePart.setClassName("col-lg-6 pt-5 pt-lg-0 order-2 order-lg-1 d-flex flex-column justify-content-center");

        return incentivePart;
    }
}

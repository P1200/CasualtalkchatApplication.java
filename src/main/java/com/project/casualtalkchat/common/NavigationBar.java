package com.project.casualtalkchat.common;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

//@CssImport("./styles.css")
public class NavigationBar extends HorizontalLayout {

    public NavigationBar() {

        StreamResource logoResource = new StreamResource("logo.png",
                () -> getClass().getResourceAsStream("/images/logo.png"));

        Image logo = new Image(logoResource, "CasualTalk logo");
        logo.setWidth(118, Unit.PIXELS);
        logo.setHeight(36, Unit.PIXELS);
        Anchor homeLinkLogo = new Anchor("", "");
        homeLinkLogo.add(logo);
        Anchor homeLink = new Anchor("#", "Home");
        Anchor aboutLink = new Anchor("#about", "About");
        Anchor contactLink = new Anchor("#contact", "Contact");

        MenuBar leftMenuBar = new MenuBar();
        leftMenuBar.addItem(homeLink);
        leftMenuBar.addItem(aboutLink);
        leftMenuBar.addItem(contactLink);

        MenuBar rightMenuBar = new MenuBar();
        rightMenuBar.addItem("Login");
        rightMenuBar.addItem("Registration");

        HorizontalLayout div = new HorizontalLayout(homeLinkLogo, leftMenuBar);

        // Add the links to the navigation bar
        add(div, rightMenuBar);
    }
}

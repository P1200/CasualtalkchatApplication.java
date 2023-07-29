package com.project.casualtalkchat.common;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class TopBar extends HorizontalLayout {

    public TopBar() {

        this.setWidth(100, Unit.PERCENTAGE);

        Anchor linkToLogin = new Anchor("#Login", "Login");
        linkToLogin.setClassName("nav-link login-button");
        Anchor linkToRegistration = new Anchor("#Registration", "Registration");
        linkToRegistration.setClassName("nav-link register-button");

        ListItem loginItem = new ListItem(linkToLogin);
        ListItem registrationItem = new ListItem(linkToRegistration);

        UnorderedList accountLinks = new UnorderedList(loginItem, registrationItem);
        accountLinks.setClassName("navbar-nav ms-auto");

        UnorderedList links = new UnorderedList(getNavigationItem("#home", "Home"),
                getNavigationItem("#about", "About"), getNavigationItem("#contact", "Contact"));
        links.setClassName("navbar-nav");

        Anchor logo = new Anchor("/2", "CasualTalk");
        logo.setClassName("navbar-brand logo");

        Nav navigation = new Nav(logo, getHamburgerButton(), getNavigationLinks(accountLinks, links));
        navigation.setClassName("navbar navbar-light navbar-expand-lg");

        Header header = new Header(navigation);
        header.setWidth(100, Unit.PERCENTAGE);
        header.setClassName("fixed-top");
        header.setId("header");

        add(header);
    }

    private static Div getNavigationLinks(UnorderedList accountLinks, UnorderedList links) {
        Div navigationLinks = new Div(links, accountLinks);
        navigationLinks.setClassName("collapse navbar-collapse");
        navigationLinks.setId("navbarSupportedContent");
        return navigationLinks;
    }

    private static ListItem getNavigationItem(String href, String text) {
        Anchor linkToHome = new Anchor(href, text);
        linkToHome.setClassName("nav-link scrollto");
        return new ListItem(linkToHome);
    }

    private static NativeButton getHamburgerButton() {
        Span hamburgerIcon = new Span();
        hamburgerIcon.setClassName("navbar-toggler-icon");

        Element hamburgerButtonElement = ElementFactory.createButton();
        hamburgerButtonElement.setAttribute("class", "navbar-toggler");
        hamburgerButtonElement.setAttribute("data-toggle", "collapse");
        hamburgerButtonElement.setAttribute("data-target", "#navbarSupportedContent");
        hamburgerButtonElement.setAttribute("aria-controls", "navbarSupportedContent");
        hamburgerButtonElement.setAttribute("aria-expanded", "false");
        hamburgerButtonElement.setAttribute("aria-label", "Toggle navigation");

        NativeButton hamburgerButton = ComponentUtil.componentFromElement(hamburgerButtonElement, NativeButton.class, true);
        hamburgerButton.add(hamburgerIcon);
        return hamburgerButton;
    }
}

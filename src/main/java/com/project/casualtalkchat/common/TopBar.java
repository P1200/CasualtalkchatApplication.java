package com.project.casualtalkchat.common;

import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.project.casualtalkchat.common.UserEntityUtils.getAvatarResource;

@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class TopBar extends HorizontalLayout {

    private final SecurityService securityService;

    public TopBar(SecurityService securityService) {

        this.securityService = securityService;

        this.setWidth(100, Unit.PERCENTAGE);

        Header header = new Header(getNavigationPart());
        header.setWidth(100, Unit.PERCENTAGE);
        header.setClassName("fixed-top");
        header.setId("header");
        header.getStyle()
                .setZIndex(10);

        add(header);
    }

    private Nav getNavigationPart() {

        Anchor logo = new Anchor("/", "CasualTalk");
        logo.setClassName("navbar-brand logo");

        Nav navigation = new Nav(logo, getHamburgerButton(), getNavigationComponents(getAccountManagementLinks(),
                getNavigationComponents()));
        navigation.setClassName("navbar navbar-light navbar-expand-lg");
        return navigation;
    }

    private Component getAccountManagementLinks() {

        Optional<CustomUserDetails> authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            Div accountManagement = getAccountComponent(authenticatedUser.get());
            accountManagement.getStyle()
                    .setCursor("pointer");

            return getAccountManagementMenu(accountManagement);
        } else {
            return getLoginRegisterButtonsList();
        }
    }

    private Div getAccountComponent(CustomUserDetails userDetails) {
        Avatar avatar = new Avatar();
        avatar.setImageResource(getAvatarResource(userDetails));
        avatar.getStyle()
                .setWidth("40px")
                .set("margin-right", "0.5rem");
        Paragraph username = new Paragraph(userDetails.getUsername());
        username.getStyle()
                .set("margin-top", "auto")
                .set("margin-bottom", "auto");

        Div accountManagement = new Div(avatar, username);
        accountManagement.setClassName("row");
        accountManagement.getStyle()
                .set("margin-right", "0");
        return accountManagement;
    }

    private UnorderedList getLoginRegisterButtonsList() {
        Anchor linkToLogin = new Anchor("login", "Login");
        linkToLogin.setClassName("nav-link login-button");
        Anchor linkToRegistration = new Anchor("register", "Registration");
        linkToRegistration.setClassName("nav-link register-button");

        ListItem loginItem = new ListItem(linkToLogin);
        ListItem registrationItem = new ListItem(linkToRegistration);

        UnorderedList accountLinks = new UnorderedList(loginItem, registrationItem);
        accountLinks.setClassName("navbar-nav ms-auto");
        return accountLinks;
    }

    private MenuBar getAccountManagementMenu(Div accountManagement) {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuItem = menuBar.addItem(accountManagement);
        menuItem.getStyle()
                .set("padding-left", "15px");
        SubMenu subMenu = menuItem.getSubMenu();

        subMenu.addItem("Settings");
        subMenu.addItem("Help");
        subMenu.addItem("Sing out", event -> securityService.logout());
        subMenu.getChildren()
                .forEach(item -> item.getStyle()
                        .setCursor("pointer"));
        menuBar.setClassName("navbar-nav ms-auto");
        return menuBar;
    }

    private UnorderedList getNavigationComponents() {
        UnorderedList links = new UnorderedList(getNavigationItem("/", "Home"),
                getNavigationItem("#about", "About"), getNavigationItem("#contact", "Contact"));
        links.setClassName("navbar-nav");
        return links;
    }

    private Div getNavigationComponents(Component accountLinks, UnorderedList links) {
        Div navigationLinks = new Div(links, accountLinks);
        navigationLinks.setClassName("collapse navbar-collapse");
        navigationLinks.setId("navbarSupportedContent");
        return navigationLinks;
    }

    private ListItem getNavigationItem(String href, String text) {
        Anchor linkToHome = new Anchor(href, text);
        linkToHome.setClassName("nav-link scrollto");
        return new ListItem(linkToHome);
    }

    private NativeButton getHamburgerButton() {
        Span hamburgerIcon = new Span();
        hamburgerIcon.setClassName("navbar-toggler-icon");

        Element hamburgerButtonElement = ElementFactory.createButton();
        hamburgerButtonElement.setAttribute("class", "navbar-toggler");
        hamburgerButtonElement.setAttribute("data-toggle", "collapse");
        hamburgerButtonElement.setAttribute("data-target", "#navbarSupportedContent");
        hamburgerButtonElement.setAttribute("aria-controls", "navbarSupportedContent");
        hamburgerButtonElement.setAttribute("aria-expanded", "false");
        hamburgerButtonElement.setAttribute("aria-label", "Toggle navigation");

        NativeButton hamburgerButton =
                ComponentUtil.componentFromElement(hamburgerButtonElement, NativeButton.class, true);
        hamburgerButton.add(hamburgerIcon);
        return hamburgerButton;
    }
}

package com.project.casualtalkchat.main_page;

import com.project.casualtalkchat.common.TopBar;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class MainView2 extends VerticalLayout {

    MainView2() {
        this.setPadding(false);
        TopBar topBar = new TopBar();

        Footer footer = getFooterComponent();

        add(topBar, new HomeSection(), new AboutSection(),new ContactSection(), footer);
    }

    private static Footer getFooterComponent() {

        Div copyright = getCopyrightPart();

        Div credits = new Div(new Paragraph("Designed by Mateusz Wołek"));
        credits.setClassName("credits");

        Div copyrightAndCredits = new Div(copyright, credits);
        copyrightAndCredits.setClassName("container footer-bottom clearfix");

        Footer footer = new Footer(copyrightAndCredits);
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setId("footer");
        return footer;
    }

    private static Div getCopyrightPart() {
        Html copyrightText = new Html("<p>© Copyright <strong>University of Zielona Góra</strong>. " +
                "All Rights Reserved</p>");
        Div copyright = new Div(copyrightText);
        copyright.setClassName("copyright");
        return copyright;
    }
}

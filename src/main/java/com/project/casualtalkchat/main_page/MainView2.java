package com.project.casualtalkchat.main_page;

import com.project.casualtalkchat.common.TopBar;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
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

        Footer footer = new Footer(new Paragraph("Copyrights"));
        footer.setHeight(5, Unit.PERCENTAGE);

        add(topBar, new HomeSection(), new AboutSection(),new ContactSection(), footer);
    }
}

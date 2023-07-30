package com.project.casualtalkchat.common;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Paragraph;

public class BottomBar extends Footer {

    public BottomBar() {

        this.setWidth(100, Unit.PERCENTAGE);
        this.setId("footer");

        Div copyright = getCopyrightPart();

        Div credits = new Div(new Paragraph("Designed by Mateusz Wołek"));
        credits.setClassName("credits");

        Div copyrightAndCredits = new Div(copyright, credits);
        copyrightAndCredits.setClassName("container footer-bottom clearfix");

        add(copyrightAndCredits);
    }

    private Div getCopyrightPart() {
        Html copyrightText = new Html("<p>© Copyright <strong>University of Zielona Góra</strong>. " +
                "All Rights Reserved</p>");
        Div copyright = new Div(copyrightText);
        copyright.setClassName("copyright");
        return copyright;
    }
}

package com.project.casualtalkchat.common;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.dom.Style;

@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class OperationStatusView extends Div {

    public OperationStatusView(Icon statusIcon, String headerText, String secondHeaderText, String paragraphText) {
        this.getStyle()
                .setBackground("#f3f4f6");
        this.setWidth(100, Unit.PERCENTAGE);
        this.setMinHeight(100, Unit.VH);

        statusIcon.setSize("40px");

        Div card = getCard(statusIcon, getHeader(headerText), getCheckYourInboxHeader(secondHeaderText),
                        getCheckYourInboxText(paragraphText));

        BottomBar bottomBar = new BottomBar();
        bottomBar.setWidth(100, Unit.PERCENTAGE);

        add(new TopBar(), card, bottomBar);
    }

    private Div getCard(Icon statusIcon, H1 header, H2 checkYourInboxHeader, Paragraph checkYourInboxText) {
        Div card = new Div(statusIcon, header, checkYourInboxHeader, checkYourInboxText);
        card.setClassName("card p-5");
        card.setMaxWidth("500px");
        card.getStyle()
                .set("margin", "20px auto 33vh auto")
                .setTop("100px")
                .set("align-items", "center");
        return card;
    }

    private Paragraph getCheckYourInboxText(String paragraphText) {
        Paragraph checkYourInboxText = new Paragraph(paragraphText);
        checkYourInboxText.setWidth(100, Unit.PERCENTAGE);
        return checkYourInboxText;
    }

    private H2 getCheckYourInboxHeader(String secondHeaderText) {
        H2 checkYourInboxHeader = new H2(secondHeaderText);
        checkYourInboxHeader.setClassName("my-2");
        checkYourInboxHeader.setWidth(100, Unit.PERCENTAGE);
        return checkYourInboxHeader;
    }

    private H1 getHeader(String headerText) {
        H1 header = new H1(headerText);
        header.getStyle()
                .set("font-size", "xx-large!important")
                .set("line-height", "1.2!important")
                .setPadding("15px 0 0 0")
                .setMargin("0 0 0 15px")
                .setTextAlign(Style.TextAlign.CENTER);
        return header;
    }
}

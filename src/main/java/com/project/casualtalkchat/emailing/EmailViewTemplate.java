package com.project.casualtalkchat.emailing;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.dom.Style;

public class EmailViewTemplate extends Div {

    public EmailViewTemplate(String headerText, String mainText, String additionalContentLink,
                             String additionalContentText, String endText) {
        this.getStyle()
                .setBackground("#f9f9fa");

        Html centeredButton = getCenteredButton(additionalContentLink, additionalContentText);
        Div center =
                getCenterBox(getEmailHeader(headerText), getEmailText(mainText), centeredButton, getEmailText(endText));

        add(getMargin(), center, getMargin());
    }

    private Div getMargin() {
        Div marginTop = new Div();
        marginTop.setMinHeight(10, Unit.PIXELS);
        return marginTop;
    }

    private Div getCenterBox(H1 emailHeader, Paragraph emailMainPart, Html tableToCenterButton, Paragraph emailEndPart) {
        Div center = new Div(getLogo(), emailHeader, emailMainPart, tableToCenterButton, emailEndPart);
        center.setMaxWidth(512, Unit.PIXELS);
        center.getStyle()
                .setMargin("25px auto")
                .setBackground("white")
                .setPadding("25px");
        return center;
    }

    private Html getCenteredButton(String additionalContentLink, String additionalContentText) {
        Anchor buttonToAdditionalContent = new Anchor(additionalContentLink, additionalContentText);
        buttonToAdditionalContent.getStyle()
                .set("font-weight", "500")
                .set("font-size", "large")
                .set("letter-spacing", "1px")
                .setDisplay(Style.Display.INLINE_BLOCK)
                .setPadding("8px 30px 10px 30px")
                .setMargin("auto")
                .set("border-radius", "4px")
                .setTransition("0.5s")
                .setColor("#fff")
                .setBackground("#16df7e")
                .setBoxShadow("0 8px 28px rgba(22, 223, 126, 0.45)")
                .setTextDecoration("none");

        return new Html("""
                                        <table border="0" cellpadding="0" cellspacing="0" style="margin: auto">
                                            <tbody>
                                                <tr>
                                                    <td align="center" valign="middle">"""
                                                    + buttonToAdditionalContent.getElement()
                                                    + "</td></tr></tbody></table>");
    }

    private Paragraph getEmailText(String mainText) {
        Paragraph emailMainPart = new Paragraph(mainText);
        emailMainPart.getStyle()
                .setColor("black")
                .set("font-size", "medium");
        return emailMainPart;
    }

    private H1 getEmailHeader(String headerText) {
        H1 emailHeader = new H1(headerText);
        emailHeader.getStyle()
                .setColor("black");
        return emailHeader;
    }

    private Paragraph getLogo() {
        Paragraph logo = new Paragraph("CasualTalk");
        logo.setClassName("logo text-center");
        logo.setWidth(100, Unit.PERCENTAGE);
        logo.getStyle()
                .setTextAlign(Style.TextAlign.CENTER)
                .setColor("#5f687b")
                .set("font-size", "32px")
                .set("line-height", "1")
                .set("font-weight", "700")
                .set("letter-spacing", "0.5px");
        return logo;
    }
}

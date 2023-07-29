package com.project.casualtalkchat.main_page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;

@CssImport("./styles.css")
public class AboutSection extends Section {

    public AboutSection() {
        this.setClassName("container");
        this.setId("about");

        Div content = new Div(getImagePart(), getApplicationDescriptionPart());
        content.setClassName("row");

        add(content);
    }

    private Component getImagePart() {

        StreamResource imageResource = new StreamResource("hand_with_phone.png",
                () -> getClass().getResourceAsStream("/images/hand_with_phone.png"));

        Image image = new Image(imageResource, "Hand with phone with opened chat application");
        image.setClassName("img-fluid");
        image.setMaxWidth(75, Unit.PERCENTAGE);

        Div imagePart = new Div(image);
        imagePart.setClassName("col-lg-6");

        return imagePart;
    }

    private Component getApplicationDescriptionPart() {
        Paragraph shortProductDescription =
                new Paragraph("CasualTalk is modern, convenient and free web application for every to chatting with people around the world.");
        shortProductDescription.setClassName("fst-italic");

        Div incentivePart = new Div(new H3("Free web chat application for everyone"),
                shortProductDescription, getApplicationAdvantagesList(), new Paragraph("Join us now and be in touch with friends and coworkers."));
        incentivePart.setClassName("col-lg-6 pt-4 pt-lg-0 content");

        return incentivePart;
    }

    private UnorderedList getApplicationAdvantagesList() {
        ListItem firstAdvantage = createAndGetAdvantage("Easy in use and convenient");
        ListItem secondAdvantage = createAndGetAdvantage("Adapted to mobile devices");
        ListItem thirdAdvantage = createAndGetAdvantage("Completely free, without any ads and payments");
        ListItem fourthAdvantage = createAndGetAdvantage("Advanced messaging options");

        UnorderedList applicationAdvantages = new UnorderedList(firstAdvantage, secondAdvantage,
                thirdAdvantage, fourthAdvantage);
        applicationAdvantages.setClassName("advantages-list");
        return applicationAdvantages;
    }

    private ListItem createAndGetAdvantage(String advantageText) {
        Paragraph firstAdvantage = new Paragraph(advantageText);
        firstAdvantage.setClassName("ml-2");
        ListItem convince = new ListItem(getCheckMarkIcon(), firstAdvantage);
        convince.setClassName("row");
        return convince;
    }

    private Icon getCheckMarkIcon() {
        Icon checkMark = new Icon(VaadinIcon.CHECK_CIRCLE_O);
        checkMark.setColor("#16df7e");
        return checkMark;
    }
}

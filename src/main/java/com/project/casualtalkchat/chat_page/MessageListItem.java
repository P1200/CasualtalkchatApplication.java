package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.AbstractStreamResource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MessageListItem extends Div {

    public MessageListItem(String text, Instant time, String userName, AbstractStreamResource avatarResource) {
        this.getStyle().set("display", "flex");

        Avatar avatar = new Avatar();
        avatar.setImageResource(avatarResource);
        avatar.getStyle().setMargin("8px");

        Paragraph textParagraph = new Paragraph(text);
        Paragraph usernameParagraph = new Paragraph(userName);
        usernameParagraph.getStyle()
                .setMargin("0 6px 0 0")
                .set("font-weight", "600");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL dd, yyyy, hh:mm a").withLocale(Locale.UK);
        Paragraph timeParagraph =
                new Paragraph(time.atZone(ZoneId.systemDefault()).format(formatter));
        timeParagraph.getStyle()
                .setMargin("0")
                .setDisplay(Style.Display.CONTENTS)
                .set("vertical-align", "text-bottom")
                .set("font-size", "small")
                .set("font-weight", "500")
                .setColor("gray");
        Div header = new Div(usernameParagraph, timeParagraph);
        header.getStyle()
                .set("display", "flex");

        add(avatar, new Div(header, textParagraph));
    }

    public MessageListItem(AbstractStreamResource contentResource, String text, Instant time, String userName, AbstractStreamResource avatarResource) {
        this(text, time, userName, avatarResource);

        Image contentImage = new Image(contentResource, "alt");
        add(contentImage);
    }
}

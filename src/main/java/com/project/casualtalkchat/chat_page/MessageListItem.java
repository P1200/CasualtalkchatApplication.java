package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.olli.FileDownloadWrapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MessageListItem extends Div {

    private final Div wrapper;

    public MessageListItem(String text, Instant time, String userName, AbstractStreamResource avatarResource) {
        this.getStyle()
                .set("display", "flex")
                .set("padding-bottom", "5px");

        Avatar avatar = new Avatar();
        avatar.setImageResource(avatarResource);
        avatar.getStyle().setMargin("8px");

        Paragraph textParagraph = new Paragraph(text);
        textParagraph.getStyle()
                    .set("overflow-wrap", "anywhere");
        Paragraph usernameParagraph = new Paragraph(userName);
        usernameParagraph.getStyle()
                        .setMargin("0 6px 0 0")
                        .set("font-weight", "600");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL dd, yyyy, hh:mm a")
                                                        .withLocale(Locale.UK);
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

        wrapper = new Div(header, textParagraph);
        add(avatar, wrapper);
    }

    public MessageListItem(List<Attachment> attachments, String text, Instant time, String userName, AbstractStreamResource avatarResource) {
        this(text, time, userName, avatarResource);

        wrapper.getStyle()
                .setDisplay(Style.Display.FLEX)
                .set("flex-direction", "column");
        for (Attachment attachment : attachments) {
            StreamResource streamResource = new StreamResource(attachment.getFileName(), attachment.getImage());
            if (attachment.getMime().contains("image")) {
                Image contentImage = new Image(streamResource, "alt");
                contentImage.setMaxWidth(50, Unit.PERCENTAGE);
                contentImage.getStyle()
                        .setBorder("1px solid lightgray");
                wrapper.add(contentImage);
            } else if (attachment.getMime().contains("video")) {
                Video video = new Video(streamResource);
                video.setMaxWidth(50, Unit.PERCENTAGE);
                video.getStyle()
                        .setBorder("1px solid lightgray");
                wrapper.add(video);
            } else if (attachment.getMime().contains("audio")) {
                Audio audio = new Audio(streamResource);
                audio.setMaxWidth(50, Unit.PERCENTAGE);
                wrapper.setWidth(100, Unit.PERCENTAGE);
                wrapper.add(audio);
            } else {
                Paragraph fileNameParagraph = new Paragraph(attachment.getFileName());
                fileNameParagraph.getStyle()
                                .setMargin("0");
                Icon icon = VaadinIcon.FILE_TEXT.create();
                icon.getStyle()
                    .setMargin("8px")
                    .set("min-width", "20px");
                Div fileComponent = new Div(icon, fileNameParagraph);
                fileComponent.getStyle()
                            .setDisplay(Style.Display.FLEX)
                            .setPadding("10px")
                            .setWidth("95%")
                            .set("border-radius", "25px")
                            .set("align-items", "center");
                fileComponent.setClassName(LumoUtility.Background.CONTRAST_5);
                FileDownloadWrapper link = new FileDownloadWrapper(streamResource);
                link.wrapComponent(fileComponent);
                wrapper.add(link);
            }
        }
    }
}

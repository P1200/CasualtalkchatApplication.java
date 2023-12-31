package com.project.casualtalkchat.register_page;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class AvatarField extends CustomField<AvatarImage> {

    private final Image currentAvatar;
    private final Upload upload;
    private AvatarImage value;
    private ByteArrayOutputStream outputStream;

    public AvatarField(String caption) {
        this();
        setLabel(caption);
    }

    public AvatarField() {

        currentAvatar = new Image();
        currentAvatar.setAlt("avatar image");
        currentAvatar.setMaxHeight("100px");
        currentAvatar.getStyle().set("margin-right", "15px");
        currentAvatar.setVisible(false); // see updateImage()

        upload = new Upload(this::receiveUpload);
        upload.getStyle().set("flex-grow", "1");

        upload.addSucceededListener(this::uploadSuccess);

        upload.addFailedListener(e -> setFailed(e.getReason().getMessage()));
        upload.addFileRejectedListener(e -> setFailed(e.getErrorMessage()));

        upload.setAcceptedFileTypes("image/*");

        upload.setMaxFiles(1);

        upload.setMaxFileSize(1024 * 1024);

        Div wrapper = new Div();
        wrapper.add(currentAvatar, upload);
        wrapper.getStyle().set("display", "flex");
        add(wrapper);
    }

    @Override
    protected AvatarImage generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(AvatarImage newPresentationValue) {
        value = newPresentationValue;
        updateImage();
    }

    private OutputStream receiveUpload(String fileName, String mimeType) {

        setInvalid(false);

        value = new AvatarImage();
        value.setName(fileName);
        value.setMime(mimeType);

        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    private void uploadSuccess(SucceededEvent e) {

        value.setImage(outputStream.toByteArray());

        setModelValue(value, true);

        updateImage();

        upload.getElement().executeJs("this.files=[]");
    }

    private void setFailed(String message) {
        setInvalid(true);
        setErrorMessage(message);
    }

    private void updateImage() {
        if (value != null && value.getImage() != null) {
            currentAvatar.setSrc(new StreamResource("avatar", () -> new ByteArrayInputStream(value.getImage())));
            currentAvatar.setVisible(true);
        } else {
            currentAvatar.setSrc("");
            currentAvatar.setVisible(false);
        }
    }
}

package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.*;
import com.vaadin.flow.server.AbstractStreamResource;

@Tag("audio")
public class Audio extends HtmlContainer implements ClickNotifier<com.vaadin.flow.component.html.Image>, HasAriaLabel {

    private static final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors
            .attributeWithDefault("src", "");

    public Audio() {
        super();
        getElement().setProperty("controls", true);
    }

    public Audio(String src) {
        setSrc(src);
        getElement().setProperty("controls", true);
    }

    public Audio(AbstractStreamResource src) {
        setSrc(src);
        getElement().setProperty("controls", true);
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }

    public void setSrc(AbstractStreamResource src) {
        getElement().setAttribute("src", src);
    }
}

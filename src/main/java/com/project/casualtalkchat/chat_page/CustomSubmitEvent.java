package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class CustomSubmitEvent extends ComponentEvent<Component> {

    private final boolean fromClient;
    private final String value;


    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public CustomSubmitEvent(Component source, boolean fromClient, String value) {
        super(source, fromClient);
        this.source = source;
        this.fromClient = fromClient;
        this.value = value;
    }
}

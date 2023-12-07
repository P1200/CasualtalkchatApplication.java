package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.function.Consumer;

public class CustomMessageInput extends Div {

    private final Button button;
    private final TextArea textArea;

    public CustomMessageInput() {
        this.textArea = new TextArea();
        this.button = new Button("Send");
        this.setId("custom-message-input");
        textArea.setWidth(80, Unit.PERCENTAGE);
        textArea.setMaxHeight(100, Unit.PIXELS);
        textArea.setPlaceholder("Message");
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        button.getStyle()
                .set("margin-left", "20px");
        button.addClassNames(
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST
        );
        button.addClickShortcut(Key.ENTER);

        add(textArea, button);
    }

    public Registration addSubmitListener(Consumer<CustomSubmitEvent> listener) {
        return button.addClickListener(e -> {
            listener.accept(new CustomSubmitEvent(button, false, textArea.getValue()));
            textArea.clear();
        });
    }
}

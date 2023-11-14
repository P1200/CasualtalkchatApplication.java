package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;

class ManagementDialog extends Dialog {

    public ManagementDialog() {
        Button closeButton = new Button("Close", e -> this.close());
        this.getFooter()
                .add(closeButton);

        Button closeCrossButton = new Button(new Icon("lumo", "cross"),
                e -> this.close());
        closeCrossButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeCrossButton);

        this.setModal(false);
        this.setDraggable(true);
        this.setWidth(50, Unit.VW);
    }
}

package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;

import java.util.Collections;
import java.util.List;

public class MessageList extends Scroller {

    public final Div div = new Div();
    private List<MessageListItem> items = Collections.emptyList();

    public MessageList() {
        setContent(div);
    }

    public void setItems(List<MessageListItem> items) {
        this.items.clear();
        this.div.removeAll();
        this.items = items;
        items.forEach(div::add);
    }

    public List<MessageListItem> getItems() {
        return items;
    }
}

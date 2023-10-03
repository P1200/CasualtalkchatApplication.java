package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnChatSelectedEventListener implements ApplicationListener<OnChatSelectedEvent> {

    @Override
    public void onApplicationEvent(OnChatSelectedEvent event) {

        ComponentUtil.getData(UI.getCurrent(), ChatView.class)
                .changeChat(event.getChatId());
    }
}

package com.project.casualtalkchat.chat_page;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnChatSelectedEvent extends ApplicationEvent {

    private final String chatId;

    public OnChatSelectedEvent(String chatId) {
        super(chatId);
        this.chatId = chatId;
    }
}

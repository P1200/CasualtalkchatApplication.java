package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.TopBar;
import com.project.casualtalkchat.common.UserEntityUtils;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("chat")
@PermitAll
@Slf4j
public class ChatView extends VerticalLayout {

    private final UserService service;
    private final ConversationService conversationService;
    private final CustomUserDetails loggedInUserDetails;
    private final MessageList messageList = new MessageList();
    private String currentConversationId;

    public ChatView(SecurityService securityService, UserService userService, ConversationService conversationService) {

        this.service = userService;
        this.conversationService = conversationService;

        ComponentUtil.setData(UI.getCurrent(), ChatView.class, this);

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            this.loggedInUserDetails = userDetails;
        } else {
            throw new RuntimeException(); //TODO redirect to error page
        }

        add(new TopBar(securityService), getPageLayout(loggedInUserDetails));
    }

    void changeChat(String currentConversationId) {

        this.currentConversationId = currentConversationId;
        log.debug("Chat was changed");

        List<MessageEntity> messages = conversationService.getMessagesList(currentConversationId);
        List<MessageListItem> items = new ArrayList<>();
        addItemForEachMessage(messages, items);

        messageList.setItems(items);
    }

    private HorizontalLayout getPageLayout(CustomUserDetails userDetails) {
        HorizontalLayout pageLayout =
                new HorizontalLayout(new TabsSectionComponent(service, conversationService, loggedInUserDetails.getId()),
                        getChatLayout(userDetails));
        pageLayout.getStyle()
                .set("margin-top", "46px")
                .setWidth("100%")
                .setHeight("86vh")
                .set("border-top", "1px solid rgba(0, 0, 0, 0.5)");
        return pageLayout;
    }

    private VerticalLayout getChatLayout(CustomUserDetails userDetails) {
        messageList.setWidthFull();
        MessageInput input = new MessageInput();
        input.setWidth(100, Unit.PERCENTAGE);
        input.addSubmitListener(submitEvent -> {
            MessageListItem newMessage = new MessageListItem(
                    submitEvent.getValue(), Instant.now(), userDetails.getUsername());
            newMessage.setUserImageResource(UserEntityUtils.getAvatarResource(userDetails));
            newMessage.addThemeNames(LumoUtility.AlignContent.END,
                    LumoUtility.AlignItems.END, LumoUtility.Background.CONTRAST_5); //TODO idk how to do that
            List<MessageListItem> items = new ArrayList<>(messageList.getItems());
            items.add(newMessage);
            messageList.setItems(items);

            if (currentConversationId != null) {
                conversationService.saveMessage(currentConversationId, userDetails.getId(), submitEvent.getValue(), Instant.now());
            }
        });

        VerticalLayout chatLayout = new VerticalLayout(messageList, input);
        chatLayout.setWidth(75, Unit.PERCENTAGE);
        chatLayout.getStyle()
                .set("border-left", "1px solid rgba(0,0,0,.5)");
        chatLayout.expand(messageList);
        return chatLayout;
    }

    private void addItemForEachMessage(List<MessageEntity> messages, List<MessageListItem> items) {
        for (MessageEntity message : messages) {
            Instant messageSentTime = message.getSentTime()
                    .toInstant();
            String senderUsername = message.getSender()
                    .getUsername();
            MessageListItem item = new MessageListItem(message.getContent(),
                    messageSentTime, senderUsername);
            item.setUserImageResource(UserEntityUtils.getAvatarResource(message.getSender()
                    .getAvatarName()));

            items.add(item);
        }
    }
}

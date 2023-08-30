package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.TopBar;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("chat")
@PermitAll
public class ChatView extends VerticalLayout {

    private final UserService service;
    private final CustomUserDetails loggedInUserDetails;

    public ChatView(SecurityService securityService, UserService userService) {

        this.service = userService;

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            this.loggedInUserDetails = userDetails;
        } else {
            throw new RuntimeException(); //TODO redirect to error page
        }

        add(new TopBar(securityService), getPageLayout(loggedInUserDetails.getUsername()));
    }

    private HorizontalLayout getPageLayout(String username) {
        HorizontalLayout pageLayout =
                new HorizontalLayout(new TabsSectionComponent(service, loggedInUserDetails.getId()), getChatLayout(username));
        pageLayout.getStyle()
                .set("margin-top", "46px")
                .setWidth("100%")
                .setHeight("86vh")
                .set("border-top", "1px solid rgba(0, 0, 0, 0.5)");
        return pageLayout;
    }

    private VerticalLayout getChatLayout(String username) {
        MessageList list = new MessageList();
        list.setWidthFull();
        MessageInput input = new MessageInput();
        input.setWidth(100, Unit.PERCENTAGE);
        input.addSubmitListener(submitEvent -> {
            MessageListItem newMessage = new MessageListItem(
                    submitEvent.getValue(), Instant.now(), username);
            newMessage.setUserColorIndex(3);
            List<MessageListItem> items = new ArrayList<>(list.getItems());
            items.add(newMessage);
            list.setItems(items);
        });

        VerticalLayout chatLayout = new VerticalLayout(list, input);
        chatLayout.setWidth(75, Unit.PERCENTAGE);
        chatLayout.getStyle()
                .set("border-left", "1px solid rgba(0,0,0,.5)");
        chatLayout.expand(list);
        return chatLayout;
    }
}

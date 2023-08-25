package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.TopBar;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("chat")
@PermitAll
public class ChatView extends VerticalLayout {

    public ChatView(SecurityService securityService) {
        String username;

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        add(new TopBar(securityService), getPageLayout(username));
    }

    private HorizontalLayout getPageLayout(String username) {
        HorizontalLayout pageLayout = new HorizontalLayout(getSideMenu(), getChatLayout(username));
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

    private TabSheet getSideMenu() {

        TabSheet tabSheet = new TabSheet();

        Tab chats = new Tab(VaadinIcon.CHAT.create(), new Span("Chats"));
        tabSheet.add(chats, getContent("Chats content", "Add new chat"));

        Tab friends = new Tab(VaadinIcon.USERS.create(), new Span("Friends"));
        tabSheet.add(friends, getContent("Friends content", "Add new friend"));

        tabSheet.setWidth(25, Unit.PERCENTAGE);
        return tabSheet;
    }

    private Div getContent(String chatsContent, String buttonText) {
        Scroller chatsScroller = new Scroller(new Text(chatsContent));

        Button addNewChatButton = new Button(buttonText);
        addNewChatButton.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setBottom("0");
        Div content = new Div(chatsScroller, addNewChatButton);
        content.getStyle()
                .setPosition(Style.Position.RELATIVE)
                .setHeight("100%");
        return content;
    }
}

package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.project.casualtalkchat.common.TopBar;
import com.project.casualtalkchat.common.UserEntityUtils;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route("chat")
@PermitAll
@Slf4j
@CssImport("./styles.css")
public class ChatView extends VerticalLayout {

    private final UserService service;
    private final ConversationService conversationService;
    private final CustomUserDetails loggedInUserDetails;
    private final MessageList messageList = new MessageList();
    private final MultiFileBuffer buffer = new MultiFileBuffer();
    private final Upload upload = new Upload(buffer);
    private final ArrayList<Attachment> fileList = new ArrayList<>();
    private String currentConversationId;

    public ChatView(SecurityService securityService, UserService userService, ConversationService conversationService) {

        this.service = userService;
        this.conversationService = conversationService;

        ComponentUtil.setData(UI.getCurrent(), ChatView.class, this);

        Optional<CustomUserDetails> authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            this.loggedInUserDetails = authenticatedUser.get();
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

        input.addSubmitListener(submitEvent -> {

            List<MessageListItem> items = new ArrayList<>(messageList.getItems());
            if (buffer.getFiles().isEmpty()) {
                MessageListItem newMessage = new MessageListItem(
                        submitEvent.getValue(), Instant.now(), userDetails.getUsername(),
                        UserEntityUtils.getAvatarResource(userDetails));
                items.add(newMessage);
                conversationService.saveMessage(currentConversationId, userDetails.getId(), submitEvent.getValue(), Instant.now());
            } else {
                upload.clearFileList();
                MessageEntity messageEntity;
                try {
                    messageEntity =
                            conversationService.saveMessage(currentConversationId, userDetails.getId(), submitEvent.getValue(), fileList, Instant.now());
                } catch (FileCouldNotBeSavedException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e); //TODO
                } finally {
                    fileList.clear();
                }
                List<Attachment> attachmentResources =
                        conversationService.getMessageAttachmentResources(messageEntity.getAttachments());
                MessageListItem newMessage = new MessageListItem(attachmentResources,
                        submitEvent.getValue(), Instant.now(), userDetails.getUsername(),
                        UserEntityUtils.getAvatarResource(userDetails));
                items.add(newMessage);
            }

            messageList.setItems(items);
        });

        upload.getElement().appendChild(input.getElement());
        upload.setUploadButton(new Button(VaadinIcon.UPLOAD.create()));
        upload.setMaxFileSize(1024 * 1024 * 5);
        upload.setMaxFiles(10);

        upload.addStartedListener(event -> {
            input.setEnabled(false);
            log.debug("Uploading has been started.");
        });

        upload.addAllFinishedListener(event -> {
            input.setEnabled(true);
            log.debug("Uploading has been ended.");
        });

        upload.addFinishedListener(event -> {
            InputStream inputStream = buffer.getInputStream(event.getFileName());
            Attachment attachment = new Attachment(() -> inputStream, event.getMIMEType(), event.getFileName());
            fileList.add(attachment);
        });

        VerticalLayout chatLayout = new VerticalLayout(messageList, upload);
        chatLayout.setWidth(75, Unit.PERCENTAGE);
        chatLayout.getStyle()
                .set("border-left", "1px solid rgba(0,0,0,.5)");

        chatLayout.expand(messageList);
        chatLayout.getStyle().set("padding-bottom", "0");
        return chatLayout;
    }

    private void addItemForEachMessage(List<MessageEntity> messages, List<MessageListItem> items) {
        for (MessageEntity message : messages) {
            Instant messageSentTime = message.getSentTime()
                    .toInstant();
            String senderUsername = message.getSender()
                    .getUsername();

            MessageListItem item;
            if (message.getAttachments().isEmpty()) {
                item = new MessageListItem(message.getContent(),
                        messageSentTime, senderUsername, UserEntityUtils.getAvatarResource(message.getSender()
                        .getAvatarName()));
            } else {
                List<Attachment> attachmentResources =
                        conversationService.getMessageAttachmentResources(message.getAttachments());
                item = new MessageListItem(attachmentResources, message.getContent(),
                        messageSentTime, senderUsername, UserEntityUtils.getAvatarResource(message.getSender()
                        .getAvatarName()));
            }

            items.add(item);
        }
    }
}

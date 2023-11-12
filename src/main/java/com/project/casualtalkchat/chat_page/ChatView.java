package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.project.casualtalkchat.common.TopBar;
import com.project.casualtalkchat.common.UserEntityUtils;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.project.casualtalkchat.security.SecurityService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Route("chat")
@PermitAll
@Slf4j
@CssImport("./styles.css")
@PageTitle("CasualTalk")
public class ChatView extends VerticalLayout {

    public static final int CHAT_PAGE_SIZE = 15;
    private final UserService service;
    private final ConversationService conversationService;
    private final CustomUserDetails loggedInUserDetails;
    private final MessageList messageList;
    private final MultiFileBuffer buffer = new MultiFileBuffer();
    private final Upload upload = new Upload(buffer);
    private final ArrayList<Attachment> fileList = new ArrayList<>();
    private String currentConversationId;
    private List<MessageEntity> messagesWithPattern;
    private int currentMessageWithPatternIndex = 0;
    private String searchedKeyword;
    private Registration broadcasterRegistration;

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

        MessageDataProvider messageDataProvider =
                new MessageDataProvider(conversationService, 15, currentConversationId, loggedInUserDetails.getId());
        this.messageList = new MessageList(messageDataProvider);

        add(new TopBar(securityService), getPageLayout(loggedInUserDetails));
    }

    void changeChat(String currentConversationId) {

        this.currentConversationId = currentConversationId;
        log.debug("Chat was changed");
        messageList.setDataProvider(new MessageDataProvider(conversationService, CHAT_PAGE_SIZE, currentConversationId, loggedInUserDetails.getId()));
        messageList.reload();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register((newMessageDTO, conversationId) -> {
            if (conversationId.equals(currentConversationId)) {

                ui.access(() -> {

                    MessageListItem newMessage;
                    if (isContainingAttachments(newMessageDTO)) {
                        newMessage = new MessageListItem(newMessageDTO.getAttachments(),
                                newMessageDTO.getContent(), newMessageDTO.getSentTime(), newMessageDTO.getUserName(),
                                newMessageDTO.getUserAvatarResource());
                    } else {
                        newMessage = new MessageListItem(
                                newMessageDTO.getContent(), newMessageDTO.getSentTime(), newMessageDTO.getUserName(),
                                newMessageDTO.getUserAvatarResource());
                    }

                    if (!loggedInUserDetails.getId().equals(newMessageDTO.getSenderId())) {
                        newMessage.getStyle()
                                .setBackground("lightcyan");
                    }

                    messageList.addItems(List.of(newMessage));
                });
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private boolean isContainingAttachments(MessageDTO newMessageDTO) {
        return newMessageDTO.getAttachments() != null;
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

        MessageInput input = prepareMessageInput(userDetails);
        prepareUploadComponent(input);
        Button searchThroughHistoryButton = preparesearchThroughHistoryButton();
        Div searchBar = prepareSearchBar(searchThroughHistoryButton);

        VerticalLayout chatLayout =
                new VerticalLayout(searchBar, searchThroughHistoryButton, messageList, upload);
        chatLayout.setWidth(75, Unit.PERCENTAGE);
        chatLayout.getStyle()
                .set("border-left", "1px solid rgba(0,0,0,.5)")
                .setPosition(Style.Position.RELATIVE)
                .set("padding-bottom", "0");

        chatLayout.expand(messageList);
        return chatLayout;
    }

    private MessageInput prepareMessageInput(CustomUserDetails userDetails) {
        MessageInput input = new MessageInput();

        input.addSubmitListener(submitEvent -> {

            MessageDTO messageDTO;
            if (buffer.getFiles().isEmpty()) {
                messageDTO = MessageDTO.builder()
                        .content(submitEvent.getValue())
                        .sentTime(Instant.now())
                        .userName(userDetails.getUsername())
                        .userAvatarResource(UserEntityUtils.getAvatarResource(userDetails))
                        .senderId(userDetails.getId())
                        .build();
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

                messageDTO = MessageDTO.builder()
                        .content(submitEvent.getValue())
                        .sentTime(Instant.now())
                        .userName(userDetails.getUsername())
                        .userAvatarResource(UserEntityUtils.getAvatarResource(userDetails))
                        .attachments(attachmentResources)
                        .senderId(userDetails.getId())
                        .build();
            }
            Broadcaster.broadcast(messageDTO, currentConversationId);
        });
        return input;
    }

    private Button preparesearchThroughHistoryButton() {
        Button searchThroughHistoryButton = new Button(VaadinIcon.SEARCH.create());
        searchThroughHistoryButton.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setTop("10px")
                .setRight("50px")
                .setZIndex(100);
        return searchThroughHistoryButton;
    }

    private Div prepareSearchBar(Button searchThroughHistoryButton) {
        TextField searchInput = new TextField();
        Paragraph resultsCount = new Paragraph();
        searchInput.getStyle()
                .setWidth("-webkit-fill-available");
        searchInput.addKeyPressListener(Key.ENTER, e -> searchThrough(searchInput, resultsCount));

        resultsCount.getStyle()
                .set("margin-left", "5px")
                .set("margin-right", "5px");

        Button nextButton = new Button(VaadinIcon.ANGLE_UP.create());
        nextButton.addClickListener(e -> {
            if (currentMessageWithPatternIndex == messagesWithPattern.size() - 1) {
                currentMessageWithPatternIndex = 0;
            } else {
                currentMessageWithPatternIndex ++;
            }
            setFilteredPageToMessageList(messagesWithPattern.get(currentMessageWithPatternIndex));
        });

        Button previousButton = new Button(VaadinIcon.ANGLE_DOWN.create());
        previousButton.addClickListener(e -> {
            if (currentMessageWithPatternIndex == 0) {
                currentMessageWithPatternIndex = messagesWithPattern.size() - 1;
            } else {
                currentMessageWithPatternIndex --;
            }
            setFilteredPageToMessageList(messagesWithPattern.get(currentMessageWithPatternIndex));
        });

        previousButton.getStyle()
                .set("margin-left", "5px")
                .set("margin-right", "5px");
        Button closeBarButton = new Button(new Icon("lumo", "cross"));
        Button searchThroughButton = new Button(VaadinIcon.SEARCH.create());
        searchThroughButton.addClickListener(e -> searchThrough(searchInput, resultsCount));

        Div searchBar =
                new Div(searchInput, searchThroughButton, resultsCount, nextButton,
                        previousButton, closeBarButton);
        searchBar.getStyle()
                .setDisplay(Style.Display.NONE)
                .setWidth("100%");

        closeBarButton.addClickListener(e -> {
            searchThroughHistoryButton.getStyle().setDisplay(Style.Display.FLEX);
            searchBar.getStyle().setDisplay(Style.Display.NONE);
            messageList.disableLoadMoreRowsAtBottom();
            messageList.setDataProvider(new MessageDataProvider(conversationService, CHAT_PAGE_SIZE,
                    currentConversationId, loggedInUserDetails.getId()));
            messageList.reload();
        });

        searchThroughHistoryButton.addClickListener(e -> {
            searchThroughHistoryButton.getStyle().setDisplay(Style.Display.NONE);
            searchBar.getStyle().setDisplay(Style.Display.FLEX);
        });
        return searchBar;
    }

    private void searchThrough(TextField searchInput, Paragraph resultsCount) {
        searchedKeyword = searchInput.getValue();
        messagesWithPattern =
                conversationService.getMessagesWithPattern(currentConversationId, searchedKeyword);
        resultsCount.setText(String.valueOf(messagesWithPattern.size()));
        setFilteredPageToMessageList(messagesWithPattern.get(currentMessageWithPatternIndex));
    }

    private void prepareUploadComponent(MessageInput input) {
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
    }

    private void setFilteredPageToMessageList(MessageEntity messageWithPattern) {
        Page<MessageEntity> pageWithSpecificRow =
                conversationService.getMessagesPageWithSpecificRow(CHAT_PAGE_SIZE, currentConversationId,
                        messageWithPattern.getId());

        List<MessageListItem> messageListItems = new ArrayList<>();

        for (MessageEntity message : pageWithSpecificRow.getContent()) {
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

            if (message.getId().equals(messageWithPattern.getId())) {
                item.scrollIntoView();

                markKeywords(message, item);
            }
            messageListItems.add(item);
        }
        messageList.setItems(messageListItems);
        messageList.setPageNumber(pageWithSpecificRow.getNumber() + 1);
        messageList.enableLoadMoreRowsAtBottom();
    }

    private void markKeywords(MessageEntity message, MessageListItem item) {
        String messageContent = message.getContent();
        Paragraph modifiedMessage = new Paragraph();
        String lowerCaseMessageContent = message.getContent().toLowerCase();
        int lastKeywordIndex;
        while (true) {
            lastKeywordIndex = lowerCaseMessageContent.indexOf(searchedKeyword.toLowerCase());

            if (lastKeywordIndex == -1) {
                break;
            }

            String prefix = messageContent.substring(0, lastKeywordIndex);
            Span markedContent =
                    new Span(messageContent.substring(lastKeywordIndex, lastKeywordIndex + searchedKeyword.length()));

            markedContent.getStyle().setBackground("orange");

            modifiedMessage.add(prefix);
            modifiedMessage.add(markedContent);

            lowerCaseMessageContent = lowerCaseMessageContent.substring(lastKeywordIndex + searchedKeyword.length());
            messageContent = messageContent.substring(lastKeywordIndex + searchedKeyword.length());
        }
        modifiedMessage.add(messageContent);

        Component oldComponent = item.getElement()
                                    .getChild(1)
                                    .getChild(1)
                                    .getComponent()
                                    .get();
        ((Div) item.getComponentAt(1)).replace(oldComponent, modifiedMessage);
    }
}

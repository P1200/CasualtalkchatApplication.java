package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.UserEntityUtils;
import com.vaadin.flow.component.Component;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MessageDataProvider implements DataProvider {

    private final ConversationService service;
    private final int pageSize;
    private final String conversationId;

    @Override
    public List<Component> fetchPage(int pageNumber) {

        List<Component> messageListItems = new ArrayList<>();
        Page<MessageEntity> messageEntityPage =
                service.getMessagesList(PageRequest.of(pageNumber, pageSize), conversationId);

        for (MessageEntity message : messageEntityPage) {
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
                        service.getMessageAttachmentResources(message.getAttachments());
                item = new MessageListItem(attachmentResources, message.getContent(),
                        messageSentTime, senderUsername, UserEntityUtils.getAvatarResource(message.getSender()
                        .getAvatarName()));
            }
            messageListItems.add(item);
        }
        return messageListItems;
    }
}

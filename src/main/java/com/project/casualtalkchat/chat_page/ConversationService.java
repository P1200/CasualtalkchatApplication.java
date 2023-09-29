package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.UserEntityUtils;
import com.vaadin.flow.component.messages.MessageListItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository repository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository repository, UserRepository userRepository, MessageRepository messageRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public void createNewConversation(String adminId, UserEntity member) {

        UserEntity admin = userRepository.getReferenceById(adminId);

        ConversationEntity conversation = ConversationEntity.builder()
                .name(null)
                .members(Set.of(admin, member))
                .admins(Set.of(admin))
                .build();

        repository.save(conversation);
    }

    public List<ConversationEntity> getUserConversations(String userId) {
        return repository.getAllByAdminsIdOrMembersId(userId, userId);
    }

    public void removeConversation(ConversationEntity conversation) {
        repository.delete(conversation);
    }

    @Transactional
    public List<MessageListItem> getMessagesList(String conversationId) {
        LinkedHashSet<MessageEntity> messages = getSortedMessages(conversationId);

        List<MessageListItem> items = new ArrayList<>();
        addItemForEachMessage(messages, items);

        return items;
    }

    @Transactional
    public void saveMessage(String conversationId, String senderId, String text, Instant time) {
        ConversationEntity conversation = repository.getReferenceById(conversationId);

        UserEntity sender = userRepository.getReferenceById(senderId);

        MessageEntity message = MessageEntity.builder()
                .type(MessageType.TEXT)
                .sender(sender)
                .sentTime(Timestamp.from(time))
                .content(text)
                .build();

        messageRepository.save(message);
        conversation.getMessages().add(message);
        repository.save(conversation);
    }

    private LinkedHashSet<MessageEntity> getSortedMessages(String conversationId) {
        return repository.getReferenceById(conversationId)
                .getMessages().stream().sorted(Comparator.comparing(MessageEntity::getSentTime)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void addItemForEachMessage(LinkedHashSet<MessageEntity> messages, List<MessageListItem> items) {
        for (MessageEntity message : messages) {
            Instant messageSentTime = message.getSentTime().toInstant();
            String senderUsername = message.getSender().getUsername();
            MessageListItem item = new MessageListItem(message.getContent(),
                    messageSentTime, senderUsername);
            item.setUserImageResource(UserEntityUtils.getAvatarResource(message.getSender()));

            items.add(item);
        }
    }
}

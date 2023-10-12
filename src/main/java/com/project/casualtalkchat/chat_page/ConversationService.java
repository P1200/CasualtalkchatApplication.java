package com.project.casualtalkchat.chat_page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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
                                                            .name(member.getUsername())
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
    public List<MessageEntity> getMessagesList(String conversationId) {
        return messageRepository.getAllByConversationIdOrderBySentTime(conversationId);
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
                                            .conversation(conversation)
                                            .build();

        messageRepository.save(message);
    }
}

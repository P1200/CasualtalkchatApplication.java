package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.FileCouldNotBeGetException;
import com.project.casualtalkchat.common.UserImagesRepository;
import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.vaadin.flow.server.InputStreamFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Slf4j
public class ConversationService {

    private static final String PATH_TO_ATTACHMENTS = "attachments/";

    private final Supplier<UUID> randomUUID = UUID::randomUUID;
    private final ConversationRepository repository;
    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserImagesRepository resourcesRepository;

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
        return repository.getAllByAdminsIdOrMembersIdSortedByLastMessageSentTime(userId);
    }

    @Transactional
    public void removeConversation(ConversationEntity conversation) {
        messageRepository.deleteAllByConversationId(conversation.getId());
        repository.delete(conversation);
    }

    @Transactional
    public Page<MessageEntity> getMessagesList(PageRequest pageRequest, String conversationId) {
        return messageRepository.getAllByConversationIdOrderBySentTimeDesc(pageRequest, conversationId);
    }

    @Transactional
    public void saveMessage(String conversationId, String senderId, String text, Instant time) {
        ConversationEntity conversation = repository.getReferenceById(conversationId);

        UserEntity sender = userRepository.getReferenceById(senderId);
        Set<UserEntity> membersWhoNotViewed = new HashSet<>(conversation.getMembers());
        membersWhoNotViewed.remove(sender);
        conversation.setMembersWhoNotViewed(membersWhoNotViewed);

        MessageEntity message = MessageEntity.builder()
                                            .sender(sender)
                                            .sentTime(Timestamp.from(time))
                                            .content(text)
                                            .conversation(conversation)
                                            .membersWhoNotViewed(membersWhoNotViewed)
                                            .build();

        messageRepository.save(message);
    }

    @Transactional
    public MessageEntity saveMessage(String conversationId, String senderId, String text, List<Attachment> attachments,
                            Instant time) throws FileCouldNotBeSavedException {
        ConversationEntity conversation = repository.getReferenceById(conversationId);

        UserEntity sender = userRepository.getReferenceById(senderId);
        Set<UserEntity> membersWhoNotViewed = new HashSet<>(conversation.getMembers());
        membersWhoNotViewed.remove(sender);
        conversation.setMembersWhoNotViewed(membersWhoNotViewed);

        List<AttachmentEntity> attachmentEntities = prepareAttachmentEntities(conversationId, attachments);
        attachmentRepository.saveAll(attachmentEntities);
        MessageEntity message = MessageEntity.builder()
                .sender(sender)
                .sentTime(Timestamp.from(time))
                .content(text)
                .conversation(conversation)
                .attachments(attachmentEntities)
                .membersWhoNotViewed(membersWhoNotViewed)
                .build();

        messageRepository.save(message);
        return message;
    }

    public List<Attachment> getMessageAttachmentResources(List<AttachmentEntity> attachmentEntities) {

        List<Attachment> attachments = new ArrayList<>();

        for (AttachmentEntity attachmentEntity : attachmentEntities) {
            try {
                InputStreamFactory streamFactory = resourcesRepository.getFile(attachmentEntity.getPathToContent());
                attachments.add(new Attachment(streamFactory, attachmentEntity.getType(), attachmentEntity.getUserFileName()));
            } catch (FileCouldNotBeGetException e) {
                log.error(e.getMessage());
            }
        }
        return attachments;
    }

    public List<MessageEntity> getMessagesWithPattern(String conversationId, String pattern) {
        String wildcardPattern = "%" + pattern + "%";
        return messageRepository.getAllByConversationIdAndContentLikeOrderBySentTimeDesc(conversationId, wildcardPattern);
    }

    @Transactional
    public Page<MessageEntity> getMessagesPageWithSpecificRow(int pageSize, String conversationId, String specificRowId) {

        int position = messageRepository.getMessagePositionInConversationById(conversationId, specificRowId);
        int pageNumber = 0;

        for (int i = pageSize; i < position; i*=2) {
            pageNumber ++;
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return messageRepository.getAllByConversationIdOrderBySentTimeDesc(pageRequest, conversationId);
    }

    @Transactional
    public void markConversationAsViewedBy(String currentConversationId, String userId) {
        ConversationEntity conversationEntity = repository.getReferenceById(currentConversationId);
        conversationEntity.getMembersWhoNotViewed()
                            .removeIf(member -> userId.equals(member.getId()));
    }

    @Transactional
    public void markMessageAsViewedBy(String conversationId, String userId) {
        MessageEntity messageEntity = messageRepository.getReferenceById(conversationId);
        messageEntity.getMembersWhoNotViewed()
                    .removeIf(member -> userId.equals(member.getId()));
    }

    @Transactional
    public void addUserToConversation(String conversationId, String userId) {
        ConversationEntity conversationEntity = repository.getReferenceById(conversationId);
        UserEntity userEntity = userRepository.getReferenceById(userId);
        conversationEntity.getMembers()
                          .add(userEntity);
    }

    public boolean isConversationAdmin(String conversationId, String userId) {
        return repository.isConversationAdmin(conversationId, userId);
    }

    @Transactional
    public void removeFromConversation(String conversationId, String userId) {
        ConversationEntity conversationEntity = repository.getReferenceById(conversationId);
        UserEntity member = userRepository.getReferenceById(userId);
        conversationEntity.getMembers()
                            .remove(member);
    }

    private List<AttachmentEntity> prepareAttachmentEntities(String conversationId, List<Attachment> attachments)
            throws FileCouldNotBeSavedException {
        List<AttachmentEntity> attachmentEntities = new ArrayList<>();

        for (Attachment attachment : attachments) {
            String generatedFileName = randomUUID.get().toString();
            String pathToContent = PATH_TO_ATTACHMENTS + conversationId + "/" + generatedFileName;
            try (InputStream inputStream = attachment.getImage().createInputStream()) {
                resourcesRepository.saveFile(pathToContent, inputStream.readAllBytes());
            } catch (Exception e) {
                log.error("File couldn't be saved in: " + pathToContent + ".");
                throw new FileCouldNotBeSavedException(pathToContent);
            }
            AttachmentEntity attachmentEntity = AttachmentEntity.builder()
                    .pathToContent(pathToContent)
                    .type(attachment.getMime())
                    .userFileName(attachment.getFileName())
                    .build();
            attachmentEntities.add(attachmentEntity);
        }
        return attachmentEntities;
    }
}

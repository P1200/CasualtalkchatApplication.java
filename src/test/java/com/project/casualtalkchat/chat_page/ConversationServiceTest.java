package com.project.casualtalkchat.chat_page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    private static final String ADMIN_ID = "d9244264-473d-426e-97bf-07afa5846881";
    public static final String USERNAME = "madame89";
    public static final String ADMIN_USERNAME = "admin2023";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String ADMIN_EMAIL = "admin2023@gmail.com";
    public static final String CONVERSATION_ID = "e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb";
    public static final String FIRST_MESSAGE_TEXT = "Hello Suzie!";
    public static final String SECOND_MESSAGE_TEXT = "Hello Mark!";
    public static final Instant FIRST_MESSAGE_SENT_TIME = Instant.parse("2023-10-08T10:15:30.00Z");
    public static final Instant SECOND_MESSAGE_SENT_TIME = Instant.parse("2023-10-08T10:15:30.00Z");

    @Mock
    private ConversationRepository repository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldCreateNewConversation() { //TODO add IT tests for this method
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);
        when(userRepository.getReferenceById(anyString()))
                .thenReturn(prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL));

        //When
        service.createNewConversation(ADMIN_ID, prepareUserEntity(USER_ID, USERNAME, EMAIL));

        //Then
        verify(repository, times(1)).save(any(ConversationEntity.class));
    }

    @Test
    void shouldGetUserConversations() { //TODO add IT tests for this method
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);
        List<ConversationEntity> conversationEntities = prepareConversations();
        when(repository.getAllByAdminsIdOrMembersId(anyString(), anyString())).thenReturn(conversationEntities);

        //When
        List<ConversationEntity> userConversations = service.getUserConversations(USER_ID);

        //Then
        assertEquals(conversationEntities, userConversations);
    }

    @Test
    void shouldRemoveConversation() {
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);

        //When
        service.removeConversation(prepareConversationEntityWithoutMessages(prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL),
                prepareUserEntity(USER_ID, USERNAME, EMAIL)));

        //Then
        verify(repository, times(1)).delete(any(ConversationEntity.class));
    }

    @Test
    void shouldGetConversationMessages() {
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);
        UserEntity userEntity = prepareUserEntity(USER_ID, USERNAME, EMAIL);
        UserEntity adminUserEntity = prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL);
        List<MessageEntity> messageEntities =
                List.of(prepareMessageEntity(MessageType.TEXT, userEntity, Timestamp.from(FIRST_MESSAGE_SENT_TIME), FIRST_MESSAGE_TEXT),
                        prepareMessageEntity(MessageType.TEXT, adminUserEntity, Timestamp.from(SECOND_MESSAGE_SENT_TIME), SECOND_MESSAGE_TEXT));
        when(messageRepository.getAllByConversationIdOrderBySentTime(CONVERSATION_ID)).thenReturn(messageEntities);

        //When
        List<MessageEntity> messages = service.getMessagesList(CONVERSATION_ID);

        //Then
        assertEquals(messageEntities, messages);
    }

    @Test
    void shouldGetConversationMessageEmptyList() {
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);
        List<MessageEntity> messageEntities = Collections.emptyList();
        when(messageRepository.getAllByConversationIdOrderBySentTime(CONVERSATION_ID)).thenReturn(messageEntities);

        //When
        List<MessageEntity> messages = service.getMessagesList(CONVERSATION_ID);

        //Then
        assertEquals(Collections.emptyList(), messages);
    }

    @Test
    void shouldSaveNewMessage() {
        //Given
        ConversationService service = new ConversationService(repository, userRepository, messageRepository);
        when(userRepository.getReferenceById(anyString())).thenReturn(prepareUserEntity(USER_ID, USERNAME, EMAIL));
        when(repository.getReferenceById(CONVERSATION_ID))
                .thenReturn(prepareConversationEntityWithoutMessages(prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL),
                                                        prepareUserEntity(USER_ID, USERNAME, EMAIL)));

        //When
        service.saveMessage(CONVERSATION_ID, USER_ID, FIRST_MESSAGE_TEXT, FIRST_MESSAGE_SENT_TIME);

        //Then
        verify(messageRepository, times(1)).save(any(MessageEntity.class));
    }

    private UserEntity prepareUserEntity(String userId, String username, String email) {
        return UserEntity.builder()
                .id(userId)
                .username(username)
                .email(email)
                .build();
    }

    private List<ConversationEntity> prepareConversations() {

        ArrayList<ConversationEntity> conversationEntities = new ArrayList<>();
        UserEntity user1 = prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL);
        UserEntity user2 = prepareUserEntity(USER_ID, USERNAME, EMAIL);

        conversationEntities.add(prepareConversationEntityWithoutMessages(user1, user2));
        conversationEntities.add(prepareConversationEntityWithoutMessages(user2, user1));

        return conversationEntities;
    }

    private MessageEntity prepareMessageEntity(MessageType messageType, UserEntity sender, Timestamp sentTime, String content) {
        return MessageEntity.builder()
                            .type(messageType)
                            .sender(sender)
                            .sentTime(sentTime)
                            .content(content)
                            .build();
    }

    private ConversationEntity prepareConversationEntityWithoutMessages(UserEntity admin, UserEntity member) {
        return ConversationEntity.builder()
                .id(CONVERSATION_ID)
                .name(null)
                .members(Set.of(admin, member))
                .admins(Set.of(admin))
                .build();
    }
}
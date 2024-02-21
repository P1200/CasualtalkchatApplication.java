package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.common.UserImagesRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static com.helger.commons.mock.CommonsAssert.assertEquals;

@SpringBootTest
class ConversationServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    private static final String ADMIN_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    public static final String USERNAME = "madame89";
    public static final String ADMIN_USERNAME = "Paul";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String ADMIN_EMAIL = "paul@gmail.com";
    public static final String CONVERSATION_ID = "e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb";
    public static final String EMPTY_CONVERSATION_ID = "ad7feeec-dbb9-4e37-ad72-a1e5f24a2c6f";
    public static final String FIRST_MESSAGE_TEXT = "Hello Suzie!";
    public static final String SECOND_MESSAGE_TEXT = "Hello Mark!";
    public static final Instant FIRST_MESSAGE_SENT_TIME = Instant.parse("2023-10-08T10:15:30.00Z");
    public static final Instant SECOND_MESSAGE_SENT_TIME = Instant.parse("2023-10-08T10:15:30.00Z");
    public static final String FIRST_MESSAGE_ID = "6c62098e-9bcb-46a9-a19b-17ab210aacef";
    public static final String SECOND_MESSAGE_ID = "e8d483aa-8dfd-4cf4-9301-882b5d8b3000";

    @Autowired
    private ConversationRepository repository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserImagesRepository resourcesRepository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldCreateNewConversation() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);

        //When
        var conversationsCountBeforeAddTheNewOne = repository.findAll().size();
        service.createNewConversation(ADMIN_ID, prepareUserEntity(USER_ID, USERNAME, EMAIL));

        //Then
        assertEquals(repository.findAll().size(), ++ conversationsCountBeforeAddTheNewOne);
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldGetUserConversations() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);
        List<ConversationEntity> conversationEntities = prepareConversations();

        //When
        List<ConversationEntity> userConversations = service.getUserConversations(USER_ID);

        //Then
        assertEquals(conversationEntities, userConversations);
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldRemoveConversation() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);

        //When
        var conversationsCountBeforeDelete = repository.findAll().size();
        service.removeConversation(prepareConversationEntityWithoutMessages(prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL),
                prepareUserEntity(USER_ID, USERNAME, EMAIL), CONVERSATION_ID));

        //Then
        assertEquals(repository.findAll().size(), -- conversationsCountBeforeDelete);
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldGetConversationMessages() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);
        UserEntity userEntity = prepareUserEntity(USER_ID, USERNAME, EMAIL);
        UserEntity adminUserEntity = prepareUserEntity(ADMIN_ID, ADMIN_USERNAME, ADMIN_EMAIL);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<MessageEntity> messageEntities =
                List.of(prepareMessageEntity(FIRST_MESSAGE_ID, userEntity, Timestamp.from(FIRST_MESSAGE_SENT_TIME), FIRST_MESSAGE_TEXT),
                        prepareMessageEntity(SECOND_MESSAGE_ID, adminUserEntity, Timestamp.from(SECOND_MESSAGE_SENT_TIME), SECOND_MESSAGE_TEXT));

        //When
        List<MessageEntity> messages = service.getMessagesList(pageRequest, CONVERSATION_ID)
                                                .toList();

        //Then
        assertEquals(messageEntities.toString(), messages.toString());
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldGetConversationMessageEmptyList() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);
        List<MessageEntity> messageEntities = Collections.emptyList();
        PageRequest pageRequest = PageRequest.of(0, 10);

        //When
        List<MessageEntity> messages = service.getMessagesList(pageRequest, EMPTY_CONVERSATION_ID)
                                                .toList();

        //Then
        assertEquals(messageEntities.toString(), messages.toString());
    }

    @Test
    @Sql("/db-scripts/conversation-service-service-it.sql")
    @Transactional
    void shouldSaveNewMessage() {
        //Given
        ConversationService service =
                new ConversationService(repository, attachmentRepository, messageRepository, userRepository, resourcesRepository);
        PageRequest pageRequest = PageRequest.of(0, 10);

        //When
        service.saveMessage(EMPTY_CONVERSATION_ID, USER_ID, FIRST_MESSAGE_TEXT, FIRST_MESSAGE_SENT_TIME);

        //Then
        int expectedNumberOfElements = 1;
        assertEquals(messageRepository.getAllByConversationIdOrderBySentTimeDesc(pageRequest, EMPTY_CONVERSATION_ID)
                                    .getNumberOfElements(), expectedNumberOfElements);
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

        conversationEntities.add(prepareConversationEntityWithoutMessages(user2, user1, CONVERSATION_ID));
        conversationEntities.add(prepareConversationEntityWithoutMessages(user2, user1, EMPTY_CONVERSATION_ID));

        return conversationEntities;
    }

    private MessageEntity prepareMessageEntity(String id, UserEntity sender, Timestamp sentTime, String content) {
        return MessageEntity.builder()
                            .id(id)
                            .sender(sender)
                            .sentTime(sentTime)
                            .content(content)
                            .attachments(new ArrayList<>())
                            .membersWhoNotViewed(new HashSet<>())
                            .build();
    }

    private ConversationEntity prepareConversationEntityWithoutMessages(UserEntity admin, UserEntity member, String conversationId) {
        return ConversationEntity.builder()
                .id(conversationId)
                .name(member.getUsername())
                .members(Set.of(admin, member))
                .admins(Set.of(admin))
                .membersWhoNotViewed(new HashSet<>())
                .build();
    }
}
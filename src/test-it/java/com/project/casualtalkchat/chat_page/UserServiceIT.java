package com.project.casualtalkchat.chat_page;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String RECEIVER_ID = "64550bdc-9162-4efa-8304-2430a0ac8cd9";
    private static final String SENDER_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    private static final String FRIEND_ID = "2ce910d8-d9c5-4b59-ba9c-01bd7c7ab47e";
    public static final String RECEIVER_USERNAME = "Pablo";
    public static final String SENDER_USERNAME = "Paul";
    public static final String RECEIVER_EMAIL = "pablo@gmail.com";
    public static final String SENDER_EMAIL = "paul@gmail.com";

    @Autowired
    private UserRepository repository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void shouldAddInvitationForGivenUserWhenThereAreNoInvitationsYet() {
        //Given
        UserService userService = new UserService(repository);

        //When
        userService.addUserAsInvited(SENDER_ID, prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));

        //Then
        assertEquals(1, repository.getReferenceById(SENDER_ID).getInvitations().size());
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void shouldAcceptInvitation() {
        //Given
        UserService userService = new UserService(repository);

        //When
        userService.acceptInvitation(RECEIVER_ID, prepareInviterUserEntity());

        //Then
        assertEquals(1, repository.findAllFriends(SENDER_ID).size());
        assertEquals(0, repository.getReferenceById(SENDER_ID).getInvitations().size());
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void shouldRemoveInvitation() {
        //Given
        UserService userService = new UserService(repository);

        //When
        userService.removeInvitation(RECEIVER_ID, prepareInviterUserEntity());

        //Then
        assertEquals(0, repository.getReferenceById(SENDER_ID).getInvitations().size());
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void shouldGetAllNonFriendUsers() {
        //Given
        UserService userService = new UserService(repository);

        //When
        List<UserEntity> allNonFriendUsers = userService.getAllNonFriendUsers(RECEIVER_ID);

        //Then
        assertEquals(List.of(prepareUserEntity(SENDER_ID, SENDER_EMAIL, SENDER_USERNAME)),
                            allNonFriendUsers);
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void getInvitationsToUser() {
        //Given
        UserService userService = new UserService(repository);

        //When
        List<UserEntity> invitations = userService.getInvitationsToUser(RECEIVER_ID);

        //Then
        assertEquals(List.of(prepareInviterUserEntity()), invitations);
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void getAllFriends() {
        //Given
        UserService userService = new UserService(repository);

        //When
        List<UserEntity> friends = userService.getAllFriends(FRIEND_ID);

        //Then
        assertEquals(List.of(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME)), friends);
    }

    @Test
    @Sql("/db-scripts/user-service-it.sql")
    @Transactional
    void shouldRemoveFriend() {
        //Given
        UserService userService = new UserService(repository);

        //When
        userService.removeFriend(FRIEND_ID, prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));

        //Then
        assertEquals(Collections.emptySet(),
                repository.getReferenceById(FRIEND_ID).getFriends());
    }

    private UserEntity prepareUserEntity(String receiverId, String receiverEmail, String receiverUsername) {
        return UserEntity.builder()
                .id(receiverId)
                .email(receiverEmail)
                .username(receiverUsername)
                .invitations(new HashSet<>())
                .friends(new HashSet<>())
                .build();
    }

    private UserEntity prepareInviterUserEntity() {
        UserEntity inviter = prepareUserEntity(SENDER_ID, SENDER_EMAIL, SENDER_USERNAME);
        inviter.getInvitations().add(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));
        return inviter;
    }
}
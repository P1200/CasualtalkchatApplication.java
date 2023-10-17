package com.project.casualtalkchat.chat_page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String RECEIVER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    private static final String SENDER_ID = "d9244264-473d-426e-97bf-07afa5846881";
    public static final String RECEIVER_USERNAME = "madame89";
    public static final String SENDER_USERNAME = "admin2023";
    public static final String RECEIVER_EMAIL = "cristinefreud89@gmail.com";
    public static final String SENDER_EMAIL = "admin2023@gmail.com";

    @Mock
    private UserRepository repository;

    @Test
    void shouldAddInvitationForGivenUserWhenThereAreNoInvitationsYet() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.getReferenceById(anyString())).thenReturn(prepareInviterUserEntity());

        //When
        userService.addUserAsInvited(SENDER_ID, prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));

        //Then
        verify(repository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void shouldAcceptInvitation() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.getReferenceById(anyString())).thenReturn(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));
        when(repository.getReferenceById(anyString())).thenReturn(prepareInviterUserEntity());

        //When
        userService.acceptInvitation(RECEIVER_ID, prepareInviterUserEntity());

        //Then
        verify(repository, times(2)).save(any(UserEntity.class));
    }

    @Test
    void shouldRemoveInvitation() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.getReferenceById(RECEIVER_ID))
                .thenReturn(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));
        when(repository.getReferenceById(SENDER_ID)).thenReturn(prepareInviterUserEntity());

        //When
        userService.removeInvitation(RECEIVER_ID, prepareInviterUserEntity());

        //Then
        verify(repository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void shouldGetAllNonFriendUsers() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.findAllNonFriendUsers(anyString()))
                .thenReturn(List.of(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME)));

        //When
        List<UserEntity> allNonFriendUsers = userService.getAllNonFriendUsers(SENDER_ID);

        //Then
        assertEquals(List.of(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME)).toString(),
                            allNonFriendUsers.toString());
    }

    @Test
    void getInvitationsToUser() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.findAllByInvitationsId(anyString())).thenReturn(List.of(prepareInviterUserEntity()));

        //When
        List<UserEntity> invitations = userService.getInvitationsToUser(RECEIVER_ID);

        //Then
        assertEquals(List.of(prepareInviterUserEntity()).toString(), invitations.toString());
    }

    @Test
    void getAllFriends() {
        //Given
        UserService userService = new UserService(repository);
        when(repository.findAllFriends(anyString())).thenReturn(List.of(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME)));

        //When
        List<UserEntity> friends = userService.getAllFriends(SENDER_ID);

        //Then
        assertEquals(List.of(prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME)).toString(), friends.toString());
    }

    @Test
    void removeFriend() {
        //Given
        UserService userService = new UserService(repository);

        //When
        userService.removeFriend(SENDER_ID, prepareUserEntity(RECEIVER_ID, RECEIVER_EMAIL, RECEIVER_USERNAME));

        //Then
        verify(repository, times(1)).removeFriend(anyString(), anyString());
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
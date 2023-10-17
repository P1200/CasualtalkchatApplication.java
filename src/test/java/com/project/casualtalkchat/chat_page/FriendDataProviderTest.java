package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendDataProviderTest {

    public static final String LOGGED_USER_ID = "e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb";
    private static final String FIRST_USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    private static final String SECOND_USER_ID = "d9244264-473d-426e-97bf-07afa5846881";
    public static final String FIRST_USERNAME = "madame89";
    public static final String SECOND_USERNAME = "admin2023";
    public static final String FIRST_EMAIL = "cristinefreud89@gmail.com";
    public static final String SECOND_EMAIL = "admin2023@gmail.com";

    @Mock
    private UserService service;
    @Spy
    private Query<UserEntity, UserFilter> userFilterQuery;

    @Test
    void shouldRefreshAll() {
        //Given
        FriendDataProvider friendDataProvider = new FriendDataProvider(service, LOGGED_USER_ID);

        //When
        friendDataProvider.refreshAll();

        //Then
        verify(service, times(2)).getAllFriends(anyString());
    }

    @Test
    void shouldFetchFromBackEndWhenThereIsNoFriends() {
        //Given
        when(service.getAllFriends(anyString())).thenReturn(Collections.emptyList());
        FriendDataProvider friendDataProvider = new FriendDataProvider(service, LOGGED_USER_ID);

        //When
        Stream<UserEntity> userEntityStream = friendDataProvider.fetchFromBackEnd(userFilterQuery);

        //Then
        assertEquals(Collections.emptyList().toString(), userEntityStream.toList().toString());
    }

    @Test
    void shouldFetchFromBackEnd() {
        //Given
        when(service.getAllFriends(anyString()))
                .thenReturn(List.of(prepareUserEntity(FIRST_USER_ID, FIRST_USERNAME, FIRST_EMAIL)));
        FriendDataProvider friendDataProvider = new FriendDataProvider(service, LOGGED_USER_ID);

        //When
        Stream<UserEntity> userEntityStream = friendDataProvider.fetchFromBackEnd(userFilterQuery);

        //Then
        assertEquals(List.of(prepareUserEntity(FIRST_USER_ID, FIRST_USERNAME, FIRST_EMAIL)).toString(),
                userEntityStream.toList().toString());
    }

    @Test
    void shouldFetchFromBackEndWithFilter() {
        //Given
        when(service.getAllFriends(anyString()))
                .thenReturn(List.of(prepareUserEntity(FIRST_USER_ID, FIRST_USERNAME, FIRST_EMAIL),
                        prepareUserEntity(SECOND_USER_ID, SECOND_USERNAME, SECOND_EMAIL)));
        FriendDataProvider friendDataProvider = new FriendDataProvider(service, LOGGED_USER_ID);

        UserFilter userFilter = new UserFilter();
        userFilter.setSearchTerm("admin");
        when(userFilterQuery.getFilter()).thenReturn(Optional.of(userFilter));

        //When
        Stream<UserEntity> userEntityStream = friendDataProvider.fetchFromBackEnd(userFilterQuery);

        //Then
        assertEquals(List.of(prepareUserEntity(SECOND_USER_ID, SECOND_USERNAME, SECOND_EMAIL)).toString(),
                            userEntityStream.toList().toString());
    }

    @Test
    void shouldGetSizeInBackEnd() {
        //Given
        when(service.getAllFriends(anyString()))
                .thenReturn(List.of(prepareUserEntity(FIRST_USER_ID, FIRST_USERNAME, FIRST_EMAIL),
                        prepareUserEntity(SECOND_USER_ID, SECOND_USERNAME, SECOND_EMAIL)));
        FriendDataProvider friendDataProvider = new FriendDataProvider(service, LOGGED_USER_ID);

        UserFilter userFilter = new UserFilter();
        userFilter.setSearchTerm("");
        when(userFilterQuery.getFilter()).thenReturn(Optional.of(userFilter));
        short expectedDataSize = 2;

        //When
        int sizeInBackEnd = friendDataProvider.sizeInBackEnd(userFilterQuery);

        //Then
        assertEquals(expectedDataSize, sizeInBackEnd);
    }

    private UserEntity prepareUserEntity(String userId, String username, String email) {
        return UserEntity.builder()
                .id(userId)
                .username(username)
                .email(email)
                .build();
    }
}
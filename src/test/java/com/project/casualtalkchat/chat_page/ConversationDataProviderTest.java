package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationDataProviderTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String FIRST_USERNAME = "madame89";
    public static final String SECOND_USERNAME = "admin2023";
    public static final String FIRST_CONVERSATION_ID = "e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb";
    public static final String SECOND_CONVERSATION_ID = "76ec2570-3c11-4599-8b1b-7e07b623f3f5";

    @Mock
    private ConversationService service;
    @Spy
    private Query<ConversationEntity, ConversationFilter> conversationFilterQuery;

    @Test
    void shouldRefreshAll() {
        //Given
        ConversationDataProvider ConversationDataProvider = new ConversationDataProvider(service, USER_ID);

        //When
        ConversationDataProvider.refreshAll();

        //Then
        verify(service, times(2)).getUserConversations(anyString());
    }

    @Test
    void shouldFetchFromBackEndWhenThereIsNoFriends() {
        //Given
        when(service.getUserConversations(anyString())).thenReturn(Collections.emptyList());
        ConversationDataProvider ConversationDataProvider = new ConversationDataProvider(service, USER_ID);

        //When
        Stream<ConversationEntity> conversationEntityStream = ConversationDataProvider.fetchFromBackEnd(conversationFilterQuery);

        //Then
        assertEquals(Collections.emptyList().toString(), conversationEntityStream.toList().toString());
    }

    @Test
    void shouldFetchFromBackEnd() {
        //Given
        when(service.getUserConversations(anyString()))
                .thenReturn(List.of(prepareConversationEntity(FIRST_CONVERSATION_ID, FIRST_USERNAME)));
        ConversationDataProvider ConversationDataProvider = new ConversationDataProvider(service, USER_ID);

        //When
        Stream<ConversationEntity> conversationEntityStream =
                ConversationDataProvider.fetchFromBackEnd(conversationFilterQuery);

        //Then
        assertEquals(List.of(prepareConversationEntity(FIRST_CONVERSATION_ID, FIRST_USERNAME)).toString(),
                                conversationEntityStream.toList().toString());
    }

    @Test
    void shouldFetchFromBackEndWithFilter() {
        //Given
        when(service.getUserConversations(anyString()))
                .thenReturn(List.of(prepareConversationEntity(FIRST_CONVERSATION_ID, FIRST_USERNAME),
                        prepareConversationEntity(SECOND_CONVERSATION_ID, SECOND_USERNAME)));
        ConversationDataProvider ConversationDataProvider = new ConversationDataProvider(service, USER_ID);

        ConversationFilter conversationFilter = new ConversationFilter();
        conversationFilter.setSearchTerm("89");
        when(conversationFilterQuery.getFilter()).thenReturn(Optional.of(conversationFilter));

        //When
        Stream<ConversationEntity> conversationEntityStream =
                ConversationDataProvider.fetchFromBackEnd(conversationFilterQuery);

        //Then
        assertEquals(List.of(prepareConversationEntity(FIRST_CONVERSATION_ID, FIRST_USERNAME)).toString(),
                    conversationEntityStream.toList().toString());
    }

    @Test
    void shouldGetSizeInBackEnd() {
        //Given
        when(service.getUserConversations(anyString()))
                .thenReturn(List.of(prepareConversationEntity(FIRST_CONVERSATION_ID, FIRST_USERNAME),
                        prepareConversationEntity(SECOND_CONVERSATION_ID, SECOND_USERNAME)));
        ConversationDataProvider ConversationDataProvider = new ConversationDataProvider(service, USER_ID);

        ConversationFilter conversationFilter = new ConversationFilter();
        conversationFilter.setSearchTerm("");
        when(conversationFilterQuery.getFilter()).thenReturn(Optional.of(conversationFilter));
        short expectedDataSize = 2;

        //When
        int sizeInBackEnd = ConversationDataProvider.sizeInBackEnd(conversationFilterQuery);

        //Then
        assertEquals(expectedDataSize, sizeInBackEnd);
    }

    private ConversationEntity prepareConversationEntity(String conversationId, String username) {
        return ConversationEntity.builder()
                .id(conversationId)
                .name(username)
                .admins(new HashSet<>())
                .members(new HashSet<>())
                .build();
    }
}
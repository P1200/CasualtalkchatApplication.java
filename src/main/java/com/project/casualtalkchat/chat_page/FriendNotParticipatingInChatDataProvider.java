package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

public class FriendNotParticipatingInChatDataProvider extends AbstractBackEndDataProvider<UserEntity, UserFilter> {

    private final UserService userService;
    private final String currentUserId;
    private final String conversationId;
    private List<UserEntity> userEntities;

    public FriendNotParticipatingInChatDataProvider(UserService service, String currentUserId, String conversationId) {
        userService = service;
        this.currentUserId = currentUserId;
        this.conversationId = conversationId;
        userEntities = service.getAllFriendsNotParticipatingInChat(currentUserId, conversationId); //TODO I think it's not good to get all data at once
    }

    @Override
    public void refreshAll() {
        userEntities = userService.getAllFriendsNotParticipatingInChat(currentUserId, conversationId);
        super.refreshAll();
    }

    @Override
    protected Stream<UserEntity> fetchFromBackEnd(Query<UserEntity, UserFilter> query) {
        Stream<UserEntity> stream = userEntities.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(person -> query.getFilter().get().test(person));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    protected int sizeInBackEnd(Query<UserEntity, UserFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}

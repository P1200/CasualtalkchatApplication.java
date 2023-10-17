package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

public class FriendDataProvider extends AbstractBackEndDataProvider<UserEntity, UserFilter> {

    private final UserService userService;
    private final String currentUserId;
    private List<UserEntity> userEntities;

    public FriendDataProvider(UserService service, String currentUserId) {
        userService = service;
        this.currentUserId = currentUserId;
        userEntities = service.getAllFriends(currentUserId); //TODO I think it's not good to get all data at once
    }

    @Override
    public void refreshAll() {
        userEntities = userService.getAllFriends(currentUserId);
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

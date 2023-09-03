package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

public class UserDataProvider extends AbstractBackEndDataProvider<UserEntity, UserFilter> {

    private final List<UserEntity> userEntities;

    public UserDataProvider(UserService service, String currentUserId) {
        userEntities = service.getAllNonFriendUsers(currentUserId); //TODO I think it's not good to get all data at once
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

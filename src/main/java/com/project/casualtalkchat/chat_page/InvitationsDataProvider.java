package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import java.util.List;
import java.util.stream.Stream;

public class InvitationsDataProvider extends AbstractBackEndDataProvider<UserEntity, UserFilter> {

    private final UserService service;
    private final String userId;
    private List<UserEntity> userEntities;

    public InvitationsDataProvider(UserService service, String currentUserId) {
        this.service = service;
        this.userId = currentUserId;
        userEntities = service.getInvitationsToUser(currentUserId); //TODO I think it's not good to get all data at once
    }

    @Override
    public void refreshAll() {

        userEntities = service.getInvitationsToUser(userId);
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

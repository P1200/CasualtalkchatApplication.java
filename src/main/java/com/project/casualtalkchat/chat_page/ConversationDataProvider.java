package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

public class ConversationDataProvider extends AbstractBackEndDataProvider<ConversationEntity, ConversationFilter> {

    private final ConversationService service;
    private final String userId;
    private List<ConversationEntity> conversations;

    public ConversationDataProvider(ConversationService service, String currentUserId) {
        this.service = service;
        this.userId = currentUserId;
        conversations = service.getUserConversations(currentUserId); //TODO I think it's not good to get all data at once
    }

    @Override
    public void refreshAll() {
        conversations = service.getUserConversations(userId);
        super.refreshAll();
    }

    @Override
    protected Stream<ConversationEntity> fetchFromBackEnd(Query<ConversationEntity, ConversationFilter> query) {
        Stream<ConversationEntity> stream = conversations.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(person -> query.getFilter().get().test(person));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    protected int sizeInBackEnd(Query<ConversationEntity, ConversationFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}

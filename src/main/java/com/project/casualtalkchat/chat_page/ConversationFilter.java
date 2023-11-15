package com.project.casualtalkchat.chat_page;

public class ConversationFilter implements Filter<ConversationEntity>{
    private String searchTerm;

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean test(ConversationEntity conversation) {
        return matches(conversation.getName(), searchTerm);
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}

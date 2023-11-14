package com.project.casualtalkchat.chat_page;

public class UserFilter implements Filter<UserEntity>{
    private String searchTerm;

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean test(UserEntity person) {
        boolean matchesFullName = matches(person.getUsername(), searchTerm);
        boolean matchesProfession = matches(person.getId(), searchTerm);
        return matchesFullName || matchesProfession;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}

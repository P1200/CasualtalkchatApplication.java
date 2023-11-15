package com.project.casualtalkchat.chat_page;

public interface Filter<T> {

    void setSearchTerm(String searchTerm);

    boolean test(T person);
}

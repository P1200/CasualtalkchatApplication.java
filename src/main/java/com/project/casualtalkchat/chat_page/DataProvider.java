package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Component;

import java.util.List;

public interface DataProvider {

    List<Component> fetchPage(final int pageNumber);
}

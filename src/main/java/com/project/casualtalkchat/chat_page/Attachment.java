package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.server.InputStreamFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Attachment {
    private InputStreamFactory image;
    private String mime;
    private String fileName;
}

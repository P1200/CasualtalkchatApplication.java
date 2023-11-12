package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.server.StreamResource;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageDTO {

    private String content;

    private List<Attachment> attachments;

    private Instant sentTime;

    private String conversationId;

    private String userName;

    private StreamResource userAvatarResource;

    private String senderId;
}

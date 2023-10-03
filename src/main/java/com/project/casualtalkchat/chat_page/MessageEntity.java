package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ChatMessageEntity")
@Table(name = "message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String content;

    @Enumerated
    private MessageType type;

    private Timestamp sentTime;

    @ManyToOne
    private UserEntity sender;
}

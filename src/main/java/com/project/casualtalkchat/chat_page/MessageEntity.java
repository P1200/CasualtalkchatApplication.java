package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<AttachmentEntity> attachments;

    private Timestamp sentTime;

    @ManyToOne
    private ConversationEntity conversation;

    @ManyToOne
    private UserEntity sender;
}

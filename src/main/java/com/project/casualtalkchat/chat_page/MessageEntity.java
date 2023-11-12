package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private ConversationEntity conversation;

    @ManyToOne
    @ToString.Exclude
    private UserEntity sender;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserEntity> membersWhoNotViewed;
}

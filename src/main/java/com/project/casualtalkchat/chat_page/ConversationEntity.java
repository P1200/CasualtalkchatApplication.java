package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatConversationEntity")
@Table(name = "conversation")
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @ManyToMany
    private Set<UserEntity> admins;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserEntity> members;

    @OneToMany
    private Set<MessageEntity> messages;
}

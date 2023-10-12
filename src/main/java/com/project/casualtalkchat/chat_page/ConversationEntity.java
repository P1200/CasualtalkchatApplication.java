package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private Set<UserEntity> admins;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserEntity> members;
}

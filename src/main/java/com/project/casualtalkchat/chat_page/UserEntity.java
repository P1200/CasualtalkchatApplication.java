package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ChatUserEntity")
@Table(name = "user")
class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Length(min = 1, max = 32)
    private String username;

    private String avatarName;

    @Email
    private String email;

    @ManyToMany
    @ToString.Exclude
    private Set<UserEntity> friends;

    @ManyToMany
    @ToString.Exclude
    private Set<UserEntity> invitations;
}

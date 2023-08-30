package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.Set;

@Getter
@Setter
@Entity(name = "ChatUserEntity")
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Length(min = 1, max = 32)
    private String username;

    private String avatarName;

    @Email
    private String email;

    @NotNull
    @Length(max = 60)
    private String password;

    private boolean isAccountConfirmed;

    private boolean isServiceTermsAccepted;

    @ManyToMany
    private Set<UserEntity> friends;

    @ManyToMany
    private Set<UserEntity> invitations;

    private Date creationDate;

    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }
}

package com.project.casualtalkchat.login_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity(name = "login-page-user-entity")
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

    public UserEntity(String id, String username, String avatarName, String email,
                      String password, boolean isAccountConfirmed) {
        this.id = id;
        this.username = username;
        this.avatarName = avatarName;
        this.email = email;
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
        this.isAccountConfirmed = isAccountConfirmed;
    }

    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }
}

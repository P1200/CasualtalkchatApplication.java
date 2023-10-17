package com.project.casualtalkchat.password_change_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
@Entity(name = "password-change-page-user-entity")
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Length(min = 1, max = 32)
    private String username;

    @Email
    private String email;

    @NotNull
    @Length(max = 60)
    private String password;

    public UserEntity(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void setPasswordWithoutEncoding(String password) {
        this.password = password;
    }

    public void skipDataBinding() {
        //Just a trick to avoid a data binding
    }
}

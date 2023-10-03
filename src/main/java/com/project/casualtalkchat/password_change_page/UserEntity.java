package com.project.casualtalkchat.password_change_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;

@Getter
@Setter
@Entity(name = "password-change-page-user-entity")
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

    private Date creationDate;

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
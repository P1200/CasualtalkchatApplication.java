package com.project.casualtalkchat.register_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity(name = "register-page-user-entity")
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

    UserEntity(String id, String username, String avatarName, String email, String password,
                      boolean isAccountConfirmed, boolean isServiceTermsAccepted, Date creationDate) {
        this.id = id;
        this.username = username;
        this.avatarName = avatarName;
        this.email = email;
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
        this.isAccountConfirmed = isAccountConfirmed;
        this.isServiceTermsAccepted = isServiceTermsAccepted;
        this.creationDate = creationDate;
    }

    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void skipDataBinding() {
        //Just a trick to skip repeatedPassword text field binding
    }
}

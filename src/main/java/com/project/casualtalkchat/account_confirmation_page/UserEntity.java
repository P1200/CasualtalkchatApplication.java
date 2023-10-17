package com.project.casualtalkchat.account_confirmation_page;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "account-confirmation-page-user-entity")
@Table(name = "user")
class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private boolean isAccountConfirmed;
}

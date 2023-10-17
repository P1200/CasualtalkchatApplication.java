package com.project.casualtalkchat.account_confirmation_page;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "account-confirmation-page-verification-token")
@Table(name = "verification_token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class VerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;
}

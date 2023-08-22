package com.project.casualtalkchat.account_recovery_page;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Entity(name = "account-recovery-page-verification-token")
@Table(name = "recovery_token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class AccountRecoveryTokenEntity {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    private Date expiryDate = calculateExpiryDate(EXPIRATION); //TODO fix calculating expiry date

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime()
                                    .getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime()
                            .getTime());
    }
}

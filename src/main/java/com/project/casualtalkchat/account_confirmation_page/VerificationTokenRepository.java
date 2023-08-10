package com.project.casualtalkchat.account_confirmation_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("account-confirmation-token-repository")
interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {

    VerificationTokenEntity findByUserIdAndToken(String userId, String token);
}

package com.project.casualtalkchat.password_change_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("password-change-repository")
interface AccountRecoveryTokenRepository extends JpaRepository<AccountRecoveryTokenEntity, Long> {

    AccountRecoveryTokenEntity findByUserIdAndToken(String userId, String token);
}

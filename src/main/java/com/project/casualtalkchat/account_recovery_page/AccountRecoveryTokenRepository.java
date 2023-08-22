package com.project.casualtalkchat.account_recovery_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("account-recovery-token-repository")
interface AccountRecoveryTokenRepository extends JpaRepository<AccountRecoveryTokenEntity, Long> {

    AccountRecoveryTokenEntity findByUserId(String userId);
}

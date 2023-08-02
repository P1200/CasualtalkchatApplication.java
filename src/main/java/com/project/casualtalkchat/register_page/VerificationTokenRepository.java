package com.project.casualtalkchat.register_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("register-page-token-repository")
interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {

}

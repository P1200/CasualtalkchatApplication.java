package com.project.casualtalkchat.account_confirmation_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("account-confirmation-page-user-repository")
public interface UserRepository extends JpaRepository<UserEntity, String> {

}

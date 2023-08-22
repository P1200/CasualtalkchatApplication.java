package com.project.casualtalkchat.password_change_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("password-change-page-user-repository")
public interface UserRepository extends JpaRepository<UserEntity, String> {

}

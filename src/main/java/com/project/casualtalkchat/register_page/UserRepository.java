package com.project.casualtalkchat.register_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("register-page-user-repository")
interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByEmail(String email);
}

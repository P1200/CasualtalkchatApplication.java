package com.project.casualtalkchat.account_recovery_page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserAccountRecoveryService {

    private final UserRepository userRepository;
    private final AccountRecoveryTokenRepository tokenRepository;

    public UserAccountRecoveryService(UserRepository userRepository, AccountRecoveryTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public String createRecoveryTokenForUser(UserEntity user) {

        AccountRecoveryTokenEntity tokenEntity = tokenRepository.findByUserId(user.getId());

        if (tokenEntity == null) {
            String token = UUID.randomUUID()
                                .toString();

            AccountRecoveryTokenEntity verificationToken = AccountRecoveryTokenEntity.builder()
                                                                                    .token(token)
                                                                                    .user(user)
                                                                                    .build();

            tokenRepository.save(verificationToken);
            return token;
        } else {
            return tokenEntity.getToken();
        }
    }

    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email);
    }
}

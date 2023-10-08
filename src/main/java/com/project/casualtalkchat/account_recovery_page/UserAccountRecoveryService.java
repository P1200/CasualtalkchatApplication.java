package com.project.casualtalkchat.account_recovery_page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;

@Service
@Transactional
public class UserAccountRecoveryService {

    private final Supplier<UUID> randomUUID = UUID::randomUUID;
    private final UserRepository userRepository;
    private final AccountRecoveryTokenRepository tokenRepository;

    public UserAccountRecoveryService(UserRepository userRepository, AccountRecoveryTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public String getRecoveryTokenForUser(UserEntity user) {

        AccountRecoveryTokenEntity oldRecoveryTokenEntity = tokenRepository.findByUserId(user.getId());

        if (oldRecoveryTokenEntity == null) {
            String token = randomUUID.get()
                                    .toString();

            AccountRecoveryTokenEntity newRecoveryTokenEntity = AccountRecoveryTokenEntity.builder()
                                                                                    .token(token)
                                                                                    .user(user)
                                                                                    .build();

            tokenRepository.save(newRecoveryTokenEntity);
            return token;
        } else {
            return oldRecoveryTokenEntity.getToken();
        }
    }

    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email);
    }
}

package com.project.casualtalkchat.password_change_page;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangePasswordService {

    private final AccountRecoveryTokenRepository tokenRepository;
    private final UserRepository userRepository;

    AccountRecoveryTokenEntity getTokenEntity(String userIdAndToken) {
        String[] parameters = userIdAndToken.split("/");

        if (parameters.length != 2) {
            return null;
        }

        String userId = parameters[0];
        String token = parameters[1];
        return tokenRepository.findByUserIdAndToken(userId, token);
    }

    void acceptPasswordChange(UserEntity user, AccountRecoveryTokenEntity tokenEntity) {
        userRepository.save(user);
        tokenRepository.delete(tokenEntity);
    }
}

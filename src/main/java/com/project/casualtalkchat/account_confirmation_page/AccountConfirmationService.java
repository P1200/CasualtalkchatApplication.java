package com.project.casualtalkchat.account_confirmation_page;

import org.springframework.stereotype.Service;

@Service
public class AccountConfirmationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public AccountConfirmationService(VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public boolean confirmUser(final String userIdAndToken) {
        VerificationTokenEntity tokenEntity = getVerificationTokenEntity(userIdAndToken);

        if (tokenEntity != null) {
            UserEntity user = tokenEntity.getUser();
            user.setAccountConfirmed(true);

            userRepository.save(user);
            tokenRepository.delete(tokenEntity);
            return true;
        } else {
            return false;
        }
    }

    private VerificationTokenEntity getVerificationTokenEntity(String userIdAndToken) {
        String[] parameters = userIdAndToken.split("/");

        if (parameters.length != 2) {
            return null;
        }

        String userId = parameters[0];
        String token = parameters[1];
        return tokenRepository.findByUserIdAndToken(userId, token);
    }
}

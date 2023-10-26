package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.common.UserImagesRepository;
import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.project.casualtalkchat.common.UserEntityUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class UserRegistrationService {

    public static final String PATH_TO_AVATARS_DIRECTORY = UserEntityUtils.USER_AVATARS_PATH;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final UserImagesRepository resourcesRepository;

    public UserEntity registerNewUserAccount(UserEntity user) throws UserAlreadyExistException {
        throwExceptionIfUserWithThatEmailExists(user.getEmail());

        return userRepository.save(user);
    }

    public UserEntity registerNewUserAccount(UserEntity user, AvatarImage avatar) throws UserAlreadyExistException, FileCouldNotBeSavedException {

        throwExceptionIfUserWithThatEmailExists(user.getEmail());

        String avatarNameMd5 = DigestUtils.md5Hex(avatar.getName()) + "." + avatar.getMime()
                .split("/")[1]; //TODO what if it generates same codes for different images?
        resourcesRepository.saveFile(PATH_TO_AVATARS_DIRECTORY + avatarNameMd5, avatar.getImage());
        user.setAvatarName(avatarNameMd5);

        return userRepository.save(user);
    }

    public void saveVerificationTokenForUser (UserEntity user, String token) {
        VerificationTokenEntity verificationToken = VerificationTokenEntity.builder()
                .token(token)
                .user(user)
                .build();

        tokenRepository.save(verificationToken);
    }

    private void throwExceptionIfUserWithThatEmailExists(String email) throws UserAlreadyExistException {
        if (emailExists(email)) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                    + email + ".");
        }
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}

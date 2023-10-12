package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.common.UserEntityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Transactional
public class UserRegistrationService {

    public static final String PATH_TO_AVATARS_DIRECTORY = "src/main/resources" + UserEntityUtils.USER_AVATARS_PATH;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    public UserRegistrationService(@Autowired UserRepository userRepository,
                                   @Autowired VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public UserEntity registerNewUserAccount(UserEntity user) throws UserAlreadyExistException {
        throwExceptionIfUserWithThatEmailExists(user.getEmail());

        return userRepository.save(user);
    }

    public UserEntity registerNewUserAccount(UserEntity user, AvatarImage avatar) throws UserAlreadyExistException, FileNotFoundException {

        throwExceptionIfUserWithThatEmailExists(user.getEmail());

        String avatarNameMd5 = DigestUtils.md5Hex(avatar.getName()) + "." + avatar.getMime()
                .split("/")[1]; //TODO what if it generates same codes for different images?
        saveAvatarOrThrowException(avatar, avatarNameMd5);
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

    private void saveAvatarOrThrowException(AvatarImage avatar, String avatarNameMd5) throws FileNotFoundException {
        try (FileOutputStream outputStream =
                     FileUtils.openOutputStream(new File(PATH_TO_AVATARS_DIRECTORY + avatarNameMd5))) {

            outputStream.write(avatar.getImage());
        } catch (IOException e) {
            throw new FileNotFoundException(); //TODO create custom exception instead
        }
    }

    private void throwExceptionIfUserWithThatEmailExists(String email) throws UserAlreadyExistException {
        if (emailExists(email)) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                    + email);
        }
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}

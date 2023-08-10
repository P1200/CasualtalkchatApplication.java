package com.project.casualtalkchat.register_page;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Service
@Transactional
public class UserRegistrationService implements Serializable{

    public static final String PATH_TO_AVATARS_DIRECTORY = "src/main/resources/images/users/avatars/";
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

        String avatarNameMd5 = DigestUtils.md5Hex(avatar.getName()); //TODO what if it generates same codes for different images?
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
                     new FileOutputStream(PATH_TO_AVATARS_DIRECTORY + avatarNameMd5)) {

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

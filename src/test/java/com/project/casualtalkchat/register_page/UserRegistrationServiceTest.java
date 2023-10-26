package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.common.UserImagesRepository;
import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String PASSWORD = "Lolek123*";
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    public static final String AVATAR_NAME = "my avatar5";
    public static final String AVATAR_MIME = "image/png";

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private UserImagesRepository resourcesRepository;

    @Test
    void shouldRegisterNewUserAccountWithoutAvatar() throws UserAlreadyExistException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        //When
        service.registerNewUserAccount(prepareUserEntity());

        //Then
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void shouldNotRegisterNewUserAccountWhenAlreadyExistsWithoutAvatar() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        when(userRepository.findByEmail(anyString())).thenReturn(prepareUserEntity());

        //When
        Exception userAlreadyExistsException =
                assertThrows(UserAlreadyExistException.class, () -> service.registerNewUserAccount(prepareUserEntity()));

        //Then
        verify(userRepository, times(0)).save(any(UserEntity.class));
        assertEquals("There is an account with that email address: " + EMAIL + ".",
                userAlreadyExistsException.getMessage());
    }

    @Test
    void shouldRegisterNewUserAccountWithAvatar() throws UserAlreadyExistException, FileCouldNotBeSavedException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        //When
        service.registerNewUserAccount(prepareUserEntity(), avatarImage);

        //Then
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(resourcesRepository, times(1)).saveFile(anyString(), any());
    }

    @Test
    void shouldNotRegisterNewUserAccountWithAvatarWhenAvatarNotSavedAndThrowException()
            throws FileCouldNotBeSavedException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        doThrow(new FileCouldNotBeSavedException(AVATAR_NAME)).when(resourcesRepository)
                                                    .saveFile(anyString(), any());

        //When
        Exception exception = assertThrows(FileCouldNotBeSavedException.class,
                () -> service.registerNewUserAccount(prepareUserEntity(), avatarImage));

        //Then
        verify(userRepository, times(0)).save(any(UserEntity.class));
        assertEquals("File couldn't be saved in " + AVATAR_NAME + ".", exception.getMessage());
    }

    @Test
    void shouldNotRegisterNewUserAccountWhenAlreadyExistsWithAvatar() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);
        when(userRepository.findByEmail(anyString())).thenReturn(prepareUserEntity());

        //When
        Exception userAlreadyExistsException =
                assertThrows(UserAlreadyExistException.class,
                        () -> service.registerNewUserAccount(prepareUserEntity(), avatarImage));

        //Then
        verify(userRepository, times(0)).save(any(UserEntity.class));
        assertEquals("There is an account with that email address: " + EMAIL + ".",
                userAlreadyExistsException.getMessage());
    }

    @Test
    void saveVerificationTokenForUser() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);

        //When
        service.saveVerificationTokenForUser(prepareUserEntity(), TOKEN);

        //Then
        verify(tokenRepository, times(1)).save(any(VerificationTokenEntity.class));
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
}
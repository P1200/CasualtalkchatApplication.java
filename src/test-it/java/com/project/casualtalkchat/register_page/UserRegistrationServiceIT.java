package com.project.casualtalkchat.register_page;

import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import com.project.casualtalkchat.common.UserImagesRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserRegistrationServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    private static final String EXISTING_USER_ID = "a2c43ade-742e-40d6-b0b3-a933c29a9d7d";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";
    public static final String EXISTING_EMAIL = "paul@gmail.com";
    public static final String PASSWORD = "Lolek123*";
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    public static final String AVATAR_NAME = "my avatar5";
    public static final String AVATAR_MIME = "image/png";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Mock
    private UserImagesRepository resourcesRepository;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldRegisterNewUserAccountWithoutAvatar() throws UserAlreadyExistException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        UserEntity newUser = prepareUserEntity();

        //When
        service.registerNewUserAccount(newUser);

        //Then
        UserEntity userEntity = userRepository.findByEmail(EMAIL);
        assertEquals(newUser.getEmail(), userEntity.getEmail());
        assertEquals(newUser.getAvatarName(), userEntity.getAvatarName());
        assertEquals(newUser.getUsername(), userEntity.getUsername());
        assertEquals(newUser.getPassword(), userEntity.getPassword());
        assertEquals(newUser.getCreationDate(), userEntity.getCreationDate());
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldNotRegisterNewUserAccountWhenEmailIsAlreadyInUseWithoutAvatar() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);

        //When
        Exception userAlreadyExistsException =
                assertThrows(UserAlreadyExistException.class, () -> service.registerNewUserAccount(prepareExistingUserEntity()));

        //Then
        assertEquals("There is an account with that email address: " + EXISTING_EMAIL + ".",
                userAlreadyExistsException.getMessage());
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldRegisterNewUserAccountWithAvatar() throws UserAlreadyExistException, FileCouldNotBeSavedException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);
        UserEntity newUser = prepareUserEntity();

        //When
        service.registerNewUserAccount(newUser, avatarImage);

        //Then
        verify(resourcesRepository, times(1)).saveFile(anyString(), any());
        UserEntity userEntity = userRepository.findByEmail(EMAIL);
        assertEquals(newUser.getEmail(), userEntity.getEmail());
        assertEquals(newUser.getAvatarName(), userEntity.getAvatarName());
        assertEquals(newUser.getUsername(), userEntity.getUsername());
        assertEquals(newUser.getPassword(), userEntity.getPassword());
        assertEquals(newUser.getCreationDate(), userEntity.getCreationDate());
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldNotRegisterNewUserAccountWithAvatarWhenAvatarNotSavedAndThrowException()
            throws FileCouldNotBeSavedException {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);
        doThrow(new FileCouldNotBeSavedException(AVATAR_NAME)).when(resourcesRepository)
                                                    .saveFile(anyString(), any());

        //When
        Exception exception = assertThrows(FileCouldNotBeSavedException.class,
                () -> service.registerNewUserAccount(prepareUserEntity(), avatarImage));

        //Then
        assertEquals("File couldn't be saved in " + AVATAR_NAME + ".", exception.getMessage());
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldNotRegisterNewUserAccountWhenAlreadyExistsWithAvatar() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        AvatarImage avatarImage = new AvatarImage();
        avatarImage.setName(AVATAR_NAME);
        avatarImage.setMime(AVATAR_MIME);

        //When
        Exception userAlreadyExistsException =
                assertThrows(UserAlreadyExistException.class,
                        () -> service.registerNewUserAccount(prepareExistingUserEntity(), avatarImage));

        //Then
        assertEquals("There is an account with that email address: " + EXISTING_EMAIL + ".",
                userAlreadyExistsException.getMessage());
    }

    @Test
    @Sql("/db-scripts/user-registration-service-it.sql")
    @Transactional
    void shouldSaveVerificationTokenForUser() {
        //Given
        UserRegistrationService service =
                new UserRegistrationService(userRepository, tokenRepository, resourcesRepository);
        int tokensCountBefore = tokenRepository.findAll().size();

        //When
        service.saveVerificationTokenForUser(prepareExistingUserEntity(), TOKEN);

        //Then
        assertEquals(++ tokensCountBefore, tokenRepository.findAll().size());
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private UserEntity prepareExistingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER_ID)
                .username(USERNAME)
                .email(EXISTING_EMAIL)
                .password(PASSWORD)
                .build();
    }
}
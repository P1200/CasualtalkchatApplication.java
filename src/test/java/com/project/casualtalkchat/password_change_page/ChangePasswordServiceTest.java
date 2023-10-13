package com.project.casualtalkchat.password_change_page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {

    private static final String PROPER_USER_ID_AND_TOKEN_STRING =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4/77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String NOT_EXISTING_USER_ID_AND_TOKEN_STRING = "abc1-ger348r/77c7eef7-16e4-938e4fb219ad";
    private static final String USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4\\77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final Long TOKEN_ID = 1L;
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String PASSWORD = "Lolek123*";

    @Mock
    private AccountRecoveryTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldGetTokenEntity() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(tokenRepository.findByUserIdAndToken(anyString(), anyString())).thenReturn(prepareRecoveryTokenEntity());

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(PROPER_USER_ID_AND_TOKEN_STRING);

        //Then
        AccountRecoveryTokenEntity expected = prepareRecoveryTokenEntity();
        assertEquals(expected.getToken(), tokenEntity.getToken());
        assertEquals(expected.getId(), tokenEntity.getId());
        UserEntity user = tokenEntity.getUser();
        assertTrue(passwordEncoder.matches(PASSWORD, user.getPassword()),"Password should be correctly encrypted.");
        assertEquals(expected.getUser()
                             .getId(), user.getId());
    }

    @Test
    void shouldNotGetTokenEntityWhenUserIdAndTokenDoNotMatchAnyDatabaseRow() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);
        when(tokenRepository.findByUserIdAndToken(anyString(), anyString())).thenReturn(null);

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(NOT_EXISTING_USER_ID_AND_TOKEN_STRING);

        //Then
        assertNull(tokenEntity, "Should return null when database row not found.");
    }

    @Test
    void shouldNotGetTokenEntityWhenUserIdAndTokenInWrongFormat() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);

        //When
        AccountRecoveryTokenEntity tokenEntity = service.getTokenEntity(USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT);

        //Then
        verify(tokenRepository, times(0)).findByUserIdAndToken(anyString(), anyString());
        assertNull(tokenEntity, "Should return null when method argument in wrong format.");
    }

    @Test
    void shouldSavePasswordAndDeleteToken() {
        //Given
        ChangePasswordService service = new ChangePasswordService(tokenRepository, userRepository);

        //When
        service.acceptPasswordChange(prepareUserEntity(), prepareRecoveryTokenEntity());

        //Then
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(tokenRepository, times(1)).delete(any(AccountRecoveryTokenEntity.class));
    }

    private AccountRecoveryTokenEntity prepareRecoveryTokenEntity() {
        return AccountRecoveryTokenEntity.builder()
                .id(TOKEN_ID)
                .token(TOKEN)
                .user(prepareUserEntity())
                .build();
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .password(PASSWORD)
                .build();
    }
}
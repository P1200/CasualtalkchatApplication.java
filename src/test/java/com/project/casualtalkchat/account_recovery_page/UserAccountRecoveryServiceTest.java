package com.project.casualtalkchat.account_recovery_page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.function.Supplier;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountRecoveryServiceTest {

    private static final Long TOKEN_ID = 1L;
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String EMAIL = "cristinefreud89@gmail.com";

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRecoveryTokenRepository tokenRepository;
    @Mock
    private Supplier uuidSupplier;

    @Test
    void shouldGetOldRecoveryTokenWhenFound() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);
        when(tokenRepository.findByUserId(anyString())).thenReturn(prepareRecoveryTokenEntity());

        //When
        String recoveryToken = service.getRecoveryTokenForUser(prepareUserEntity());

        //Then
        assertEquals(recoveryToken, TOKEN);
        verify(tokenRepository, times(0)).save(any(AccountRecoveryTokenEntity.class));
    }

    @Test
    void shouldGetNewRecoveryTokenAndSaveItToDatabaseWhenOldTokenNotFound() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);
        ReflectionTestUtils.setField(service, "randomUUID", uuidSupplier);
        when(tokenRepository.findByUserId(anyString())).thenReturn(null);
        when(uuidSupplier.get()).thenReturn(UUID.fromString(TOKEN));

        //When
        String recoveryToken = service.getRecoveryTokenForUser(prepareUserEntity());

        //Then
        assertEquals(recoveryToken, TOKEN);
        verify(tokenRepository, times(1)).save(any(AccountRecoveryTokenEntity.class));
    }

    @Test
    void shouldGetUser() {
        //Given
        UserAccountRecoveryService service = new UserAccountRecoveryService(userRepository, tokenRepository);
        UserEntity preparedUserEntity = prepareUserEntity();
        when(userRepository.findByEmail(anyString())).thenReturn(preparedUserEntity);

        //When
        UserEntity user = service.getUser(EMAIL);

        //Then
        assertEquals(preparedUserEntity, user);
    }

    private AccountRecoveryTokenEntity prepareRecoveryTokenEntity() {
        return AccountRecoveryTokenEntity.builder()
                                            .id(TOKEN_ID)
                                            .token(TOKEN)
                                            .build();
    }

    private UserEntity prepareUserEntity() {
        return UserEntity.builder()
                            .id(USER_ID)
                            .username(USERNAME)
                            .email(EMAIL)
                            .build();
    }
}
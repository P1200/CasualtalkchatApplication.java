package com.project.casualtalkchat.account_confirmation_page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountConfirmationServiceTest {

    private static final String PROPER_USER_ID_AND_TOKEN_STRING =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4/77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String NOT_EXISTING_USER_ID_AND_TOKEN_STRING = "abc1-ger348r/77c7eef7-16e4-938e4fb219ad";
    private static final String USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT =
            "c66e1a75-a34d-4c28-9429-191dc59b86f4\\77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final Long TOKEN_ID = 1L;
    private static final String TOKEN = "77c7eef7-16e4-4926-9c07-938e4fb219ad";
    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";

    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void userShouldBeConfirmed() {
        //Given
        AccountConfirmationService confirmationService =
                new AccountConfirmationService(tokenRepository, userRepository);
        when(tokenRepository.findByUserIdAndToken(anyString(), anyString())).thenReturn(prepareVerificationTokenEntity());

        //When
        boolean isUserConfirmed = confirmationService.confirmUser(PROPER_USER_ID_AND_TOKEN_STRING);

        //Then
        verify(tokenRepository, times(1)).findByUserIdAndToken(anyString(), anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(tokenRepository, times(1)).delete(any(VerificationTokenEntity.class));

        assertTrue(isUserConfirmed, "User should be confirmed");
    }

    @Test
    void userShouldNotBeConfirmedWhenUserIdAndTokenDoNotMatchAnyDatabaseRow() {
        //Given
        AccountConfirmationService confirmationService =
                new AccountConfirmationService(tokenRepository, userRepository);
        when(tokenRepository.findByUserIdAndToken(anyString(), anyString())).thenReturn(null);

        //When
        boolean isUserConfirmed = confirmationService.confirmUser(NOT_EXISTING_USER_ID_AND_TOKEN_STRING);

        //Then
        verify(tokenRepository, times(1)).findByUserIdAndToken(anyString(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
        verify(tokenRepository, times(0)).delete(any(VerificationTokenEntity.class));

        assertFalse(isUserConfirmed, "User shouldn't be confirmed");
    }

    @Test
    void userShouldNotBeConfirmedWhenUserIdAndTokenInWrongFormat() {
        //Given
        AccountConfirmationService confirmationService =
                new AccountConfirmationService(tokenRepository, userRepository);

        //When
        boolean isUserConfirmed = confirmationService.confirmUser(USER_ID_AND_TOKEN_STRING_IN_WRONG_FORMAT);

        //Then
        verify(tokenRepository, times(0)).findByUserIdAndToken(anyString(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
        verify(tokenRepository, times(0)).delete(any(VerificationTokenEntity.class));

        assertFalse(isUserConfirmed, "User shouldn't be confirmed");
    }

    private VerificationTokenEntity prepareVerificationTokenEntity() {
        return VerificationTokenEntity.builder()
                .id(TOKEN_ID)
                .token(TOKEN)
                .user(UserEntity.builder()
                        .id(USER_ID)
                        .isAccountConfirmed(false)
                        .build())
                .build();
    }
}
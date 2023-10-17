package com.project.casualtalkchat.login_page;

import com.project.casualtalkchat.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    private static final String USER_ID = "c66e1a75-a34d-4c28-9429-191dc59b86f4";
    public static final String USERNAME = "madame89";
    public static final String USER_EMAIL = "cristinefreud89@gmail.com";
    public static final String PASSWORD = "Lolek123*";
    public static final String AVATAR_NAME = "aea2bac3c4933907e6be863efccd01b9.jpeg";

    @Mock
    private UserRepository repository;

    @Test
    void shouldGetUserDetailsByUsernameWhenUserIsEnabled() {
        //Given
        UserLoginService service = new UserLoginService(repository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(repository.findByEmail(anyString()))
                .thenReturn(Optional.ofNullable(prepareUserEntity(
                        true)));

        //When
        CustomUserDetails userDetails = (CustomUserDetails) service.loadUserByUsername(USER_EMAIL);

        //Then
        assertEquals(USERNAME, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(PASSWORD, userDetails.getPassword()), "Password should be correctly encrypted.");
        assertEquals(USER_EMAIL, userDetails.getEmail());
        assertEquals(USER_ID, userDetails.getId());
        assertEquals(AVATAR_NAME, userDetails.getAvatar());
        assertTrue(userDetails.isEnabled(), "User should be confirmed and enabled.");
        assertFalse(userDetails.isAccountNonExpired(), "Account shouldn't be expired.");
        assertFalse(userDetails.isAccountNonLocked(), "Account shouldn't be locked.");
        assertFalse(userDetails.isCredentialsNonExpired(), "Credentials shouldn't be expired.");
    }

    @Test
    void shouldGetUserDetailsByUsernameWhenUserIsNotEnabled() {
        //Given
        UserLoginService service = new UserLoginService(repository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(repository.findByEmail(anyString()))
                .thenReturn(Optional.ofNullable(prepareUserEntity(
                        false)));

        //When
        CustomUserDetails userDetails = (CustomUserDetails) service.loadUserByUsername(USER_EMAIL);

        //Then
        assertEquals(USERNAME, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(PASSWORD, userDetails.getPassword()), "Password should be correctly encrypted.");
        assertEquals(USER_EMAIL, userDetails.getEmail());
        assertEquals(USER_ID, userDetails.getId());
        assertEquals(AVATAR_NAME, userDetails.getAvatar());
        assertFalse(userDetails.isEnabled(), "User should be confirmed and enabled.");
        assertFalse(userDetails.isAccountNonExpired(), "Account shouldn't be expired.");
        assertFalse(userDetails.isAccountNonLocked(), "Account shouldn't be locked.");
        assertFalse(userDetails.isCredentialsNonExpired(), "Credentials shouldn't be expired.");
    }

    private UserEntity prepareUserEntity(boolean isAccountConfirmed) {
        return UserEntity.builder()
                .id(UserLoginServiceTest.USER_ID)
                .email(UserLoginServiceTest.USER_EMAIL)
                .username(UserLoginServiceTest.USERNAME)
                .password(UserLoginServiceTest.PASSWORD)
                .avatarName(UserLoginServiceTest.AVATAR_NAME)
                .isAccountConfirmed(isAccountConfirmed)
                .build();
    }
}
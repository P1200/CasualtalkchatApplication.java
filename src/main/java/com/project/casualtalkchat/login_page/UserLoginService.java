package com.project.casualtalkchat.login_page;

import com.project.casualtalkchat.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UserLoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.debug("User with email: " + userEmail + " not found");
                    return new UsernameNotFoundException("User not found with email: " + userEmail);});

        return CustomUserDetails.build(user);
    }
}

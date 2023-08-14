package com.project.casualtalkchat.security;

import com.project.casualtalkchat.login_page.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String username;
    private final String email;
    private final String password;
    private final String avatar;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isEnabled;

    public CustomUserDetails(String id, String username, String email, String password, String avatar,
                           Collection<? extends GrantedAuthority> authorities, boolean isAccountConfirmed) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.authorities = authorities;
        this.isEnabled = isAccountConfirmed;
    }

    public static CustomUserDetails build(UserEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getAvatarName(),
                authorities,
                user.isAccountConfirmed()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}

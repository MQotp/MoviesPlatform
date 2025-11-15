package com.moviesPlatform.backend.security;

import com.moviesPlatform.backend.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security expects roles in the format "ROLE_ADMIN", "ROLE_USER"
        String roleName = "ROLE_" + user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getEnabled();
    }

    public boolean isCredentialsNonExpired() {
        return user.getEnabled();
    }

    public boolean isEnabled() {
        return user.getEnabled();
    }

    public User getUser() {
        return user;
    }
}

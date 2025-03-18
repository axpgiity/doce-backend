package com.doce_ai.Security.Services;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.doce_ai.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of Spring Security's UserDetails interface for representing user details.
 */
@SuppressWarnings("FieldMayBeFinal")
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;

    @JsonIgnore
    private String password;

    /**
     * Constructor to initialize UserDetailsImpl.
     *
     * @param username The username of the user.
     * @param email    The email of the user.
     * @param password The password of the user.
     */
    public UserDetailsImpl(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Builds a UserDetailsImpl instance from a User object.
     *
     * @param user The User object.
     * @return A UserDetailsImpl instance.
     */
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUsername(),
                user.getEmail(),
                user.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an empty list if you don't have roles/authorities
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(username, user.username);
    }
}
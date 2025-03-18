package com.doce_ai.Security.Services;


import com.doce_ai.model.User;
import com.doce_ai.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
1) This class is responsible for loading user-specific data during the authentication process.
2) It retrieves user details from the database and returns a UserDetails object,
which Spring Security uses to perform authentication and authorization.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Loads user details by username or email.
     *
     * @param usernameOrEmail The username or email of the user.
     * @return UserDetails containing user information.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Attempt to find the user by username or email
        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username or email: " + usernameOrEmail));

        // Return UserDetails implementation for the found user
        return UserDetailsImpl.build(user);
    }
}
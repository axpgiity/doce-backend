package com.doce_ai.controller;

import com.doce_ai.model.User;
import com.doce_ai.payload.request.LoginRequest;
import com.doce_ai.payload.request.SignUpRequest;
import com.doce_ai.payload.response.JWTResponse;
import com.doce_ai.payload.response.MessageResponse;

import com.doce_ai.Repository.UserRepository;
import com.doce_ai.Security.jwt.JwtUtils;
import com.doce_ai.Security.Services.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//@CrossOrigin(origins = "*", maxAge = 3600) // Allow cross-origin requests for all origins
@RestController // Indicate that this class is a REST controller
@RequestMapping("/api/auth") // Base URL for authentication-related endpoints
class AuthController {
    @Autowired
    AuthenticationManager authenticationManager; // Handles user authentication

    @Autowired
    UserRepository userRepository; // Repository for user-related database operations

    @Autowired
    PasswordEncoder encoder; // Encoder for password hashing

    @Autowired
    JwtUtils jwtUtils; // Utility for generating JWT tokens

    /**
     * Authenticate user and return a JWT token if successful.
     * @param loginRequest The login request containing username and password.
     * @return A ResponseEntity containing the JWT response or an error message.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {

            // Validate username/email exists
            //Optional<User> user = userRepository.findByUsernameOrEmail(loginRequest.getUsername());
            //if (!user.isPresent()) {
            //    return ResponseEntity.badRequest()
            //            .body(new MessageResponse("Error: Username/Email not found!"));
            //}

            // Check if the email in the request matches the user's email (if provided)
            //if (loginRequest.getbyEmail(userRepository.findByEmail()) != null &&
            //        !loginRequest.getbyEmail().equals(user.get().getEmail())) {
            //    return ResponseEntity.badRequest()
            //            .body(new MessageResponse("Error: Email does not match!"));
            //}

            // Authenticate the user with the provided username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token based on the authentication
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Get user details from the authentication object
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Return a response containing the JWT and user details
            return ResponseEntity.ok(new JWTResponse(jwt,
                    userDetails.getUsername(),
                    userDetails.getEmail()));

        } catch (AuthenticationException e) {

            Optional<User> user = userRepository.findByUsernameOrEmail(loginRequest.getUsername());

            if (user.isPresent()) {
                // Username/email exists but password is wrong
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Invalid password!"));
            } else {
                // Username/email does not exist
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username or Email not found!"));
            }

        }
    }

    /**
     * Register a new user account.
     *
     * @param signUpRequest The signup request containing user details.
     * @return A ResponseEntity indicating success or error message.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        // Check if the username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if the email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create a new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),// Encode the password
                signUpRequest.getEmail());

        userRepository.save(user);

        // Return a success message upon successful registration
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}

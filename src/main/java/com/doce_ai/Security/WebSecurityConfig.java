package com.doce_ai.Security;

import com.doce_ai.Security.jwt.AuthEntryPointJwt; // Import for unauthorized access handler
import com.doce_ai.Security.jwt.AuthTokenFilter; // Import for JWT token filter
import com.doce_ai.Security.Services.UserDetailsServiceImpl; // Import for user details service implementation
import org.springframework.beans.factory.annotation.Autowired; // Import for dependency injection
import org.springframework.context.annotation.Bean; // Import for Spring configuration
import org.springframework.context.annotation.Configuration; // Import for configuration class
import org.springframework.security.authentication.AuthenticationManager; // Import for authentication manager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Import for authentication provider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Import for authentication configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Import for method security
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Import for HTTP security configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Import for HTTP security configuration
import org.springframework.security.config.http.SessionCreationPolicy; // Import for session creation policies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import for password encoding
import org.springframework.security.crypto.password.PasswordEncoder; // Import for password encoder interface
import org.springframework.security.web.SecurityFilterChain; // Import for security filter chain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import for username/password authentication filter

/**
 * Security configuration class to set up Spring Security.
 */
@Configuration // Marks the class as a source of bean definitions
@EnableWebSecurity // Enables web-level security annotations
@EnableMethodSecurity
public class WebSecurityConfig {


    @Autowired
    UserDetailsServiceImpl userDetailsService; // Injects the user details service for authentication

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Injects the entry point for unauthorized requests

    /**
     * Creates a bean for the authentication JWT token filter.
     *
     * @return AuthTokenFilter instance
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(); // Returns a new instance of AuthTokenFilter
    }

    /**
     * Creates a bean for the DAO authentication provider.
     *
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // Create a new authentication provider

        authProvider.setUserDetailsService(userDetailsService); // Set the user details service
        authProvider.setPasswordEncoder(passwordEncoder()); // Set the password encoder

        return authProvider; // Return the configured authentication provider
    }

    /**
     * Creates a bean for the authentication manager.
     *
     * @param authConfig Authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if there is an error getting the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // Returns the authentication manager from the configuration
    }

    /**
     * Creates a bean for the password encoder.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Returns a new instance of BCryptPasswordEncoder
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain instance
     * @throws Exception if there is an error configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure CSRF protection, exception handling, session management, and authorization
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))
                // Set unauthorized handler
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set session policy to stateless
                .authorizeHttpRequests(auth -> auth
                        // Configure authorization for HTTP requests
                        .requestMatchers("/api/auth/**").permitAll()
                        // Allow public access to auth endpoints
                        .requestMatchers("/api/test/**").permitAll()
                        // Allow public access to test endpoints
                        .anyRequest().authenticated());
        // Require authentication for any other request

        http.authenticationProvider(authenticationProvider()); // Set the authentication provider

        // Add the JWT token filter before the username/password authentication filter
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Build and return the security filter chain
    }
}



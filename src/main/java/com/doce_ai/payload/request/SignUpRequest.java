package com.doce_ai.payload.request;

import jakarta.validation.constraints.*;

public class SignUpRequest {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 20,message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 50)
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 120,message = "Password must be between 6 and 120 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

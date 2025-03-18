package com.doce_ai.payload.response;

public class JWTResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;

    public JWTResponse(String accessToken,String username, String email) {
        this.token = accessToken;
        this.username = username;
        this.email = email;
    }
    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

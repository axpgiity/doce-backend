package com.doce_ai.payload.response;

//This class is typically used to provide feedback to the client, such as success or error messages,
// after performing certain operations (e.g., registration, login, etc.).
public class MessageResponse {
    private String message;
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

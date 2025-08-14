package com.doce_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id; // Maps to _id (ObjectId) in MongoDB

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


    private List<String> documents = new ArrayList<>(); // Stores Document IDs

    public User(String username,String password,String email){
        this.username=username;
        this.password=password;
        this.email=email;
    }

    public User() {

    }

    // Add getters and setters

    //id
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    //username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    //password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    //List of document ids
    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents;}

    }

package com.doce_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Document(collection = "documents")
public class Documents {
    @Id
    private String id;

    private String userId; // Foreign Key → User.id
    private String title;
    private List<String> data;
    private Date createdAt;
    private Date updatedAt;

    public Documents() {}

    public Documents(String userId, String title,List<String> data) {
        this.userId = userId;
        this.title = title;
        this.data = data;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getData() { return data; }
    public void setData(List<String> data) { this.data = data; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

}
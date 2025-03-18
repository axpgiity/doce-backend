package com.doce_ai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "documentations")
public class Documentation {
    @Id
    private String id;
    private String repoPath;
    private Object documentationData;
    private Date createdDate;

    // Constructors
    public Documentation() {}

    public Documentation(String repoPath, Object documentationData) {
        this.repoPath = repoPath;
        this.documentationData = documentationData;
        this.createdDate = new Date();
    }

    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRepoPath() { return repoPath; }
    public void setRepoPath(String repoPath) { this.repoPath = repoPath; }

    public Object getDocumentationData() { return documentationData; }
    public void setDocumentationData(Object documentationData) { this.documentationData = documentationData; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}

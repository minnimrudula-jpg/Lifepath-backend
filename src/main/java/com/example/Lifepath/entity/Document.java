package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private String fileUrl;

    protected Document() {
    }

    public Document(String name, String description, String fileUrl) {
        this.name = name;
        this.description = description;
        this.fileUrl = fileUrl;
    }

    public Long getId() {
        return id;
    }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFileUrl() { return fileUrl; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}

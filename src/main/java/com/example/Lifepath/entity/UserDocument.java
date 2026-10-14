package com.example.Lifepath.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "user_documents")
public class UserDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne @JoinColumn(name = "document_id", nullable = false) private Document document;
    private String status;
    private String fileUrl;
    protected UserDocument() {}
    public UserDocument(User user, Document document, String status) { this.user = user; this.document = document; this.status = status; }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Document getDocument() { return document; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

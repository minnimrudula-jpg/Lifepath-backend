package com.example.Lifepath.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String message;
    @Column(name = "`read`", nullable = false)
    private boolean read;
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Notification() {
    }

    public Notification(User user, String message) {
        this.user = user;
        this.message = message;
        this.read = false;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void markRead() { read = true; }
    public void setMessage(String message) { this.message = message; }
}

package com.example.Lifepath.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deadlines")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;
    @Column(nullable = false)
    private String title;
    private LocalDateTime deadline;
    private String description;

    protected Deadline() {
    }

    public Deadline(Opportunity opportunity, String title, LocalDateTime deadline, String description) {
        this.opportunity = opportunity;
        this.title = title;
        this.deadline = deadline;
        this.description = description;
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }
}

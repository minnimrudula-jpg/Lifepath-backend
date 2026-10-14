package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "preparation_tasks")
public class PreparationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;
    @Column(nullable = false)
    private String title;
    private String description;

    protected PreparationTask() {
    }

    public PreparationTask(Opportunity opportunity, String title, String description) {
        this.opportunity = opportunity;
        this.title = title;
        this.description = description;
    }

    public Long getId() { return id; }
    public Opportunity getOpportunity() { return opportunity; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}

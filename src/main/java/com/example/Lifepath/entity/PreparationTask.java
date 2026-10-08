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
}

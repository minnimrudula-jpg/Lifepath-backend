package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "opportunity_categories")
public class OpportunityCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(length = 1000)
    private String description;

    protected OpportunityCategory() {
    }

    public OpportunityCategory(String n, String d) {
        name = n;
        description = d;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String v) {
        name = v;
    }

    public void setDescription(String v) {
        description = v;
    }
}

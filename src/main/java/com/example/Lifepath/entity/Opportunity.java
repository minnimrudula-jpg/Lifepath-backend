package com.example.Lifepath.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private OpportunityCategory category;
    @Column(nullable = false)
    private String title;
    @Column(length = 4000)
    private String description;
    private String provider;
    @Column(name = "official_url")
    private String officialUrl;
    @Column(nullable = false)
    private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Opportunity() {
    }

    public Long getId() {
        return id;
    }

    public OpportunityCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getProvider() {
        return provider;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setCategory(OpportunityCategory v) {
        category = v;
    }

    public void setTitle(String v) {
        title = v;
    }

    public void setDescription(String v) {
        description = v;
    }

    public void setProvider(String v) {
        provider = v;
    }

    public void setOfficialUrl(String v) {
        officialUrl = v;
    }

    public void setStatus(String v) {
        status = v;
    }
}

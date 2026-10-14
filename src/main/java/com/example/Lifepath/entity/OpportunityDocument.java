package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "opportunity_documents")
public class OpportunityDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    protected OpportunityDocument() {
    }

    public OpportunityDocument(Opportunity opportunity, Document document) {
        this.opportunity = opportunity;
        this.document = document;
    }

    public Long getId() { return id; }
    public Opportunity getOpportunity() { return opportunity; }
    public Document getDocument() { return document; }
}

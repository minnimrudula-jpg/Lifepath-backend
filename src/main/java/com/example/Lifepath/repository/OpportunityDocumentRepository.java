package com.example.Lifepath.repository;

import com.example.Lifepath.entity.OpportunityDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityDocumentRepository extends JpaRepository<OpportunityDocument, Long> {
    boolean existsByOpportunityIdAndDocumentId(Long opportunityId, Long documentId);
    boolean existsByOpportunityId(Long opportunityId);
    boolean existsByDocumentId(Long documentId);
}

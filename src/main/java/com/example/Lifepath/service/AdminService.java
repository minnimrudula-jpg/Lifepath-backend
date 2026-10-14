package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.AdminAnalyticsView;
import com.example.Lifepath.dto.ApiDtos.AdminDocumentRequest;
import com.example.Lifepath.dto.ApiDtos.AdminDocumentView;
import com.example.Lifepath.dto.ApiDtos.AdminUserView;
import com.example.Lifepath.dto.ApiDtos.SavedOpportunityCount;
import com.example.Lifepath.entity.Document;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final UserRepository users;
    private final DocumentRepository documents;
    private final OpportunityDocumentRepository opportunityDocuments;
    private final UserDocumentRepository userDocuments;
    private final OpportunityRepository opportunities;
    private final OpportunityCategoryRepository categories;
    private final EligibilityCheckRepository checks;
    private final SavedOpportunityRepository savedOpportunities;

    public AdminService(UserRepository users, DocumentRepository documents,
            OpportunityDocumentRepository opportunityDocuments, UserDocumentRepository userDocuments,
            OpportunityRepository opportunities, OpportunityCategoryRepository categories,
            EligibilityCheckRepository checks, SavedOpportunityRepository savedOpportunities) {
        this.users = users;
        this.documents = documents;
        this.opportunityDocuments = opportunityDocuments;
        this.userDocuments = userDocuments;
        this.opportunities = opportunities;
        this.categories = categories;
        this.checks = checks;
        this.savedOpportunities = savedOpportunities;
    }

    @Transactional(readOnly = true)
    public List<AdminUserView> listUsers() {
        return users.findAll().stream().map(u -> new AdminUserView(u.getId(), u.getName(), u.getEmail(),
                u.getRole().name(), u.getCreatedAt())).toList();
    }

    @Transactional(readOnly = true)
    public List<AdminDocumentView> listDocuments() {
        return documents.findAll().stream().map(this::documentView).toList();
    }

    @Transactional
    public AdminDocumentView createDocument(AdminDocumentRequest request) {
        return documentView(documents.save(new Document(request.name(), request.description(), request.fileUrl())));
    }

    @Transactional
    public AdminDocumentView updateDocument(Long id, AdminDocumentRequest request) {
        Document document = documents.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Document not found"));
        document.setName(request.name());
        document.setDescription(request.description());
        document.setFileUrl(request.fileUrl());
        return documentView(documents.save(document));
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documents.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Document not found"));
        if (opportunityDocuments.existsByDocumentId(id) || userDocuments.existsByDocumentId(id))
            throw new ApiException(HttpStatus.CONFLICT, "Document is referenced and cannot be deleted");
        documents.delete(document);
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsView analytics() {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (Object[] row : checks.countGroupedByFinalStatus())
            grouped.put((String) row[0], ((Number) row[1]).longValue());
        List<SavedOpportunityCount> savedByOpportunity = savedOpportunities.countGroupedByOpportunity().stream()
                .map(row -> new SavedOpportunityCount(((Number) row[0]).longValue(), (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();
        Map<String, Long> registrationsByDate = new LinkedHashMap<>();
        for (Object[] row : users.countRegistrationsByDate())
            registrationsByDate.put(row[0].toString(), ((Number) row[1]).longValue());
        return new AdminAnalyticsView(users.count(), opportunities.count(), categories.count(), savedOpportunities.count(),
                checks.count(), grouped, savedByOpportunity, registrationsByDate);
    }

    private AdminDocumentView documentView(Document document) {
        return new AdminDocumentView(document.getId(), document.getName(), document.getDescription(),
                document.getFileUrl());
    }
}

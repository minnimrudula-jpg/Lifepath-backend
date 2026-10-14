package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.DocumentStatusRequest;
import com.example.Lifepath.dto.ApiDtos.UserDocumentView;
import com.example.Lifepath.entity.Document;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.entity.UserDocument;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.DocumentRepository;
import com.example.Lifepath.repository.OpportunityDocumentRepository;
import com.example.Lifepath.repository.UserDocumentRepository;
import com.example.Lifepath.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {
    private static final Set<String> USER_STATUSES = Set.of("REQUIRED", "SUBMITTED", "PENDING");
    private final DocumentRepository documents;
    private final OpportunityDocumentRepository opportunityDocuments;
    private final UserDocumentRepository userDocuments;
    private final UserRepository users;

    public DocumentService(DocumentRepository documents, OpportunityDocumentRepository opportunityDocuments,
            UserDocumentRepository userDocuments, UserRepository users) {
        this.documents = documents;
        this.opportunityDocuments = opportunityDocuments;
        this.userDocuments = userDocuments;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<UserDocumentView> list(Long userId) {
        Map<Long, List<String>> related = opportunityDocuments.findAll().stream()
                .collect(Collectors.groupingBy(link -> link.getDocument().getId(),
                        Collectors.mapping(link -> link.getOpportunity().getTitle(), Collectors.toList())));
        return documents.findAll().stream().map(document -> view(userId, document, related)).toList();
    }

    @Transactional
    public UserDocumentView updateStatus(Long userId, Long documentId, DocumentStatusRequest request) {
        if (request == null || request.status() == null || !USER_STATUSES.contains(request.status()))
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Document status must be REQUIRED, SUBMITTED, or PENDING; verification is reserved for staff");
        Document document = documents.findById(documentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Document not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        List<UserDocument> records = userDocuments.findAllByUserIdAndDocumentIdOrderByIdAsc(userId, documentId);
        UserDocument userDocument = records.isEmpty()
                ? new UserDocument(user, document, request.status()) : records.get(0);
        userDocument.setStatus(request.status());
        userDocuments.save(userDocument);
        Map<Long, List<String>> related = opportunityDocuments.findAll().stream()
                .collect(Collectors.groupingBy(link -> link.getDocument().getId(),
                        Collectors.mapping(link -> link.getOpportunity().getTitle(), Collectors.toList())));
        return new UserDocumentView(document.getId(), document.getName(), document.getDescription(),
                related.getOrDefault(document.getId(), List.of()), request.status());
    }

    private UserDocumentView view(Long userId, Document document, Map<Long, List<String>> related) {
        List<UserDocument> records = userDocuments.findAllByUserIdAndDocumentIdOrderByIdAsc(userId, document.getId());
        String status = records.isEmpty() || records.get(0).getStatus() == null ? "REQUIRED" : records.get(0).getStatus();
        return new UserDocumentView(document.getId(), document.getName(), document.getDescription(),
                related.getOrDefault(document.getId(), List.of()), status);
    }
}

package com.example.Lifepath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.Lifepath.dto.ApiDtos.AdminDocumentRequest;
import com.example.Lifepath.entity.Document;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AdminServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final DocumentRepository documents = mock(DocumentRepository.class);
    private final OpportunityDocumentRepository opportunityDocuments = mock(OpportunityDocumentRepository.class);
    private final UserDocumentRepository userDocuments = mock(UserDocumentRepository.class);
    private final AdminService service = new AdminService(users, documents, opportunityDocuments, userDocuments,
            mock(OpportunityRepository.class), mock(OpportunityCategoryRepository.class),
            mock(EligibilityCheckRepository.class), mock(SavedOpportunityRepository.class));

    @Test
    void refusesToDeleteDocumentUsedByEitherChecklistRelationship() {
        Document document = new Document("Identity proof", null, null);
        when(documents.findById(7L)).thenReturn(Optional.of(document));
        when(opportunityDocuments.existsByDocumentId(7L)).thenReturn(false);
        when(userDocuments.existsByDocumentId(7L)).thenReturn(true);

        ApiException error = assertThrows(ApiException.class, () -> service.deleteDocument(7L));

        assertEquals(HttpStatus.CONFLICT, error.getStatus());
        verify(documents, never()).delete(any(Document.class));
    }

    @Test
    void mapsAdminDocumentFieldsAndSavesExistingEntity() {
        when(documents.save(any(Document.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.createDocument(new AdminDocumentRequest("Identity proof", "Government ID", null));

        assertEquals("Identity proof", result.name());
        assertEquals("Government ID", result.description());
        assertNull(result.fileUrl());
    }
}

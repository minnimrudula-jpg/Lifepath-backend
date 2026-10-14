package com.example.Lifepath.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.Lifepath.entity.*;
import com.example.Lifepath.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

class DemoDataInitializerTest {
    @Test
    void seedsFiveClearlyMarkedDemoOpportunitiesAndRelatedRecordsWithoutExistingRows() {
        OpportunityCategoryRepository categories = mock(OpportunityCategoryRepository.class);
        OpportunityRepository opportunities = mock(OpportunityRepository.class);
        EligibilityRuleRepository rules = mock(EligibilityRuleRepository.class);
        DeadlineRepository deadlines = mock(DeadlineRepository.class);
        DocumentRepository documents = mock(DocumentRepository.class);
        OpportunityDocumentRepository opportunityDocuments = mock(OpportunityDocumentRepository.class);
        PreparationTaskRepository preparationTasks = mock(PreparationTaskRepository.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        when(opportunities.findByTitleIgnoreCase(any())).thenReturn(Optional.empty());
        when(categories.findByName(any())).thenReturn(Optional.empty());
        when(documents.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(categories.save(any())).thenAnswer(call -> call.getArgument(0));
        when(opportunities.save(any())).thenAnswer(call -> call.getArgument(0));
        when(documents.save(any())).thenAnswer(call -> call.getArgument(0));

        new DemoDataInitializer(categories, opportunities, rules, deadlines, documents, opportunityDocuments,
                preparationTasks, notifications).run(mock(ApplicationArguments.class));

        verify(opportunities, times(5)).save(any(Opportunity.class));
        verify(categories, times(5)).save(any(OpportunityCategory.class));
        verify(rules, times(15)).save(any(EligibilityRule.class));
        verify(deadlines, times(5)).save(any(Deadline.class));
        verify(opportunityDocuments, atLeast(10)).save(any(OpportunityDocument.class));
        verify(preparationTasks, times(10)).save(any(PreparationTask.class));
    }
}

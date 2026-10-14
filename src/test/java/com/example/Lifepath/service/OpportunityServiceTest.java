package com.example.Lifepath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.Lifepath.entity.Opportunity;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class OpportunityServiceTest {
    @Test
    void blocksPhysicalDeleteWhenHistoricalChecksExist() {
        OpportunityRepository opportunities = mock(OpportunityRepository.class);
        EligibilityCheckRepository checks = mock(EligibilityCheckRepository.class);
        EligibilityRuleRepository rules = mock(EligibilityRuleRepository.class);
        Opportunity opportunity = mock(Opportunity.class);
        when(opportunities.findById(3L)).thenReturn(Optional.of(opportunity));
        when(checks.existsByOpportunityId(3L)).thenReturn(true);
        OpportunityService service = service(opportunities, checks, rules);
        ApiException exception = assertThrows(ApiException.class, () -> service.delete(3L));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(rules, never()).deleteByOpportunityId(any());
        verify(opportunities, never()).delete(any(Opportunity.class));
    }

    private OpportunityService service(OpportunityRepository opportunities, EligibilityCheckRepository checks,
            EligibilityRuleRepository rules) {
        return new OpportunityService(opportunities, mock(OpportunityCategoryRepository.class), checks, rules,
                mock(SavedOpportunityRepository.class), mock(DeadlineRepository.class),
                mock(OpportunityDocumentRepository.class), mock(PreparationTaskRepository.class));
    }
}

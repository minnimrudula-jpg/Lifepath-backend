package com.example.Lifepath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.Lifepath.dto.ApiDtos.EligibilityRuleRequest;
import com.example.Lifepath.entity.*;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class EligibilityRuleServiceTest {
    private EligibilityRuleRepository rules;
    private OpportunityRepository opportunities;
    private EligibilityRuleService service;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        rules = mock(EligibilityRuleRepository.class);
        opportunities = mock(OpportunityRepository.class);
        service = new EligibilityRuleService(rules, opportunities);
        opportunity = mock(Opportunity.class);
        when(opportunity.getId()).thenReturn(10L);
        when(opportunities.findById(10L)).thenReturn(Optional.of(opportunity));
        when(rules.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsAndListsValidRule() {
        var created = service.create(10L, request("age", ">=", "INTEGER", "18"));
        assertEquals(10L, created.opportunityId());
        assertEquals("age", created.fieldKey());
        assertTrue(created.active());
        when(rules.findByOpportunityId(10L)).thenReturn(List.of(new EligibilityRule(opportunity, "age", ">=", "INTEGER", "18", true, true)));
        assertEquals(1, service.list(10L).size());
    }

    @Test
    void updatesAndDeletesOwnedRule() {
        EligibilityRule rule = new EligibilityRule(opportunity, "age", ">=", "INTEGER", "18", true, true);
        when(rules.findById(4L)).thenReturn(Optional.of(rule));
        var updated = service.update(10L, 4L, request("age", ">=", "INTEGER", "21"));
        assertEquals("21", updated.expectedValue());
        service.delete(10L, 4L);
        verify(rules).delete(rule);
    }

    @Test
    void rejectsUnsupportedFieldTypeOperatorAndExpectedValue() {
        assertBad(request("citizenship", "=", "STRING", "X"), "Invalid field_key");
        assertBad(request("age", "=", "STRING", "18"), "Invalid value_type");
        assertBad(request("education", ">", "STRING", "B.Tech"), "Invalid operator");
        assertBad(request("annual_income", ">", "DECIMAL", "abc"), "Invalid expected_value");
    }

    @Test
    void permitsNumericOperatorsAndDecimalValues() {
        assertEquals("300000.50", service.create(10L,
                request("annual_income", ">=", "DECIMAL", "300000.50")).expectedValue());
    }

    @Test
    void rejectsRuleFromDifferentOpportunity() {
        Opportunity other = mock(Opportunity.class);
        when(other.getId()).thenReturn(20L);
        when(rules.findById(7L)).thenReturn(Optional.of(new EligibilityRule(other, "age", ">", "INTEGER", "18", true, true)));
        ApiException exception = assertThrows(ApiException.class,
                () -> service.update(10L, 7L, request("age", ">", "INTEGER", "18")));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertTrue(exception.getMessage().contains("another opportunity"));
    }

    @Test
    void newlyManagedActiveRuleIsLoadedByEligibilityEvaluation() {
        var managed = service.create(10L, request("education", "=", "STRING", "B.Tech"));
        EligibilityRule rule = new EligibilityRule(opportunity, managed.fieldKey(), managed.operator(), managed.valueType(),
                managed.expectedValue(), managed.required(), managed.active());
        when(rules.findByOpportunityIdAndActiveTrue(10L)).thenReturn(List.of(rule));
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(opportunities.findById(10L)).thenReturn(Optional.of(opportunity));
        when(opportunity.getTitle()).thenReturn("Scholarship");
        UserRepository users = mock(UserRepository.class);
        UserProfileRepository profiles = mock(UserProfileRepository.class);
        EligibilityCheckRepository checks = mock(EligibilityCheckRepository.class);
        EligibilityCheckResultRepository results = mock(EligibilityCheckResultRepository.class);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        UserProfile profile = mock(UserProfile.class);
        when(profile.getEducation()).thenReturn("B.Tech");
        when(profiles.findById(1L)).thenReturn(Optional.of(profile));
        when(checks.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = new EligibilityService(rules, new OpportunityService(opportunities,
                mock(OpportunityCategoryRepository.class), checks, rules, mock(SavedOpportunityRepository.class),
                mock(DeadlineRepository.class), mock(OpportunityDocumentRepository.class),
                mock(PreparationTaskRepository.class)), users, profiles, checks, results).check(1L, 10L);
        assertEquals("ELIGIBLE", response.finalStatus());
        assertEquals("SATISFIED", response.results().get(0).status());
    }

    private EligibilityRuleRequest request(String field, String operator, String type, String value) {
        return new EligibilityRuleRequest(field, operator, type, value, true, true);
    }

    private void assertBad(EligibilityRuleRequest request, String messagePart) {
        ApiException ex = assertThrows(ApiException.class, () -> service.create(10L, request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains(messagePart));
    }
}

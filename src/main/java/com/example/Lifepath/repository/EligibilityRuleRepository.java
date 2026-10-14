package com.example.Lifepath.repository;

import com.example.Lifepath.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule, Long> {
    List<EligibilityRule> findByOpportunityIdAndActiveTrue(Long opportunityId);
    List<EligibilityRule> findByOpportunityId(Long opportunityId);
    java.util.Optional<EligibilityRule> findByIdAndOpportunityId(Long id, Long opportunityId);
    void deleteByOpportunityId(Long opportunityId);
    boolean existsByOpportunityIdAndFieldKeyAndOperatorAndValueTypeAndExpectedValue(
            Long opportunityId, String fieldKey, String operator, String valueType, String expectedValue);
}

package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.EligibilityRuleRequest;
import com.example.Lifepath.dto.ApiDtos.EligibilityRuleView;
import com.example.Lifepath.entity.EligibilityRule;
import com.example.Lifepath.entity.Opportunity;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.EligibilityRuleRepository;
import com.example.Lifepath.repository.OpportunityRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EligibilityRuleService {
    private static final Set<String> FIELDS = Set.of("age", "education", "annual_income", "experience_years", "branch", "category");
    private static final Set<String> NUMERIC_OPERATORS = Set.of("=", "!=", ">", ">=", "<", "<=");
    private final EligibilityRuleRepository rules;
    private final OpportunityRepository opportunities;

    public EligibilityRuleService(EligibilityRuleRepository rules, OpportunityRepository opportunities) {
        this.rules = rules;
        this.opportunities = opportunities;
    }

    @Transactional(readOnly = true)
    public List<EligibilityRuleView> list(Long opportunityId) {
        requireOpportunity(opportunityId);
        return rules.findByOpportunityId(opportunityId).stream().map(this::view).toList();
    }

    @Transactional
    public EligibilityRuleView create(Long opportunityId, EligibilityRuleRequest request) {
        Opportunity opportunity = requireOpportunity(opportunityId);
        validate(request);
        EligibilityRule rule = new EligibilityRule(opportunity, request.fieldKey(), request.operator(),
                request.valueType(), request.expectedValue(), request.required() == null || request.required(),
                request.active() == null || request.active());
        return view(rules.save(rule));
    }

    @Transactional
    public EligibilityRuleView update(Long opportunityId, Long ruleId, EligibilityRuleRequest request) {
        requireOpportunity(opportunityId);
        EligibilityRule rule = rules.findById(ruleId).orElseThrow(() -> notFound("Eligibility rule not found"));
        if (!rule.getOpportunity().getId().equals(opportunityId))
            throw new ApiException(HttpStatus.CONFLICT, "Eligibility rule belongs to another opportunity");
        validate(request);
        rule.update(request.fieldKey(), request.operator(), request.valueType(), request.expectedValue(),
                request.required() == null ? rule.isRequired() : request.required(),
                request.active() == null ? rule.isActive() : request.active());
        return view(rules.save(rule));
    }

    @Transactional
    public void delete(Long opportunityId, Long ruleId) {
        requireOpportunity(opportunityId);
        EligibilityRule rule = rules.findById(ruleId).orElseThrow(() -> notFound("Eligibility rule not found"));
        if (!rule.getOpportunity().getId().equals(opportunityId))
            throw new ApiException(HttpStatus.CONFLICT, "Eligibility rule belongs to another opportunity");
        rules.delete(rule);
    }

    private Opportunity requireOpportunity(Long id) {
        return opportunities.findById(id).orElseThrow(() -> notFound("Opportunity not found"));
    }

    private void validate(EligibilityRuleRequest r) {
        if (r == null || r.fieldKey() == null || !FIELDS.contains(r.fieldKey()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid field_key");
        String expectedType = switch (r.fieldKey()) {
            case "age", "experience_years" -> "INTEGER";
            case "annual_income" -> "DECIMAL";
            default -> "STRING";
        };
        if (!expectedType.equals(r.valueType()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid value_type for " + r.fieldKey());
        Set<String> operators = expectedType.equals("STRING") ? Set.of("=", "!=") : NUMERIC_OPERATORS;
        if (!operators.contains(r.operator()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid operator for " + r.valueType());
        if (r.expectedValue() == null || r.expectedValue().isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid expected_value");
        try {
            if (expectedType.equals("INTEGER")) Integer.parseInt(r.expectedValue());
            else if (expectedType.equals("DECIMAL")) new BigDecimal(r.expectedValue());
        } catch (NumberFormatException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid expected_value for " + r.valueType());
        }
    }

    private EligibilityRuleView view(EligibilityRule r) {
        return new EligibilityRuleView(r.getId(), r.getOpportunity().getId(), r.getFieldKey(), r.getOperator(),
                r.getValueType(), r.getExpectedValue(), r.isRequired(), r.isActive());
    }

    private ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }
}

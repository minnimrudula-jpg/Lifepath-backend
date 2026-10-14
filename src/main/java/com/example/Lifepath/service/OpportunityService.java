package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.entity.*;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpportunityService {
    private final OpportunityRepository opportunities;
    private final OpportunityCategoryRepository categories;
    private final EligibilityCheckRepository checks;
    private final EligibilityRuleRepository rules;
    private final SavedOpportunityRepository savedOpportunities;
    private final DeadlineRepository deadlines;
    private final OpportunityDocumentRepository opportunityDocuments;
    private final PreparationTaskRepository preparationTasks;

    public OpportunityService(OpportunityRepository o, OpportunityCategoryRepository c, EligibilityCheckRepository checks,
            EligibilityRuleRepository rules, SavedOpportunityRepository savedOpportunities, DeadlineRepository deadlines,
            OpportunityDocumentRepository opportunityDocuments, PreparationTaskRepository preparationTasks) {
        opportunities = o;
        categories = c;
        this.checks = checks;
        this.rules = rules;
        this.savedOpportunities = savedOpportunities;
        this.deadlines = deadlines;
        this.opportunityDocuments = opportunityDocuments;
        this.preparationTasks = preparationTasks;
    }

    @Transactional(readOnly = true)
    public List<OpportunityView> list(String q) {
        return (q == null || q.isBlank() ? opportunities.findAll() : opportunities.findByTitleContainingIgnoreCase(q))
                .stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public OpportunityView get(Long id) {
        return view(find(id));
    }

    @Transactional
    public OpportunityView create(OpportunityRequest r) {
        Opportunity o = new Opportunity();
        apply(o, r);
        return view(opportunities.save(o));
    }

    @Transactional
    public OpportunityView update(Long id, OpportunityRequest r) {
        Opportunity o = find(id);
        apply(o, r);
        return view(opportunities.save(o));
    }

    @Transactional
    public void delete(Long id) {
        Opportunity opportunity = find(id);
        if (checks.existsByOpportunityId(id))
            throw new ApiException(HttpStatus.CONFLICT,
                    "Opportunity cannot be physically deleted because historical eligibility records reference it");
        if (savedOpportunities.existsByOpportunity_Id(id) || deadlines.existsByOpportunityId(id)
                || opportunityDocuments.existsByOpportunityId(id) || preparationTasks.existsByOpportunityId(id))
            throw new ApiException(HttpStatus.CONFLICT,
                    "Opportunity is referenced by saved opportunities, deadlines, documents, or preparation tasks and cannot be deleted");
        rules.deleteByOpportunityId(id);
        opportunities.delete(opportunity);
    }

    public Opportunity find(Long id) {
        return opportunities.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Opportunity not found"));
    }

    private void apply(Opportunity o, OpportunityRequest r) {
        o.setTitle(r.title());
        o.setDescription(r.description());
        o.setProvider(r.provider());
        o.setOfficialUrl(r.officialUrl());
        if (r.categoryId() != null)
            o.setCategory(categories.findById(r.categoryId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found")));
        o.setStatus(r.status() == null ? "ACTIVE" : r.status());
    }

    private OpportunityView view(Opportunity o) {
        return new OpportunityView(o.getId(), o.getCategory() == null ? null : o.getCategory().getId(),
                o.getCategory() == null ? null : o.getCategory().getName(), o.getTitle(), o.getDescription(),
                o.getProvider(), o.getOfficialUrl(), o.getStatus());
    }
}

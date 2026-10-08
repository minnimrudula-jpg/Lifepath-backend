package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.OpportunityView;
import com.example.Lifepath.entity.SavedOpportunity;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedOpportunityService {
    private final SavedOpportunityRepository saved;
    private final UserRepository users;
    private final OpportunityRepository opportunities;
    private final OpportunityService opportunityService;

    public SavedOpportunityService(SavedOpportunityRepository s, UserRepository u, OpportunityRepository o,
            OpportunityService os) {
        saved = s;
        users = u;
        opportunities = o;
        opportunityService = os;
    }

    @Transactional(readOnly = true)
    public List<OpportunityView> list(Long userId) {
        return saved.findByUser_Id(userId).stream().map(s -> opportunityService.get(s.getOpportunity().getId()))
                .toList();
    }

    @Transactional
    public void save(Long userId, Long opportunityId) {
        if (saved.findByUser_IdAndOpportunity_Id(userId, opportunityId).isPresent())
            return;
        var user = users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        var opportunity = opportunities.findById(opportunityId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        saved.save(new SavedOpportunity(user, opportunity));
    }

    @Transactional
    public void remove(Long userId, Long opportunityId) {
        saved.findByUser_IdAndOpportunity_Id(userId, opportunityId).ifPresent(saved::delete);
    }
}

package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.OpportunityView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.SavedOpportunityService;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saved-opportunities")
public class SavedOpportunityController {
    private final SavedOpportunityService service;

    public SavedOpportunityController(SavedOpportunityService s) {
        service = s;
    }

    private Long userId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    @GetMapping
    public List<OpportunityView> list() {
        return service.list(userId());
    }

    @PostMapping("/{opportunityId}")
    public void save(@PathVariable Long opportunityId) {
        service.save(userId(), opportunityId);
    }

    @DeleteMapping("/{opportunityId}")
    public void remove(@PathVariable Long opportunityId) {
        service.remove(userId(), opportunityId);
    }
}

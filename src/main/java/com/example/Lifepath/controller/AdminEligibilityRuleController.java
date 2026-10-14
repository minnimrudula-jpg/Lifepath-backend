package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.EligibilityRuleRequest;
import com.example.Lifepath.dto.ApiDtos.EligibilityRuleView;
import com.example.Lifepath.service.EligibilityRuleService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/opportunities/{opportunityId}/rules")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEligibilityRuleController {
    private final EligibilityRuleService service;

    public AdminEligibilityRuleController(EligibilityRuleService service) {
        this.service = service;
    }

    @GetMapping
    public List<EligibilityRuleView> list(@PathVariable Long opportunityId) {
        return service.list(opportunityId);
    }

    @PostMapping
    public EligibilityRuleView create(@PathVariable Long opportunityId, @RequestBody EligibilityRuleRequest request) {
        return service.create(opportunityId, request);
    }

    @PutMapping("/{ruleId}")
    public EligibilityRuleView update(@PathVariable Long opportunityId, @PathVariable Long ruleId,
            @RequestBody EligibilityRuleRequest request) {
        return service.update(opportunityId, ruleId, request);
    }

    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable Long opportunityId, @PathVariable Long ruleId) {
        service.delete(opportunityId, ruleId);
    }
}

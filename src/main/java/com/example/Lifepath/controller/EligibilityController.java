package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.EligibilityResponse;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.EligibilityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {
    private final EligibilityService service;

    public EligibilityController(EligibilityService s) {
        service = s;
    }

    @PostMapping("/check/{opportunityId}")
    public EligibilityResponse check(@PathVariable Long opportunityId) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.check(u.getId(), opportunityId);
    }
}

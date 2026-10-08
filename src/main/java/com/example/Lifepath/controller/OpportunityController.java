package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.service.OpportunityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final OpportunityService service;

    public OpportunityController(OpportunityService s) {
        service = s;
    }

    @GetMapping
    public List<OpportunityView> list(@RequestParam(required = false) String search) {
        return service.list(search);
    }

    @GetMapping("/{id}")
    public OpportunityView get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public OpportunityView create(@Valid @RequestBody OpportunityRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OpportunityView update(@PathVariable Long id, @Valid @RequestBody OpportunityRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.PreparationTaskStatusRequest;
import com.example.Lifepath.dto.ApiDtos.PreparationTaskView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.PreparationService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preparation")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class PreparationController {
    private final PreparationService service;
    public PreparationController(PreparationService service) { this.service = service; }

    @GetMapping
    public List<PreparationTaskView> list() { return service.list(currentUserId()); }

    @PutMapping("/{taskId}")
    public PreparationTaskView updateStatus(@PathVariable Long taskId,
            @RequestBody PreparationTaskStatusRequest request) {
        return service.updateStatus(currentUserId(), taskId, request);
    }

    private Long currentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}

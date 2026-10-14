package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/users")
    public List<AdminUserView> users() { return service.listUsers(); }

    @GetMapping("/documents")
    public List<AdminDocumentView> documents() { return service.listDocuments(); }

    @PostMapping("/documents")
    public AdminDocumentView createDocument(@Valid @RequestBody AdminDocumentRequest request) {
        return service.createDocument(request);
    }

    @PutMapping("/documents/{id}")
    public AdminDocumentView updateDocument(@PathVariable Long id, @Valid @RequestBody AdminDocumentRequest request) {
        return service.updateDocument(id, request);
    }

    @DeleteMapping("/documents/{id}")
    public void deleteDocument(@PathVariable Long id) { service.deleteDocument(id); }

    @GetMapping("/analytics")
    public AdminAnalyticsView analytics() { return service.analytics(); }
}

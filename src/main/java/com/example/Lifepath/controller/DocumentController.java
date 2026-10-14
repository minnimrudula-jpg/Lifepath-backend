package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.DocumentStatusRequest;
import com.example.Lifepath.dto.ApiDtos.UserDocumentView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.DocumentService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class DocumentController {
    private final DocumentService service;
    public DocumentController(DocumentService service) { this.service = service; }

    @GetMapping
    public List<UserDocumentView> list() { return service.list(currentUserId()); }

    @PutMapping("/{documentId}")
    public UserDocumentView updateStatus(@PathVariable Long documentId, @RequestBody DocumentStatusRequest request) {
        return service.updateStatus(currentUserId(), documentId, request);
    }

    private Long currentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}

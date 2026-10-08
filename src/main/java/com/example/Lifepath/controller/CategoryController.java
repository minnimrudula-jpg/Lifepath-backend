package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.CategoryRequest;
import com.example.Lifepath.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService s) {
        service = s;
    }

    @GetMapping
    public Object all() {
        return service.all();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Object create(@Valid @RequestBody CategoryRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Object update(@PathVariable Long id, @Valid @RequestBody CategoryRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

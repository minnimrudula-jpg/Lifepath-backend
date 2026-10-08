package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.CategoryRequest;
import com.example.Lifepath.entity.OpportunityCategory;
import com.example.Lifepath.repository.OpportunityCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final OpportunityCategoryRepository repo;

    public CategoryService(OpportunityCategoryRepository r) {
        repo = r;
    }

    public List<OpportunityCategory> all() {
        return repo.findAll();
    }

    public OpportunityCategory create(CategoryRequest r) {
        return repo.save(new OpportunityCategory(r.name(), r.description()));
    }

    public OpportunityCategory update(Long id, CategoryRequest r) {
        var c = repo.findById(id).orElseThrow();
        c.setName(r.name());
        c.setDescription(r.description());
        return repo.save(c);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

package com.example.Lifepath.repository;

import com.example.Lifepath.entity.OpportunityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityCategoryRepository extends JpaRepository<OpportunityCategory, Long> {
    java.util.Optional<OpportunityCategory> findByName(String name);
}

package com.example.Lifepath.repository;

import com.example.Lifepath.entity.PreparationTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreparationTaskRepository extends JpaRepository<PreparationTask, Long> {
    boolean existsByOpportunityIdAndTitle(Long opportunityId, String title);
    boolean existsByOpportunityId(Long opportunityId);
    java.util.List<PreparationTask> findAllByTitle(String title);
}

package com.example.Lifepath.repository;

import com.example.Lifepath.entity.SavedOpportunity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedOpportunityRepository extends JpaRepository<SavedOpportunity, Long> {
    List<SavedOpportunity> findByUser_Id(Long userId);

    Optional<SavedOpportunity> findByUser_IdAndOpportunity_Id(Long userId, Long opportunityId);
}

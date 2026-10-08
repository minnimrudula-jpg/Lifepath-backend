package com.example.Lifepath.repository;
import com.example.Lifepath.entity.*; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule,Long>{List<EligibilityRule> findByOpportunityIdAndActiveTrue(Long opportunityId);}

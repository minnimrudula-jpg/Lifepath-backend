package com.example.Lifepath.repository;
import com.example.Lifepath.entity.EligibilityCheck; import org.springframework.data.jpa.repository.JpaRepository;
public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck,Long>{
    boolean existsByOpportunityId(Long opportunityId);
    @org.springframework.data.jpa.repository.Query("select c.finalStatus, count(c) from EligibilityCheck c group by c.finalStatus")
    java.util.List<Object[]> countGroupedByFinalStatus();
}

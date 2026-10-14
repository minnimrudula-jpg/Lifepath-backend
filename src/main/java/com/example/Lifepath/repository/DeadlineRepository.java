package com.example.Lifepath.repository;
import com.example.Lifepath.entity.Deadline;import java.time.LocalDateTime;import java.util.List;import org.springframework.data.jpa.repository.JpaRepository;
public interface DeadlineRepository extends JpaRepository<Deadline,Long>{List<Deadline> findByDeadlineGreaterThanEqualOrderByDeadlineAsc(LocalDateTime from); List<Deadline> findAllByOrderByDeadlineAsc(); List<Deadline> findAllByTitle(String title); List<Deadline> findAllByOpportunityId(Long opportunityId); boolean existsByOpportunityId(Long opportunityId);}

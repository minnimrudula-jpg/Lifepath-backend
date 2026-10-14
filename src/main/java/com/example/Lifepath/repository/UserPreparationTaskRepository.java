package com.example.Lifepath.repository;

import com.example.Lifepath.entity.UserPreparationTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreparationTaskRepository extends JpaRepository<UserPreparationTask, Long> {
    Optional<UserPreparationTask> findByUserIdAndTaskId(Long userId, Long taskId);
    List<UserPreparationTask> findAllByUserId(Long userId);
}

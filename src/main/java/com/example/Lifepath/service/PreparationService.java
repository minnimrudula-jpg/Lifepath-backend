package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.PreparationTaskStatusRequest;
import com.example.Lifepath.dto.ApiDtos.PreparationTaskView;
import com.example.Lifepath.entity.PreparationTask;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.entity.UserPreparationTask;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.PreparationTaskRepository;
import com.example.Lifepath.repository.UserPreparationTaskRepository;
import com.example.Lifepath.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreparationService {
    private static final Set<String> STATUSES = Set.of("NOT_STARTED", "IN_PROGRESS", "COMPLETED");
    private final PreparationTaskRepository tasks;
    private final UserPreparationTaskRepository userTasks;
    private final UserRepository users;

    public PreparationService(PreparationTaskRepository tasks, UserPreparationTaskRepository userTasks,
            UserRepository users) {
        this.tasks = tasks;
        this.userTasks = userTasks;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<PreparationTaskView> list(Long userId) {
        Map<Long, String> statuses = userTasks.findAllByUserId(userId).stream()
                .collect(Collectors.toMap(link -> link.getTask().getId(), link -> link.getStatus() == null ? "NOT_STARTED" : link.getStatus(),
                        (left, right) -> left));
        return tasks.findAll().stream().sorted(Comparator.comparing((PreparationTask task) ->
                task.getOpportunity() == null ? "" : task.getOpportunity().getTitle()).thenComparing(PreparationTask::getTitle))
                .map(task -> view(task, statuses.getOrDefault(task.getId(), "NOT_STARTED"))).toList();
    }

    @Transactional
    public PreparationTaskView updateStatus(Long userId, Long taskId, PreparationTaskStatusRequest request) {
        if (request == null || request.status() == null || !STATUSES.contains(request.status()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid preparation task status");
        PreparationTask task = tasks.findById(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Preparation task not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        UserPreparationTask link = userTasks.findByUserIdAndTaskId(userId, taskId)
                .orElseGet(() -> new UserPreparationTask(user, task, "NOT_STARTED"));
        link.setStatus(request.status());
        userTasks.save(link);
        return view(task, link.getStatus());
    }

    private PreparationTaskView view(PreparationTask task, String status) {
        return new PreparationTaskView(task.getId(), task.getTitle(), task.getDescription(),
                task.getOpportunity() == null ? null : task.getOpportunity().getId(),
                task.getOpportunity() == null ? null : task.getOpportunity().getTitle(), status);
    }
}

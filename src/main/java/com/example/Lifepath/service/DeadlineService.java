package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.DeadlineView;
import com.example.Lifepath.repository.DeadlineRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeadlineService {
    private final DeadlineRepository deadlines;

    public DeadlineService(DeadlineRepository d) {
        deadlines = d;
    }

    @Transactional(readOnly = true)
    public List<DeadlineView> upcoming() {
        return deadlines.findByDeadlineGreaterThanEqualOrderByDeadlineAsc(LocalDateTime.now()).stream()
                .map(d -> new DeadlineView(d.getId(), d.getOpportunity().getId(), d.getOpportunity().getTitle(),
                        d.getTitle(), d.getDeadline(), d.getDescription()))
                .toList();
    }
}

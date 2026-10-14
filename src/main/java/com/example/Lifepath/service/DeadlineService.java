package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.DeadlineView;
import com.example.Lifepath.repository.DeadlineRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        LocalDate today = LocalDate.now();
        return deadlines.findAllByOrderByDeadlineAsc().stream()
                .map(d -> {
                    Integer remaining = d.getDeadline() == null ? null
                            : Math.toIntExact(ChronoUnit.DAYS.between(today, d.getDeadline().toLocalDate()));
                    String status = remaining == null ? "UNSCHEDULED"
                            : remaining < 0 ? "PASSED" : remaining <= 7 ? "DUE_SOON" : "UPCOMING";
                    return new DeadlineView(d.getId(), d.getOpportunity().getId(), d.getOpportunity().getTitle(),
                            d.getTitle(), d.getDeadline(), d.getDescription(), status, remaining);
                })
                .toList();
    }
}

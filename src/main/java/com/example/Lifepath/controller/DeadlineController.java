package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.DeadlineView;
import com.example.Lifepath.service.DeadlineService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deadlines")
public class DeadlineController {
    private final DeadlineService service;

    public DeadlineController(DeadlineService s) {
        service = s;
    }

    @GetMapping
    public List<DeadlineView> upcoming() {
        return service.upcoming();
    }
}

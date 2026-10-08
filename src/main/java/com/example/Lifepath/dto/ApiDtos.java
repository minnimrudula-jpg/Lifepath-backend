package com.example.Lifepath.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ApiDtos {
    public record ProfileRequest(LocalDate dateOfBirth, String gender, String category,
            @DecimalMin("0") BigDecimal annualIncome, String education, String branch, @Min(0) Integer experienceYears,
            String phone, String address, String city, String state) {
    }

    public record ProfileView(Long userId, LocalDate dateOfBirth, String gender, String category,
            BigDecimal annualIncome, String education, String branch, Integer experienceYears, String phone,
            String address, String city, String state) {
    }

    public record CategoryRequest(@NotBlank String name, String description) {
    }

    public record OpportunityRequest(@NotBlank String title, String description, String provider, String officialUrl,
            Long categoryId, String status) {
    }

    public record OpportunityView(Long id, Long categoryId, String category, String title, String description,
            String provider, String officialUrl, String status) {
    }

    public record DeadlineView(Long id, Long opportunityId, String opportunityTitle, String title,
            java.time.LocalDateTime deadline, String description) {
    }

    public record RuleResult(Long ruleId, String criterion, String requirement, String userValue, String status,
            String explanation) {
    }

    public record EligibilityResponse(Long opportunityId, String opportunityTitle, String finalStatus,
            java.util.List<RuleResult> results) {
    }
}

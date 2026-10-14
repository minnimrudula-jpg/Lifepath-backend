package com.example.Lifepath.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public record OpportunityRequest(@NotBlank @Size(max = 255) String title, @Size(max = 4000) String description,
            @Size(max = 255) String provider, @Size(max = 2048) String officialUrl, Long categoryId,
            @Size(max = 50) String status) {
    }

    public record OpportunityView(Long id, Long categoryId, String category, String title, String description,
            String provider, String officialUrl, String status) {
    }

    public record EligibilityRuleRequest(@JsonProperty("field_key") String fieldKey, String operator,
            @JsonProperty("value_type") String valueType,
            @JsonProperty("expected_value") String expectedValue, Boolean required, Boolean active) {
    }

    public record EligibilityRuleView(Long id, Long opportunityId, @JsonProperty("field_key") String fieldKey,
            String operator, @JsonProperty("value_type") String valueType,
            @JsonProperty("expected_value") String expectedValue, boolean required, boolean active) {
    }

    public record DeadlineView(Long id, Long opportunityId, String opportunityTitle, String title,
            java.time.LocalDateTime deadline, String description, String status, Integer daysRemaining) {
    }

    public record UserDocumentView(Long id, String name, String description, java.util.List<String> opportunities,
            String status) {
    }

    public record AdminDocumentRequest(@NotBlank @Size(max = 255) String name, @Size(max = 255) String description,
            @Size(max = 255) String fileUrl) { }

    public record AdminDocumentView(Long id, String name, String description, String fileUrl) { }

    public record AdminUserView(Long id, String name, String email, String role, java.time.LocalDateTime createdAt) { }

    public record SavedOpportunityCount(Long opportunityId, String opportunityTitle, long savedCount) { }

    public record AdminAnalyticsView(long totalUsers, long totalOpportunities, long totalCategories,
            long totalSavedOpportunities, long totalEligibilityChecks,
            java.util.Map<String, Long> eligibilityChecksByFinalStatus,
            java.util.List<SavedOpportunityCount> savedOpportunitiesByOpportunity,
            java.util.Map<String, Long> userRegistrationsByDate) { }

    public record DocumentStatusRequest(String status) {
    }

    public record PreparationTaskView(Long id, String title, String description, Long opportunityId,
            String opportunityTitle, String status) {
    }

    public record PreparationTaskStatusRequest(String status) {
    }

    public record NotificationView(Long id, String message, java.time.LocalDateTime createdAt, boolean read) {
    }

    public record SettingsView(Long userId, String name, String email, String role) {
    }

    public record ChangePasswordRequest(@jakarta.validation.constraints.NotBlank String currentPassword,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(min = 8, max = 72) String newPassword) {
    }

    public record RuleResult(Long ruleId, String criterion, String requirement, String userValue, String status,
            String explanation) {
    }

    public record EligibilityResponse(Long opportunityId, String opportunityTitle, String finalStatus,
            java.util.List<RuleResult> results) {
    }
}

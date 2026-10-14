package com.example.Lifepath.config;

import com.example.Lifepath.entity.*;
import com.example.Lifepath.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Seeds development catalogue data and cleans the visible labels of previously seeded rows. */
@Component
public class DemoDataInitializer implements ApplicationRunner {
    private final OpportunityCategoryRepository categories;
    private final OpportunityRepository opportunities;
    private final EligibilityRuleRepository rules;
    private final DeadlineRepository deadlines;
    private final DocumentRepository documents;
    private final OpportunityDocumentRepository opportunityDocuments;
    private final PreparationTaskRepository preparationTasks;
    private final NotificationRepository notifications;

    public DemoDataInitializer(OpportunityCategoryRepository categories, OpportunityRepository opportunities,
            EligibilityRuleRepository rules, DeadlineRepository deadlines, DocumentRepository documents,
            OpportunityDocumentRepository opportunityDocuments, PreparationTaskRepository preparationTasks,
            NotificationRepository notifications) {
        this.categories = categories;
        this.opportunities = opportunities;
        this.rules = rules;
        this.deadlines = deadlines;
        this.documents = documents;
        this.opportunityDocuments = opportunityDocuments;
        this.preparationTasks = preparationTasks;
        this.notifications = notifications;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        migrateLegacyLabels();
        seed(new DemoOpportunity("Graduate Opportunity Program 2026", "Competitive Exam",
                "A graduate opportunity listing with age, education, and category requirements.", List.of(
                        new DemoRule("age", ">=", "INTEGER", "18"),
                        new DemoRule("education", "=", "STRING", "B.Tech"),
                        new DemoRule("category", "=", "STRING", "BC")),
                List.of("Identity document", "Academic transcripts"),
                List.of("Review eligibility requirements", "Prepare application materials")),
                "LifePath Graduate Opportunity Test 2026");
        seed(new DemoOpportunity("Government Career Opportunity 2026", "Government Job",
                "A career opportunity listing with age, education, and income requirements.", List.of(
                        new DemoRule("age", ">=", "INTEGER", "21"),
                        new DemoRule("education", "=", "STRING", "B.Tech"),
                        new DemoRule("annual_income", "<=", "DECIMAL", "500000")),
                List.of("Identity document", "Education certificate"),
                List.of("Review role requirements", "Prepare your resume")),
                "LifePath Government Career Program 2026");
        seed(new DemoOpportunity("Student Scholarship 2026", "Scholarship",
                "A scholarship listing with age, education, and income requirements.", List.of(
                        new DemoRule("age", ">=", "INTEGER", "18"),
                        new DemoRule("education", "=", "STRING", "B.Tech"),
                        new DemoRule("annual_income", "<=", "DECIMAL", "300000")),
                List.of("Identity document", "Academic transcripts", "Income statement"),
                List.of("Collect application documents", "Review scholarship requirements")),
                "LifePath Student Scholarship 2026");
        seed(new DemoOpportunity("Technology Internship 2026", "Internship",
                "An internship listing with age, education, and experience requirements.", List.of(
                        new DemoRule("age", ">=", "INTEGER", "18"),
                        new DemoRule("education", "=", "STRING", "B.Tech"),
                        new DemoRule("experience_years", ">=", "INTEGER", "0")),
                List.of("Identity document", "Academic record"),
                List.of("Review internship details", "Prepare a project summary")),
                "LifePath Tech Internship 2026");
        seed(new DemoOpportunity("Skill Development Fellowship 2026", "Fellowship",
                "A fellowship listing with age, education, and branch requirements.", List.of(
                        new DemoRule("age", ">=", "INTEGER", "18"),
                        new DemoRule("education", "=", "STRING", "B.Tech"),
                        new DemoRule("branch", "=", "STRING", "CSE")),
                List.of("Identity document", "Branch certificate"),
                List.of("Review fellowship requirements", "Prepare a learning plan")),
                "LifePath Skill Fellowship 2026");
    }

    private void seed(DemoOpportunity data, String legacyTitle) {
        if (opportunities.findByTitleIgnoreCase(data.title()).isPresent())
            return;

        OpportunityCategory category = categories.findByName(data.category()).orElseGet(() ->
                categories.save(new OpportunityCategory(data.category(), "Opportunities grouped by this category.")));
        Opportunity opportunityToSave = opportunities.findByTitleIgnoreCase(legacyTitle).orElseGet(Opportunity::new);
        opportunityToSave.setCategory(category);
        opportunityToSave.setTitle(data.title());
        opportunityToSave.setDescription(data.description());
        opportunityToSave.setProvider(null);
        opportunityToSave.setOfficialUrl(null);
        opportunityToSave.setStatus("ACTIVE");
        Opportunity opportunity = opportunities.save(opportunityToSave);

        if (rules.findByOpportunityId(opportunity.getId()).isEmpty()) {
            for (DemoRule rule : data.rules())
                rules.save(new EligibilityRule(opportunity, rule.fieldKey(), rule.operator(), rule.valueType(),
                        rule.expectedValue(), true, true));
        }

        if (deadlines.findAllByOpportunityId(opportunity.getId()).isEmpty()) {
            LocalDateTime deadline = LocalDate.now().plusDays(45).atTime(23, 59);
            deadlines.save(new Deadline(opportunity, "Application closing date", deadline,
                    "Review the opportunity details and confirm applicable dates before applying."));
        }

        for (String documentName : data.documents()) {
            Document document = documents.findByNameIgnoreCase(documentName).orElseGet(() ->
                    documents.save(new Document(documentName, "Checklist item. No file is stored by LifePath.", null)));
            if (!opportunityDocuments.existsByOpportunityIdAndDocumentId(opportunity.getId(), document.getId()))
                opportunityDocuments.save(new OpportunityDocument(opportunity, document));
        }

        for (String taskTitle : data.preparationTasks()) {
            if (!preparationTasks.existsByOpportunityIdAndTitle(opportunity.getId(), taskTitle))
                preparationTasks.save(new PreparationTask(opportunity, taskTitle,
                        "Use this task to plan and track your preparation."));
        }
    }

    private void migrateLegacyLabels() {
        Map<String, String> oldDocuments = Map.of(
                "Demo identity document", "Identity document",
                "Demo academic transcripts", "Academic transcripts",
                "Demo education certificate", "Education certificate",
                "Demo income statement", "Income statement",
                "Demo academic record", "Academic record",
                "Demo branch certificate", "Branch certificate");
        oldDocuments.forEach((oldName, newName) -> documents.findByNameIgnoreCase(oldName).ifPresent(document -> {
            document.setName(newName);
            document.setDescription("Checklist item. No file is stored by LifePath.");
            documents.save(document);
        }));

        Map<String, String> oldTasks = Map.ofEntries(
                Map.entry("Review the demo eligibility criteria", "Review eligibility requirements"),
                Map.entry("Prepare demo application materials", "Prepare application materials"),
                Map.entry("Review the demo role requirements", "Review role requirements"),
                Map.entry("Prepare a demo resume", "Prepare your resume"),
                Map.entry("Collect demo scholarship documents", "Collect application documents"),
                Map.entry("Review demo scholarship criteria", "Review scholarship requirements"),
                Map.entry("Review the demo internship details", "Review internship details"),
                Map.entry("Prepare a demo project summary", "Prepare a project summary"),
                Map.entry("Review the demo fellowship criteria", "Review fellowship requirements"),
                Map.entry("Prepare a demo learning plan", "Prepare a learning plan"));
        oldTasks.forEach((oldTitle, newTitle) -> preparationTasks.findAllByTitle(oldTitle).forEach(task -> {
            task.setTitle(newTitle);
            task.setDescription("Use this task to plan and track your preparation.");
            preparationTasks.save(task);
        }));

        deadlines.findAllByTitle("Demo application deadline").forEach(deadline -> {
            deadline.update("Application closing date",
                    "Review the opportunity details and confirm applicable dates before applying.");
            deadlines.save(deadline);
        });

        notifications.findAllByMessageIn(List.of(
                "Welcome to LifePath. Your demo workspace is ready.",
                "New fictional demo opportunities are available to explore.",
                "Your profile is incomplete. Add details to evaluate demo eligibility rules.")).forEach(notification -> {
                    notification.setMessage(switch (notification.getMessage()) {
                        case "Welcome to LifePath. Your demo workspace is ready." -> "Welcome to LifePath. Your account is ready.";
                        case "New fictional demo opportunities are available to explore." -> "New opportunities are available to explore.";
                        default -> "Your profile is incomplete. Add details to evaluate eligibility requirements.";
                    });
                    notifications.save(notification);
                });

        Set<String> seededCategories = Set.of("Competitive Exam", "Government Job", "Scholarship", "Internship", "Fellowship");
        categories.findAll().stream().filter(category -> seededCategories.contains(category.getName())
                && category.getDescription() != null
                && (category.getDescription().toLowerCase().contains("demo")
                        || category.getDescription().toLowerCase().contains("fictional")))
                .forEach(category -> {
                    category.setDescription("Opportunities grouped by this category.");
                    categories.save(category);
                });
    }

    private record DemoOpportunity(String title, String category, String description, List<DemoRule> rules,
            List<String> documents, List<String> preparationTasks) {
    }

    private record DemoRule(String fieldKey, String operator, String valueType, String expectedValue) {
    }
}

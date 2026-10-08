package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "eligibility_check_results")
public class EligibilityCheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "eligibility_check_id", nullable = false)
    private EligibilityCheck eligibilityCheck;
    @Column(name = "eligibility_rule_id")
    private Long eligibilityRuleId;
    @Column(name = "criterion_snapshot", nullable = false)
    private String criterionSnapshot;
    @Column(name = "requirement_snapshot", nullable = false)
    private String requirementSnapshot;
    @Column(name = "user_value_snapshot")
    private String userValueSnapshot;
    @Column(nullable = false)
    private String status;
    @Column(length = 1000)
    private String explanation;

    protected EligibilityCheckResult() {
    }

    public EligibilityCheckResult(EligibilityCheck c, Long rule, String criterion, String requirement, String value,
            String s, String explanation) {
        eligibilityCheck = c;
        eligibilityRuleId = rule;
        criterionSnapshot = criterion;
        requirementSnapshot = requirement;
        userValueSnapshot = value;
        status = s;
        this.explanation = explanation;
    }
}

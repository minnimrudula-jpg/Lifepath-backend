package com.example.Lifepath.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "eligibility_rules", indexes = @Index(columnList = "opportunity_id, active"))
public class EligibilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;
    @Column(name = "field_key", nullable = false)
    private String fieldKey;
    @Column(nullable = false)
    private String operator;
    @Column(name = "value_type", nullable = false)
    private String valueType;
    @Column(name = "expected_value", nullable = false)
    private String expectedValue;
    @Column(nullable = false)
    private boolean required = true;
    @Column(nullable = false)
    private boolean active = true;

    protected EligibilityRule() {
    }

    public EligibilityRule(Opportunity opportunity, String fieldKey, String operator, String valueType,
            String expectedValue, boolean required, boolean active) {
        this.opportunity = opportunity;
        this.fieldKey = fieldKey;
        this.operator = operator;
        this.valueType = valueType;
        this.expectedValue = expectedValue;
        this.required = required;
        this.active = active;
    }

    public void update(String fieldKey, String operator, String valueType, String expectedValue,
            boolean required, boolean active) {
        this.fieldKey = fieldKey;
        this.operator = operator;
        this.valueType = valueType;
        this.expectedValue = expectedValue;
        this.required = required;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getOperator() {
        return operator;
    }

    public String getValueType() {
        return valueType;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isActive() {
        return active;
    }
}

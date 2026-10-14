package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.entity.*;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EligibilityService {
    private final EligibilityRuleRepository rules;
    private final OpportunityService opportunities;
    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final EligibilityCheckRepository checks;
    private final EligibilityCheckResultRepository results;
    private static final Set<String> FIELDS = Set.of("age", "education", "annual_income", "experience_years", "branch",
            "category");
    private static final Set<String> OPS = Set.of("=", "!=", ">", ">=", "<", "<=");

    public EligibilityService(EligibilityRuleRepository r, OpportunityService o, UserRepository u,
            UserProfileRepository p, EligibilityCheckRepository c, EligibilityCheckResultRepository cr) {
        rules = r;
        opportunities = o;
        users = u;
        profiles = p;
        checks = c;
        results = cr;
    }

    @Transactional
    public EligibilityResponse check(Long userId, Long opportunityId) {
        User user = users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        Opportunity opportunity = opportunities.find(opportunityId);
        UserProfile profile = profiles.findById(userId).orElse(null);
        List<RuleResult> out = new ArrayList<>();
        boolean failed = false, missing = false;
        List<EligibilityRule> active = rules.findByOpportunityIdAndActiveTrue(opportunityId);
        for (EligibilityRule rule : active) {
            validate(rule);
            String value = value(profile, rule.getFieldKey());
            String status, explanation;
            if (value == null) {
                status = "MISSING";
                explanation = "Provide " + rule.getFieldKey() + " in your profile to evaluate this requirement.";
                if (rule.isRequired())
                    missing = true;
            } else if (matches(value, rule.getExpectedValue(), rule.getOperator(), rule.getValueType())) {
                status = "SATISFIED";
                explanation = "Your value meets this requirement.";
            } else {
                status = "NOT_SATISFIED";
                explanation = "Your value does not meet this requirement.";
                if (rule.isRequired())
                    failed = true;
            }
            out.add(new RuleResult(rule.getId(), rule.getFieldKey(), rule.getOperator() + " " + rule.getExpectedValue(),
                    value, status, explanation));
        }
        String finalStatus = failed ? "NOT_CURRENTLY_ELIGIBLE" : missing ? "MISSING_INFORMATION" : "ELIGIBLE";
        EligibilityCheck check = checks.save(new EligibilityCheck(user, opportunity, finalStatus));
        for (int i = 0; i < active.size(); i++) {
            var r = out.get(i);
            results.save(new EligibilityCheckResult(check, r.ruleId(), r.criterion(), r.requirement(), r.userValue(),
                    r.status(), r.explanation()));
        }
        return new EligibilityResponse(opportunityId, opportunity.getTitle(), finalStatus, out);
    }

    private void validate(EligibilityRule r) {
        if (!FIELDS.contains(r.getFieldKey()) || !OPS.contains(r.getOperator()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported eligibility rule field or operator");
        String expected = (r.getFieldKey().equals("age") || r.getFieldKey().equals("experience_years")) ? "INTEGER"
                : r.getFieldKey().equals("annual_income") ? "DECIMAL" : "STRING";
        if (!expected.equals(r.getValueType()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid value_type for " + r.getFieldKey());
        if (expected.equals("STRING") && !Set.of("=", "!=").contains(r.getOperator()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid operator for STRING eligibility rule");
        try {
            if (expected.equals("INTEGER"))
                Integer.parseInt(r.getExpectedValue());
            if (expected.equals("DECIMAL"))
                new BigDecimal(r.getExpectedValue());
        } catch (NumberFormatException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid expected_value for " + r.getFieldKey());
        }
    }

    private String value(UserProfile p, String field) {
        if (p == null)
            return null;
        return switch (field) {
            case "age" -> p.getDateOfBirth() == null ? null
                    : String.valueOf(Period.between(p.getDateOfBirth(), LocalDate.now()).getYears());
            case "education" -> p.getEducation();
            case "annual_income" -> p.getAnnualIncome() == null ? null : p.getAnnualIncome().toPlainString();
            case "experience_years" -> p.getExperienceYears() == null ? null : p.getExperienceYears().toString();
            case "branch" -> p.getBranch();
            case "category" -> p.getCategory();
            default -> null;
        };
    }

    private boolean matches(String actual, String expected, String op, String type) {
        int compare;
        if (type.equals("INTEGER"))
            compare = Integer.compare(Integer.parseInt(actual), Integer.parseInt(expected));
        else if (type.equals("DECIMAL"))
            compare = new BigDecimal(actual).compareTo(new BigDecimal(expected));
        else
            compare = actual.equalsIgnoreCase(expected) ? 0 : 1;
        return switch (op) {
            case "=" -> compare == 0;
            case "!=" -> compare != 0;
            case ">" -> compare > 0;
            case ">=" -> compare >= 0;
            case "<" -> compare < 0;
            case "<=" -> compare <= 0;
            default -> false;
        };
    }
}

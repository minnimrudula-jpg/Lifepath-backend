package com.example.Lifepath.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_checks")
public class EligibilityCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;
    @Column(name = "opportunity_title_snapshot", nullable = false)
    private String opportunityTitleSnapshot;
    @Column(name = "final_status", nullable = false)
    private String finalStatus;
    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt = LocalDateTime.now();

    protected EligibilityCheck() {
    }

    public EligibilityCheck(User u, Opportunity o, String s) {
        user = u;
        opportunity = o;
        opportunityTitleSnapshot = o.getTitle();
        finalStatus = s;
    }

    public Long getId() {
        return id;
    }
}

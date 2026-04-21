package com.jobtrackr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JobApplication – Core domain entity.
 *
 * Demonstrates OOP principles:
 *   - Encapsulation  : private fields + Lombok getters/setters
 *   - Abstraction    : JPA hides SQL persistence details
 *   - Single Responsibility: entity only models the domain object
 */
@Entity
@Table(name = "job_applications", indexes = {
    @Index(name = "idx_status",   columnList = "status"),
    @Index(name = "idx_company",  columnList = "company"),
    @Index(name = "idx_priority", columnList = "priority")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core fields ──────────────────────────────────────────
    @NotBlank(message = "Company name is required")
    @Size(max = 120)
    @Column(nullable = false)
    private String company;

    @NotBlank(message = "Role is required")
    @Size(max = 200)
    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "date_applied")
    private LocalDate dateApplied;

    // ── Compensation & Location ───────────────────────────────
    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "salary_currency", length = 10)
    @Builder.Default
    private String salaryCurrency = "INR";

    @Size(max = 150)
    private String location;

    @Column(name = "remote_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RemoteType remoteType = RemoteType.ON_SITE;

    // ── Source & Links ────────────────────────────────────────
    @Size(max = 60)
    private String source;

    @Column(name = "jd_url", length = 500)
    private String jdUrl;

    // ── Tech Stack (stored as comma-separated; normalized in service) ──
    @Column(name = "tech_stack", columnDefinition = "TEXT")
    private String techStack;

    // ── Notes & Follow-up ─────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "next_step", length = 300)
    private String nextStep;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    // ── Recruiter info ────────────────────────────────────────
    @Column(name = "recruiter_name", length = 120)
    private String recruiterName;

    @Column(name = "recruiter_email", length = 200)
    private String recruiterEmail;

    // ── Audit ─────────────────────────────────────────────────
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Enums ─────────────────────────────────────────────────

    public enum ApplicationStatus {
        APPLIED, PHONE_SCREEN, TECHNICAL, ON_SITE, OFFER, REJECTED, WITHDRAWN, GHOSTED
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum RemoteType {
        REMOTE, HYBRID, ON_SITE
    }

    // ── Domain helpers ────────────────────────────────────────

    /** Returns tech stack as a List<String>. */
    public List<String> getTechList() {
        if (techStack == null || techStack.isBlank()) return new ArrayList<>();
        return List.of(techStack.split(","))
                   .stream()
                   .map(String::trim)
                   .filter(s -> !s.isEmpty())
                   .toList();
    }

    /** Returns true if the application is still active in the pipeline. */
    public boolean isActive() {
        return switch (status) {
            case APPLIED, PHONE_SCREEN, TECHNICAL, ON_SITE, OFFER -> true;
            default -> false;
        };
    }

    /** Formatted salary range string. */
    public String getSalaryRange() {
        if (salaryMin == null && salaryMax == null) return null;
        String symbol = "INR".equals(salaryCurrency) ? "₹" : salaryCurrency + " ";
        if (salaryMin != null && salaryMax != null)
            return symbol + salaryMin + " – " + symbol + salaryMax;
        if (salaryMin != null) return symbol + salaryMin + "+";
        return "Up to " + symbol + salaryMax;
    }
}

package com.jobtrackr.dto;

import com.jobtrackr.model.JobApplication.ApplicationStatus;
import com.jobtrackr.model.JobApplication.Priority;
import com.jobtrackr.model.JobApplication.RemoteType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO classes for the REST API layer.
 *
 * Separating DTOs from entities is an OOP best practice:
 *   - Keeps API contract independent of DB schema
 *   - Prevents over-posting / mass-assignment vulnerabilities
 *   - Enables API versioning without breaking the domain model
 */
public final class JobApplicationDTOs {

    private JobApplicationDTOs() {} // utility class — no instantiation

    // ─────────────────────────────────────────────────────────────
    // REQUEST DTO  (what the client sends)
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Company is required")
        @Size(max = 120)
        private String company;

        @NotBlank(message = "Role is required")
        @Size(max = 200)
        private String role;

        private ApplicationStatus status;
        private Priority priority;

        private LocalDate dateApplied;

        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal salaryMin;

        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal salaryMax;

        private String salaryCurrency;
        private String location;
        private RemoteType remoteType;
        private String source;

        @Size(max = 500)
        private String jdUrl;

        private List<String> tech;        // client sends a list; service joins to CSV
        private String notes;
        private String nextStep;
        private LocalDate followUpDate;
        private String recruiterName;

        @Email(message = "Invalid recruiter email")
        private String recruiterEmail;
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE DTO  (PATCH – all fields optional)
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String company;
        private String role;
        private ApplicationStatus status;
        private Priority priority;
        private LocalDate dateApplied;
        private BigDecimal salaryMin;
        private BigDecimal salaryMax;
        private String salaryCurrency;
        private String location;
        private RemoteType remoteType;
        private String source;
        private String jdUrl;
        private List<String> tech;
        private String notes;
        private String nextStep;
        private LocalDate followUpDate;
        private String recruiterName;
        private String recruiterEmail;
    }

    // ─────────────────────────────────────────────────────────────
    // RESPONSE DTO  (what the API returns)
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String company;
        private String role;
        private ApplicationStatus status;
        private Priority priority;
        private LocalDate dateApplied;
        private BigDecimal salaryMin;
        private BigDecimal salaryMax;
        private String salaryCurrency;
        private String salaryRange;       // formatted e.g. "₹12 – ₹18 LPA"
        private String location;
        private RemoteType remoteType;
        private String source;
        private String jdUrl;
        private List<String> tech;
        private String notes;
        private String nextStep;
        private LocalDate followUpDate;
        private String recruiterName;
        private String recruiterEmail;
        private boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // ─────────────────────────────────────────────────────────────
    // STATS DTO
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatsResponse {
        private long total;
        private long active;
        private long offers;
        private long rejected;
        private Double responseRate;
        private Map<String, Long> byStatus;
        private Map<String, Long> byPriority;
    }

    // ─────────────────────────────────────────────────────────────
    // PAGINATED RESPONSE WRAPPER
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    // ─────────────────────────────────────────────────────────────
    // API ERROR RESPONSE
    // ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> fieldErrors; // for validation errors
    }
}

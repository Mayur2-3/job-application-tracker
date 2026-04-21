package com.jobtrackr.service;

import com.jobtrackr.dto.JobApplicationDTOs.*;
import com.jobtrackr.exception.DuplicateApplicationException;
import com.jobtrackr.exception.ResourceNotFoundException;
import com.jobtrackr.model.JobApplication;
import com.jobtrackr.model.JobApplication.*;
import com.jobtrackr.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JobApplicationService – Business logic layer.
 *
 * OOP principles applied:
 *   - Single Responsibility: only handles business rules, delegates DB to repository
 *   - Dependency Injection via constructor (@RequiredArgsConstructor)
 *   - Encapsulation: private helper methods for mapping
 *   - Open/Closed: new filter types can be added without changing existing methods
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository repo;

    // ── CREATE ────────────────────────────────────────────────────────

    @Transactional
    public Response create(CreateRequest req) {
        log.debug("Creating application for {} – {}", req.getCompany(), req.getRole());

        if (repo.existsByCompanyIgnoreCaseAndRoleIgnoreCase(req.getCompany(), req.getRole())) {
            throw new DuplicateApplicationException(
                "Application for '" + req.getRole() + "' at '" + req.getCompany() + "' already exists."
            );
        }

        JobApplication app = JobApplication.builder()
            .company(req.getCompany().trim())
            .role(req.getRole().trim())
            .status(req.getStatus() != null ? req.getStatus() : ApplicationStatus.APPLIED)
            .priority(req.getPriority() != null ? req.getPriority() : Priority.MEDIUM)
            .dateApplied(req.getDateApplied() != null ? req.getDateApplied() : LocalDate.now())
            .salaryMin(req.getSalaryMin())
            .salaryMax(req.getSalaryMax())
            .salaryCurrency(req.getSalaryCurrency() != null ? req.getSalaryCurrency() : "INR")
            .location(req.getLocation())
            .remoteType(req.getRemoteType() != null ? req.getRemoteType() : RemoteType.ON_SITE)
            .source(req.getSource())
            .jdUrl(req.getJdUrl())
            .techStack(joinTech(req.getTech()))
            .notes(req.getNotes())
            .nextStep(req.getNextStep())
            .followUpDate(req.getFollowUpDate())
            .recruiterName(req.getRecruiterName())
            .recruiterEmail(req.getRecruiterEmail())
            .build();

        return toResponse(repo.save(app));
    }

    // ── READ ──────────────────────────────────────────────────────────

    public Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public PageResponse<Response> getAll(String q, ApplicationStatus status,
                                         Priority priority, int page, int size, String sort) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<JobApplication> result = repo.findWithFilters(
            (q != null && !q.isBlank()) ? q : null,
            status, priority, pageable
        );
        return toPageResponse(result);
    }

    public List<Response> search(String q) {
        return repo.search(q).stream().map(this::toResponse).toList();
    }

    public List<Response> getByStatus(ApplicationStatus status) {
        return repo.findByStatus(status).stream().map(this::toResponse).toList();
    }

    public List<Response> getDueFollowUps() {
        return repo.findDueFollowUps(LocalDate.now()).stream().map(this::toResponse).toList();
    }

    public List<Response> getByTech(String tech) {
        return repo.findByTech(tech).stream().map(this::toResponse).toList();
    }

    public List<Response> getByDateRange(LocalDate from, LocalDate to) {
        return repo.findByDateRange(from, to).stream().map(this::toResponse).toList();
    }

    // ── UPDATE ────────────────────────────────────────────────────────

    @Transactional
    public Response update(Long id, UpdateRequest req) {
        JobApplication app = findOrThrow(id);

        if (req.getCompany()        != null) app.setCompany(req.getCompany().trim());
        if (req.getRole()           != null) app.setRole(req.getRole().trim());
        if (req.getStatus()         != null) app.setStatus(req.getStatus());
        if (req.getPriority()       != null) app.setPriority(req.getPriority());
        if (req.getDateApplied()    != null) app.setDateApplied(req.getDateApplied());
        if (req.getSalaryMin()      != null) app.setSalaryMin(req.getSalaryMin());
        if (req.getSalaryMax()      != null) app.setSalaryMax(req.getSalaryMax());
        if (req.getSalaryCurrency() != null) app.setSalaryCurrency(req.getSalaryCurrency());
        if (req.getLocation()       != null) app.setLocation(req.getLocation());
        if (req.getRemoteType()     != null) app.setRemoteType(req.getRemoteType());
        if (req.getSource()         != null) app.setSource(req.getSource());
        if (req.getJdUrl()          != null) app.setJdUrl(req.getJdUrl());
        if (req.getTech()           != null) app.setTechStack(joinTech(req.getTech()));
        if (req.getNotes()          != null) app.setNotes(req.getNotes());
        if (req.getNextStep()       != null) app.setNextStep(req.getNextStep());
        if (req.getFollowUpDate()   != null) app.setFollowUpDate(req.getFollowUpDate());
        if (req.getRecruiterName()  != null) app.setRecruiterName(req.getRecruiterName());
        if (req.getRecruiterEmail() != null) app.setRecruiterEmail(req.getRecruiterEmail());

        return toResponse(repo.save(app));
    }

    // ── DELETE ────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        repo.deleteById(id);
        log.debug("Deleted application id={}", id);
    }

    // ── STATS ─────────────────────────────────────────────────────────

    public StatsResponse getStats() {
        long total     = repo.count();
        long active    = repo.countActive();
        long offers    = repo.countByStatus(ApplicationStatus.OFFER);
        long rejected  = repo.countByStatus(ApplicationStatus.REJECTED);
        Double rate    = repo.getResponseRate();

        // Build byStatus map from native query
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Object[] row : repo.countByStatusNative()) {
            byStatus.put(row[0].toString(), ((Number) row[1]).longValue());
        }

        // Build byPriority map
        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (Priority p : Priority.values()) {
            byPriority.put(p.name(), repo.countByPriority(p));
        }

        return StatsResponse.builder()
            .total(total).active(active).offers(offers)
            .rejected(rejected).responseRate(rate)
            .byStatus(byStatus).byPriority(byPriority)
            .build();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────

    private JobApplication findOrThrow(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found: id=" + id));
    }

    private String joinTech(List<String> tech) {
        if (tech == null || tech.isEmpty()) return null;
        return tech.stream().map(String::trim).filter(s -> !s.isEmpty())
                   .collect(Collectors.joining(", "));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "dateApplied");
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(dir, field);
    }

    /** Entity → Response DTO mapping (encapsulated in private method). */
    private Response toResponse(JobApplication a) {
        return Response.builder()
            .id(a.getId())
            .company(a.getCompany())
            .role(a.getRole())
            .status(a.getStatus())
            .priority(a.getPriority())
            .dateApplied(a.getDateApplied())
            .salaryMin(a.getSalaryMin())
            .salaryMax(a.getSalaryMax())
            .salaryCurrency(a.getSalaryCurrency())
            .salaryRange(a.getSalaryRange())
            .location(a.getLocation())
            .remoteType(a.getRemoteType())
            .source(a.getSource())
            .jdUrl(a.getJdUrl())
            .tech(a.getTechList())
            .notes(a.getNotes())
            .nextStep(a.getNextStep())
            .followUpDate(a.getFollowUpDate())
            .recruiterName(a.getRecruiterName())
            .recruiterEmail(a.getRecruiterEmail())
            .active(a.isActive())
            .createdAt(a.getCreatedAt())
            .updatedAt(a.getUpdatedAt())
            .build();
    }

    private PageResponse<Response> toPageResponse(Page<JobApplication> page) {
        return PageResponse.<Response>builder()
            .content(page.getContent().stream().map(this::toResponse).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}

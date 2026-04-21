package com.jobtrackr.controller;

import com.jobtrackr.dto.JobApplicationDTOs.*;
import com.jobtrackr.model.JobApplication.ApplicationStatus;
import com.jobtrackr.model.JobApplication.Priority;
import com.jobtrackr.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * JobApplicationController – REST API layer.
 *
 * Exposes CRUD + search + stats endpoints following REST conventions:
 *
 *   GET    /api/v1/applications              → list (paginated, filterable)
 *   GET    /api/v1/applications/{id}         → get by id
 *   POST   /api/v1/applications              → create
 *   PATCH  /api/v1/applications/{id}         → partial update
 *   DELETE /api/v1/applications/{id}         → delete
 *   GET    /api/v1/applications/search?q=    → search
 *   GET    /api/v1/applications/stats        → dashboard stats
 *   GET    /api/v1/applications/follow-ups   → due today
 *   GET    /api/v1/applications/by-tech?tech= → filter by tech
 *   GET    /api/v1/applications/by-date-range → date range filter
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class JobApplicationController {

    private final JobApplicationService service;

    // ─── CREATE ──────────────────────────────────────────────────────

    /**
     * POST /api/v1/applications
     * Body: CreateRequest JSON
     * Returns: 201 Created + Response body
     */
    @PostMapping
    public ResponseEntity<Response> create(@Valid @RequestBody CreateRequest req) {
        log.info("POST /applications — {}", req.getCompany());
        Response created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─── READ ─────────────────────────────────────────────────────────

    /**
     * GET /api/v1/applications?q=&status=&priority=&page=&size=&sort=
     */
    @GetMapping
    public ResponseEntity<PageResponse<Response>> getAll(
        @RequestParam(required = false)                  String q,
        @RequestParam(required = false)                  ApplicationStatus status,
        @RequestParam(required = false)                  Priority priority,
        @RequestParam(defaultValue = "0")                int page,
        @RequestParam(defaultValue = "20")               int size,
        @RequestParam(defaultValue = "dateApplied,desc") String sort
    ) {
        return ResponseEntity.ok(service.getAll(q, status, priority, page, size, sort));
    }

    /**
     * GET /api/v1/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * GET /api/v1/applications/search?q=java+backend
     */
    @GetMapping("/search")
    public ResponseEntity<List<Response>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.search(q));
    }

    /**
     * GET /api/v1/applications/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    /**
     * GET /api/v1/applications/follow-ups
     */
    @GetMapping("/follow-ups")
    public ResponseEntity<List<Response>> getDueFollowUps() {
        return ResponseEntity.ok(service.getDueFollowUps());
    }

    /**
     * GET /api/v1/applications/by-tech?tech=Java
     */
    @GetMapping("/by-tech")
    public ResponseEntity<List<Response>> getByTech(@RequestParam String tech) {
        return ResponseEntity.ok(service.getByTech(tech));
    }

    /**
     * GET /api/v1/applications/by-date-range?from=2025-10-01&to=2025-10-31
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Response>> getByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(service.getByDateRange(from, to));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────

    /**
     * PATCH /api/v1/applications/{id}
     * Partial update — only provided fields are changed.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Response> update(
        @PathVariable Long id,
        @RequestBody UpdateRequest req
    ) {
        log.info("PATCH /applications/{}", id);
        return ResponseEntity.ok(service.update(id, req));
    }

    // ─── DELETE ───────────────────────────────────────────────────────

    /**
     * DELETE /api/v1/applications/{id}
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /applications/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

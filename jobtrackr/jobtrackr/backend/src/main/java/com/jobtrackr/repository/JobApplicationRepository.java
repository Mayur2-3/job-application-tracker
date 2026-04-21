package com.jobtrackr.repository;

import com.jobtrackr.model.JobApplication;
import com.jobtrackr.model.JobApplication.ApplicationStatus;
import com.jobtrackr.model.JobApplication.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * JobApplicationRepository – Data access layer.
 *
 * Extends JpaRepository to inherit:
 *   save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * Custom JPQL + native SQL queries demonstrate SQL knowledge.
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // ── Basic finders ──────────────────────────────────────────────────

    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByPriority(Priority priority);

    List<JobApplication> findByStatusAndPriority(ApplicationStatus status, Priority priority);

    long countByStatus(ApplicationStatus status);

    long countByPriority(Priority priority);

    // ── Search (JPQL) ──────────────────────────────────────────────────

    /**
     * Full-text search across company, role, tech stack, and notes.
     * JPQL query — demonstrates ORM SQL abstraction.
     */
    @Query("""
        SELECT a FROM JobApplication a
        WHERE LOWER(a.company)   LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.role)      LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.techStack) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.notes)     LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY a.dateApplied DESC
    """)
    List<JobApplication> search(@Param("q") String query);

    /**
     * Paginated search with optional status + priority filters.
     */
    @Query("""
        SELECT a FROM JobApplication a
        WHERE (:q        IS NULL OR LOWER(a.company)   LIKE LOWER(CONCAT('%', :q, '%'))
                                 OR LOWER(a.role)      LIKE LOWER(CONCAT('%', :q, '%'))
                                 OR LOWER(a.techStack) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:status   IS NULL OR a.status   = :status)
          AND (:priority IS NULL OR a.priority = :priority)
    """)
    Page<JobApplication> findWithFilters(
        @Param("q")        String q,
        @Param("status")   ApplicationStatus status,
        @Param("priority") Priority priority,
        Pageable pageable
    );

    // ── Statistics (native SQL) ────────────────────────────────────────

    /** Count per status – used for dashboard stats. Native SQL example. */
    @Query(value = """
        SELECT status, COUNT(*) AS cnt
        FROM job_applications
        GROUP BY status
        ORDER BY cnt DESC
    """, nativeQuery = true)
    List<Object[]> countByStatusNative();

    /** Applications due for follow-up today or earlier. */
    @Query("SELECT a FROM JobApplication a WHERE a.followUpDate <= :today AND a.status NOT IN ('REJECTED','WITHDRAWN','GHOSTED')")
    List<JobApplication> findDueFollowUps(@Param("today") LocalDate today);

    /** Applications applied within a date range. */
    @Query("SELECT a FROM JobApplication a WHERE a.dateApplied BETWEEN :from AND :to ORDER BY a.dateApplied DESC")
    List<JobApplication> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /** Count active applications (in-pipeline). */
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.status IN ('APPLIED','PHONE_SCREEN','TECHNICAL','ON_SITE','OFFER')")
    long countActive();

    /** Response rate: non-ghosted + non-pending out of total. */
    @Query(value = """
        SELECT
            ROUND(100.0 * SUM(CASE WHEN status NOT IN ('APPLIED','GHOSTED') THEN 1 ELSE 0 END) / COUNT(*), 1)
        FROM job_applications
    """, nativeQuery = true)
    Double getResponseRate();

    /** Find applications by a specific tech (substring match). */
    @Query("SELECT a FROM JobApplication a WHERE LOWER(a.techStack) LIKE LOWER(CONCAT('%', :tech, '%'))")
    List<JobApplication> findByTech(@Param("tech") String tech);

    boolean existsByCompanyIgnoreCaseAndRoleIgnoreCase(String company, String role);
}

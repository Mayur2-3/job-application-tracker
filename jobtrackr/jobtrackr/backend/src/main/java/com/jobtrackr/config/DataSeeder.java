package com.jobtrackr.config;

import com.jobtrackr.model.JobApplication;
import com.jobtrackr.model.JobApplication.*;
import com.jobtrackr.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DataSeeder – Seeds sample job applications on startup (dev profile).
 * Run with: spring.profiles.active=dev (or just run normally since H2 is default)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final JobApplicationRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return; // don't seed if data exists
        log.info("Seeding sample job applications...");

        List<JobApplication> seed = List.of(
            build("Freshworks",   "Backend Engineer",          ApplicationStatus.OFFER,        Priority.HIGH,   "2025-09-20", 18, 24, "Java, Spring Boot, REST API, SQL, OOP",        "Offer received! Deadline Nov 5.", "Accept/decline by Nov 5"),
            build("Zoho Corp",    "Software Developer",        ApplicationStatus.ON_SITE,      Priority.HIGH,   "2025-09-25", 12, 18, "Java, JavaScript, OOP, Spring Boot",           "Referral via Akash. Great culture.", "On-site Oct 30 9am"),
            build("Infosys",      "Java Backend Developer",    ApplicationStatus.TECHNICAL,    Priority.HIGH,   "2025-10-02",  8, 12, "Java, Spring Boot, SQL, REST API, OOP",        "Round 2 scheduled.", "Technical interview Oct 28"),
            build("TCS Digital",  "Full Stack Engineer",       ApplicationStatus.PHONE_SCREEN, Priority.MEDIUM, "2025-10-05", 10, 15, "Java, JavaScript, SQL, REST API",              "HR round done. Waiting for L1.", "Follow up Nov 1"),
            build("EPAM Systems", "Java Consultant",           ApplicationStatus.PHONE_SCREEN, Priority.MEDIUM, "2025-10-07", 14, 20, "Java, Spring Boot, OOP, SQL",                  "International project exposure.", "L1 call Oct 29"),
            build("Wipro",        "Associate Engineer",        ApplicationStatus.APPLIED,      Priority.LOW,    "2025-10-08",  6,  9, "Java, OOP, SQL",                               "Online application submitted.", ""),
            build("Razorpay",     "Software Engineer",         ApplicationStatus.APPLIED,      Priority.HIGH,   "2025-10-10", 20, 28, "Java, JavaScript, REST API, Spring Boot",      "Dream company. Tailored resume.", ""),
            build("Persistent",   "Software Engineer",         ApplicationStatus.APPLIED,      Priority.MEDIUM, "2025-10-12",  9, 13, "Java, OOP, SQL, REST API",                     "Applied via LinkedIn Easy Apply.", ""),
            build("HCL Tech",     "Java Developer",            ApplicationStatus.REJECTED,     Priority.MEDIUM, "2025-10-01",  7, 10, "Java, SQL, OOP",                               "Rejected after L1 coding round.", ""),
            build("Mindtree",     "Associate Developer",       ApplicationStatus.GHOSTED,      Priority.LOW,    "2025-09-15",  5,  8, "Java, SQL",                                    "No response after 3 weeks.", "")
        );

        repo.saveAll(seed);
        log.info("Seeded {} applications.", seed.size());
    }

    private JobApplication build(String company, String role, ApplicationStatus status,
                                  Priority priority, String date, int salMin, int salMax,
                                  String tech, String notes, String next) {
        return JobApplication.builder()
            .company(company).role(role).status(status).priority(priority)
            .dateApplied(LocalDate.parse(date))
            .salaryMin(BigDecimal.valueOf(salMin))
            .salaryMax(BigDecimal.valueOf(salMax))
            .salaryCurrency("INR")
            .source("LinkedIn").location("India").remoteType(RemoteType.HYBRID)
            .techStack(tech).notes(notes).nextStep(next)
            .build();
    }
}

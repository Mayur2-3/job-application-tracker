package com.jobtrackr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JobTrackr – Job Application Tracker
 * Spring Boot REST API Entry Point
 *
 * Tech Stack: Java 17, Spring Boot 3, JPA, H2/PostgreSQL, REST API, OOP
 */
@SpringBootApplication
public class JobTrackrApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobTrackrApplication.class, args);
    }
}

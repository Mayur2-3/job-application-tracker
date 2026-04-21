# JobTrackr – Job Application Tracker

> Full-stack project: **Java 17 + Spring Boot 3** REST API backend · **React + TypeScript** frontend  
> Tech stack: Java · SQL · JavaScript/TypeScript · OOP · REST API · Spring Boot · JPA/Hibernate · H2/PostgreSQL

---

## Project Structure

```
jobtrackr/
├── backend/                          # Spring Boot REST API
│   ├── pom.xml
│   └── src/main/java/com/jobtrackr/
│       ├── JobTrackrApplication.java      ← Entry point
│       ├── model/
│       │   └── JobApplication.java        ← JPA Entity (OOP)
│       ├── repository/
│       │   └── JobApplicationRepository.java  ← JPA + custom SQL queries
│       ├── service/
│       │   └── JobApplicationService.java     ← Business logic
│       ├── controller/
│       │   └── JobApplicationController.java  ← REST endpoints
│       ├── dto/
│       │   └── JobApplicationDTOs.java        ← Request/Response DTOs
│       ├── exception/
│       │   ├── ResourceNotFoundException.java
│       │   ├── DuplicateApplicationException.java
│       │   └── GlobalExceptionHandler.java
│       └── config/
│           ├── CorsConfig.java
│           └── DataSeeder.java            ← Sample data on startup
│
└── frontend/                         # React + TypeScript (Vite)
    ├── package.json
    ├── vite.config.ts
    ├── index.html
    └── src/
        ├── main.tsx
        ├── App.tsx                        ← Root component
        ├── App.css                        ← Dark terminal theme
        ├── types/index.ts                 ← TypeScript types (mirror Java enums)
        ├── services/api.ts                ← Axios REST client
        ├── hooks/useApplications.ts       ← Custom React hooks
        └── components/
            ├── StatsBar.tsx
            ├── Toolbar.tsx
            ├── ApplicationTable.tsx       ← Sortable table view
            ├── KanbanBoard.tsx            ← Pipeline kanban view
            └── ApplicationModal.tsx       ← Add / Edit form
```

---

## How to Run

### Backend (Spring Boot)

**Prerequisites:** Java 17+, Maven 3.8+

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**  
H2 console: **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:jobtrackrdb`)  
Sample data is auto-seeded on first run.

---

### Frontend (React + Vite)

**Prerequisites:** Node.js 18+, npm

```bash
cd frontend
npm install
npm run dev
```

Opens at **http://localhost:3000**  
Vite proxies `/api/*` → `http://localhost:8080` automatically.

---

## REST API Endpoints

| Method   | Endpoint                                | Description                  |
|----------|-----------------------------------------|------------------------------|
| `GET`    | `/api/v1/applications`                  | List all (paginated, filtered)|
| `GET`    | `/api/v1/applications/{id}`             | Get by ID                    |
| `POST`   | `/api/v1/applications`                  | Create new application       |
| `PATCH`  | `/api/v1/applications/{id}`             | Partial update               |
| `DELETE` | `/api/v1/applications/{id}`             | Delete                       |
| `GET`    | `/api/v1/applications/search?q=`        | Full-text search             |
| `GET`    | `/api/v1/applications/stats`            | Dashboard statistics         |
| `GET`    | `/api/v1/applications/follow-ups`       | Due follow-ups today         |
| `GET`    | `/api/v1/applications/by-tech?tech=`    | Filter by tech stack         |
| `GET`    | `/api/v1/applications/by-date-range`    | Filter by date range         |

### Query Parameters for GET /applications

| Param      | Example              | Description              |
|------------|----------------------|--------------------------|
| `q`        | `?q=java`            | Search across all fields |
| `status`   | `?status=APPLIED`    | Filter by status         |
| `priority` | `?priority=HIGH`     | Filter by priority       |
| `page`     | `?page=0`            | Page number (0-based)    |
| `size`     | `?size=20`           | Page size                |
| `sort`     | `?sort=dateApplied,desc` | Sort field + direction|

### Sample Requests (curl)

```bash
# Create
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"company":"Google","role":"Backend Engineer","status":"APPLIED","priority":"HIGH","tech":["Java","Spring Boot","SQL"]}'

# Get all
curl http://localhost:8080/api/v1/applications

# Search
curl "http://localhost:8080/api/v1/applications/search?q=java"

# Stats
curl http://localhost:8080/api/v1/applications/stats

# Update status
curl -X PATCH http://localhost:8080/api/v1/applications/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"TECHNICAL"}'

# Delete
curl -X DELETE http://localhost:8080/api/v1/applications/1
```

---

## OOP Concepts Demonstrated

| Principle           | Where                                         |
|---------------------|-----------------------------------------------|
| **Encapsulation**   | `JobApplication` – private fields, Lombok accessors, domain helpers (`isActive()`, `getSalaryRange()`) |
| **Abstraction**     | `JobApplicationRepository` extends `JpaRepository` – hides SQL persistence |
| **Single Responsibility** | Controller → Service → Repository layers, each with one job |
| **Dependency Injection** | Constructor injection via `@RequiredArgsConstructor` |
| **Open/Closed**     | New filters added to `findWithFilters()` without breaking existing API |
| **Enum types**      | `ApplicationStatus`, `Priority`, `RemoteType` – type-safe state |

---

## SQL Concepts Demonstrated

| Concept                  | Where                                         |
|--------------------------|-----------------------------------------------|
| **DDL** (table creation) | JPA auto-generates from `@Entity` annotations |
| **DML** (CRUD)           | `JpaRepository` default methods               |
| **JPQL** queries         | `findWithFilters()`, `search()`, `findDueFollowUps()` |
| **Native SQL**           | `countByStatusNative()`, `getResponseRate()`  |
| **Indexes**              | `@Index` on `status`, `company`, `priority`   |
| **Aggregations**         | `COUNT`, `SUM`, `ROUND`, `GROUP BY`           |
| **Date queries**         | `BETWEEN`, `<=` for follow-up deadlines       |
| **Pagination**           | `Pageable` / `Page<T>` Spring Data             |

---

## Production Setup (PostgreSQL)

1. Create database: `CREATE DATABASE jobtrackrdb;`
2. Uncomment PostgreSQL block in `application.properties`
3. Comment out H2 block
4. Update username/password
5. Change `ddl-auto` to `update`

---

## Status Values

| Status        | Meaning                     |
|---------------|-----------------------------|
| `APPLIED`     | Application submitted       |
| `PHONE_SCREEN`| HR / recruiter call         |
| `TECHNICAL`   | Coding / technical round    |
| `ON_SITE`     | Final / on-site interview   |
| `OFFER`       | Offer received              |
| `REJECTED`    | Application rejected        |
| `WITHDRAWN`   | Candidate withdrew          |
| `GHOSTED`     | No response                 |

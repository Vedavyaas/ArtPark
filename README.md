# AI-Adaptive Onboarding Engine

## 1. Value Proposition & Problem Statement
Current corporate onboarding heavily utilizes static, "one-size-fits-all" curricula, resulting in significant inefficiencies. The **AI-Adaptive Onboarding Engine** tackles this challenge by intelligently parsing a new hire's current capabilities (via their Resume) against role expectations (via the target Job Description). It dynamically uncovers exact missing competencies and maps a completely optimized, customized training pathway to reach role-specific competency.

## 2. High-Level Logic: Skill-Gap Analysis & Adaptive Pathing
Our logic is executed via a synchronized, end-to-end processing pipeline:
1. **Ingestion & Parsing:** Candidate `Resume` and role `Job Description` (JD) PDFs are uploaded via the web interface. We use `Apache PDFBox` to strip raw text and isolate key delimiters targeting `SKILLS`, `EXPERIENCE`, and `REQUIREMENTS`.
2. **Gap Identification:** The filtered sub-sections are handed directly to our proprietary Python inference engine (`localhost:8000`). By cross-referencing extracted candidate skills against the firm's strict JD requirements, the model isolates the exact technical "delta" (or skill gap).
3. **Adaptive Pathing Generation:** Rather than a static list, the model computes an adaptive learning roadmap. It generates a phased, chronological CSV payload containing the Title, Description, and Estimated Duration for each required upskilling step, entirely skipping concepts the candidate has already mastered. 

## 3. Setup & Execution Instructions

### Prerequisites
* Java 17 or higher
* Maven 3.8+
* Python ML Service backend active on `http://localhost:8000/generate-roadmap`

### Running the Application
1. Clone this repository locally.
2. Ensure the companion Python ML backend is active and listening on port `8000`.
3. Build the backend using the included Maven wrapper:
   ```bash
   ./mvnw clean install
   ```
4. Start the Spring Boot Application:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Navigate to `http://localhost:8080/` in your browser.
6. Upload a sample Resume (PDF) and a standard Job Description (PDF). Click **Generate Roadmap** to kick off the skill-gap analysis process.

## 4. Dependencies & Tech Stack
* **Java 17 / Spring Boot 3.x** - Core routing and business logic.
* **Spring Web MVC** - Handling REST API endpoints and form ingestion.
* **Apache PDFBox 3.0.1** - Handling heavy-lifting PDF-to-Text extraction.
* **Vanilla JavaScript & CSS** - Frontend dynamically generating the animated, interactive roadmap UI.
* **H2 Database & Spring Data JPA** - Pre-configured ORM/DB scaffolds for extended features.

*(Note: Ensure proper dataset citations and metrics are outlined in the accompanying 5-slide Technical presentation!)*
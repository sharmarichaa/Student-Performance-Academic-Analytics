# PROJECT REPORT

## Student Performance & Academic Analytics System

**Course / Evaluation**: VITyarthi Core Java Project Submission  
**Technology Stack**: Core Java (JDK 22), Standard I/O, File System Persistence (CSV / TXT)  
**Architecture Pattern**: 4-Tier Layered Architecture (Model-Service-Repository-UI)

---

## 1. Abstract

The **Student Performance & Academic Analytics System** is a command-line software solution developed in Core Java to streamline student academic record keeping, automate grade and GPA calculations, track attendance and examination clearance eligibility, perform class-wide analytics, and produce text-based performance reports.

The application operates without external database or framework dependencies, utilizing CSV file handling for persistence and standard I/O for interactive console navigation. A diagnostic test suite (`TestRunner`) validates data transformations, grade point algorithms, attendance thresholds, and persistence mechanisms.

---

## 2. Problem Statement & Objectives

### Problem Statement
Educational institutions require lightweight, reliable tools to evaluate student performance, calculate credit-weighted grade points, verify exam eligibility rules based on attendance thresholds, and identify academically at-risk students for early intervention without complex software overhead.

### Project Objectives
1. Implement a modular **Student Management** module supporting CRUD operations and searching.
2. Automate **Academic Records** processing, mapping marks to the VIT 7-point grading scale (S, A, B, C, D, E, F) and computing credit-weighted CGPA.
3. Enforce **Attendance Rules**, evaluating the 75% attendance threshold for exam clearance.
4. Build an **Analytics Engine** capable of calculating class averages, generating top performer rank lists, computing grade distributions, and flagging academically at-risk students.
5. Provide a **Report Generation** engine exporting official transcripts and summaries to formatted text files.
6. Package comprehensive software documentation, system architecture, workflow, UML diagrams, and automated verification tests per VITyarthi evaluation guidelines.

---

## 3. Technology Stack & Software Design

- **Language**: Core Java (Java SE 22)
- **Data Persistence**: CSV Files (`data/students.csv`, `data/academic_records.csv`, `data/attendance.csv`)
- **Report Output**: Formatted ASCII Text Files (`reports/student_transcript_*.txt`, `reports/class_performance_report.txt`, `reports/at_risk_students_report.txt`)
- **Design Patterns**:
  - *Layered Architecture*: Decouples Data Domain, File Repository, Business Logic Services, and Console Presentation.
  - *Repository Pattern*: Abstracts file I/O operations into `FileRepository`.
  - *Defensive Programming*: `InputValidator` handles regex validation and prevents execution crashes.

---

## 4. Module Specifications

### Module 1: Student Management
Handles creation, retrieval, updates, and deletion of student profiles. Provides multi-attribute search across Student ID, Roll Number, and Name.

### Module 2: Academic Marks & Grade Management
Tracks subject marks for internal (50) and final exam (50) components. Calculates total marks, percentage, assigns letter grades, and computes credit-weighted Cumulative Grade Point Average (CGPA).

### Module 3: Attendance Management
Tracks total conducted classes and attended classes. Calculates percentage and evaluates clearance:
- **`ELIGIBLE`**: Attendance >= 80%
- **`ELIGIBLE (WARNING)`**: Attendance 75% to 79.99%
- **`DEBARRED`**: Attendance < 75%

### Module 4: Performance Analytics Engine
Generates aggregated statistical insights:
- Class-wide average CGPA, overall percentage, and attendance percentage.
- Top Performers Rank List sorted by CGPA.
- Visual Grade Distribution histogram representation.
- Academically At-Risk student detection based on low GPA (< 5.0), failing grades ('F'), or debarred attendance (< 75%).

### Module 5: Report Generator
Formats and writes text reports to disk:
- Individual Student Official Academic Transcript.
- Class Performance & Ranking Report.
- Academically At-Risk Alert Report.

---

## 5. Verification & Test Results

An automated diagnostic test suite (`com.vityarthi.analytics.test.TestRunner`) executes 26 test cases:

| Test Case Suite | Description | Status |
|---|---|---|
| **Test 1: Grade Calculation** | Verified S, A, B, C, D, E, F thresholds and grade points | **PASSED** |
| **Test 2: GPA Computation** | Verified credit-weighted CGPA calculations | **PASSED** |
| **Test 3: Attendance Eligibility** | Verified 75% and 80% clearance boundaries | **PASSED** |
| **Test 4: Student CRUD** | Verified add, update, search, delete in memory | **PASSED** |
| **Test 5: At-Risk Engine** | Verified classification of failing and debarred students | **PASSED** |
| **Test 6: File Persistence** | Verified CSV serialization and reloading | **PASSED** |
| **Test 7: Report Generation** | Verified text file generation in `./reports/` | **PASSED** |

**Final Verification Summary**: 26 / 26 Diagnostic Tests Passed (100% Success Rate).

---

## 6. Conclusion & Future Scope

### Conclusion
The **Student Performance & Academic Analytics System** delivers a modular Core Java solution for managing academic records, evaluating attendance eligibility, performing class analytics, and generating text reports. All VITyarthi evaluation requirements have been met.

### Future Scope
1. **GUI Integration**: Expand CLI interface into JavaFX or Swing desktop client.
2. **Database Support**: Transition CSV persistence to JDBC relational database storage (MySQL/PostgreSQL).
3. **PDF Export**: Extend text report generation to formatted PDF files using iText library.

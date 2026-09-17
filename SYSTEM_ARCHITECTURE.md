# System Architecture & Technical Design

This document details the architectural principles, system design, data flow, and class responsibilities for the **Student Performance & Academic Analytics System**.

---

## 1. High-Level Architectural Overview

The application follows a 4-Tier Modular Layered Architecture:

```
+-----------------------------------------------------------------------+
|                        PRESENTATION LAYER                             |
|       - Main.java (Interactive Navigation Console)                    |
|       - UIConsole.java (ASCII Banners & Tables)                       |
|       - InputValidator.java (CLI Boundaries & Type Safety)            |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                           SERVICE LAYER                               |
|       - StudentService (CRUD & Multi-Field Search)                    |
|       - AcademicService (Marks, Grades & GPA Calculation)              |
|       - AttendanceService (Attendance % & Eligibility Checks)         |
|       - AnalyticsService (Class Averages, Rankings & At-Risk Engine)  |
|       - ReportService (TXT Export Engine)                             |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                          REPOSITORY LAYER                             |
|       - FileRepository (CSV Serialization / Deserialization)          |
+-----------------------------------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------+
|                           DOMAIN MODEL                                |
|       - Student, Course, AcademicRecord, AttendanceRecord             |
+-----------------------------------------------------------------------+
```

---

## 2. Core Modules & Responsibilities

### Presentation Layer
- **`Main`**: Entry point orchestrating system flow, hosting sub-menus for Student Management, Marks Entry, Attendance Management, Analytics, and Report Generation.
- **`UIConsole`**: Formats output tables, header banners, dividers, and colored badge indicators (`[SUCCESS]`, `[ERROR]`, `[WARNING]`).
- **`InputValidator`**: Prevents scanner runtime exceptions by parsing inputs through boundary validation loops and email regex matching.

### Service Layer (Business Logic)
- **`StudentService`**: Manages in-memory maps of student entities, performing lookup, filtering, addition, updates, and deletion.
- **`AcademicService`**: Computes course total marks, percentage, VIT 7-point grade assignment, grade points, and credit-weighted Cumulative Grade Point Average (CGPA).
- **`AttendanceService`**: Computes course-level and overall attendance percentages and evaluates examination eligibility thresholds.
- **`AnalyticsService`**: Processes aggregated student performance dataset to compute class averages, top performer ranks, grade distributions, and identifies at-risk students.
- **`ReportService`**: Formats individual student transcripts, class performance summaries, and at-risk alert notifications into formatted `.txt` files.

### Repository Layer (Persistence)
- **`FileRepository`**: Handles reading from and writing to CSV files (`students.csv`, `academic_records.csv`, `attendance.csv`). Seeds sample records automatically if directory files are absent.

### Domain Model Layer
- **`Student`**: Represents student demographic data (`studentId`, `name`, `rollNo`, `department`, `semester`, `email`).
- **`Course`**: Represents course details (`courseCode`, `title`, `credits`, `maxMarks`).
- **`AcademicRecord`**: Stores subject marks (`internalMarks`, `examMarks`) and calculates total, percentage, grade, grade point, and pass/fail status.
- **`AttendanceRecord`**: Tracks class attendance (`totalClasses`, `attendedClasses`) and determines exam clearance.

---

## 3. Data Flow Architecture

### Marks Entry & Calculation Flow
```
[User Input] --> (InputValidator) --> [Main.java] --> (AcademicService) 
                                                             |
                                                             +--> Computes Total, % & Grade
                                                             +--> Updates Student Record
                                                             +--> (FileRepository) --> [academic_records.csv]
```

### Analytics & At-Risk Detection Flow
```
[Analytics Engine] <-- Aggregates Data -- (StudentService + AcademicService + AttendanceService)
        |
        +--> Filters (GPA < 5.0 OR Failed Subjects > 0 OR Attendance < 75%)
        +--> Constructs StudentPerformanceSummary
        +--> Outputs Rank List / Visual Grade Distribution / At-Risk Alerts
```

---

## 4. Business Logic Algorithms

### GPA / CGPA Calculation Formula
\[
\text{CGPA} = \frac{\sum_{i=1}^{n} (\text{Grade Point}_i \times \text{Credits}_i)}{\sum_{i=1}^{n} \text{Credits}_i}
\]

Where Grade Points are assigned based on the VIT 7-point scale:
- \( \text{Percentage} \ge 90\% \rightarrow \text{Grade S (10.0 points)} \)
- \( 80\% \le \text{Percentage} < 90\% \rightarrow \text{Grade A (9.0 points)} \)
- \( 70\% \le \text{Percentage} < 80\% \rightarrow \text{Grade B (8.0 points)} \)
- \( 60\% \le \text{Percentage} < 70\% \rightarrow \text{Grade C (7.0 points)} \)
- \( 50\% \le \text{Percentage} < 60\% \rightarrow \text{Grade D (6.0 points)} \)
- \( 40\% \le \text{Percentage} < 50\% \rightarrow \text{Grade E (5.0 points)} \)
- \( \text{Percentage} < 40\% \rightarrow \text{Grade F (0.0 points, Fail)} \)

### Exam Clearance Threshold Rules
\[
\text{Status} = \begin{cases} 
\text{ELIGIBLE} & \text{if } \text{Attendance \%} \ge 80\% \\
\text{ELIGIBLE (WARNING)} & \text{if } 75\% \le \text{Attendance \%} < 80\% \\
\text{DEBARRED} & \text{if } \text{Attendance \%} < 75\%
\end{cases}
\]

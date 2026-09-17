# Student Performance & Academic Analytics System

A command-line based Java application built with Core Java for managing student academic records, calculating grade point averages (GPA/CGPA), tracking attendance and exam eligibility, performing class analytics, and generating official text file reports.

Developed according to the **VITyarthi Evaluation Guidelines**.

---

## Key Modules & Features

1. **Student Management Module**
   - Add, update, delete, and search student records by ID, Roll Number, or Name.
   - Comprehensive student profile fields: ID, Name, Roll No, Department, Semester, and Email.

2. **Academic Records Module**
   - Course catalog management with credit allocations.
   - Internal (50) and Exam (50) marks entry with automatic total, percentage, grade, grade point, and pass/fail calculations.
   - Standard 7-point grading scale (S: >=90%, A: 80-89%, B: 70-79%, C: 60-69%, D: 50-59%, E: 40-49%, F: <40%).
   - Weighted Cumulative Grade Point Average (CGPA) computation.

3. **Attendance Management Module**
   - Total conducted classes and attended classes tracking per course.
   - Attendance percentage calculator.
   - Exam eligibility decision rules (`ELIGIBLE`: >=80%, `ELIGIBLE (WARNING)`: 75-79%, `DEBARRED`: <75%).

4. **Performance Analytics & Insights Engine**
   - Class-wide averages (GPA, Marks %, Attendance %).
   - Top Performers Rank List (sortable by CGPA).
   - Grade Distribution analysis with visual ASCII distribution bars.
   - Academically At-Risk student detection (GPA < 5.0, failed subjects, or debarred attendance).

5. **Report Generation Module**
   - Official Individual Student Transcripts (`reports/student_transcript_<ID>.txt`).
   - Class Performance Summary Reports (`reports/class_performance_report.txt`).
   - Academically At-Risk Alert Reports (`reports/at_risk_students_report.txt`).

6. **Data Persistence**
   - File-based persistence layer saving records as lightweight CSV files (`data/students.csv`, `data/academic_records.csv`, `data/attendance.csv`).
   - Auto-population of sample datasets for immediate demonstration upon first startup.

---

## Directory Structure

```
java vityarthi/
│
├── src/
│   └── com/
│       └── vityarthi/
│           └── analytics/
│               ├── Main.java                       
│               ├── model/                           
│               │   ├── Student.java
│               │   ├── Course.java
│               │   ├── AcademicRecord.java
│               │   └── AttendanceRecord.java
│               ├── service/                         
│               │   ├── StudentService.java
│               │   ├── AcademicService.java
│               │   ├── AttendanceService.java
│               │   ├── AnalyticsService.java
│               │   └── ReportService.java
│               ├── repository/                      
│               │   └── FileRepository.java
│               ├── util/                            
│               │   ├── InputValidator.java
│               │   └── UIConsole.java
│               └── test/                            
│                   └── TestRunner.java
│
├── data/                                            
├── reports/                                         
├── bin/                                             
│
├── README.md                                     
├── SYSTEM_ARCHITECTURE.md                        
├── WORKFLOW.md                                   
├── UML_DIAGRAMS.md                                 
└── PROJECT_REPORT.md                                
```

---

## Requirements

- **Java Development Kit (JDK)**: JDK 17 or higher (tested on OpenJDK 22).
- **Operating System**: Windows / Linux / macOS.
- **Dependencies**: None (100% Core Java Standard Library).

---

## Quick Start Guide

### 1. Compile the Project
Open a command terminal (PowerShell / Command Prompt / Terminal) in the project root directory and execute:

```powershell
# Create build directory
mkdir bin

# Compile all source files
javac -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
```

### 2. Run Diagnostic Test Suite
Verify mathematical logic, persistence, and report generation:

```powershell
java -cp bin com.vityarthi.analytics.test.TestRunner
```

### 3. Launch Interactive Console Application
Start the main application menu:

```powershell
java -cp bin com.vityarthi.analytics.Main
```

---

## Design Principles & Patterns Used

- **Separation of Concerns (SoC)**: Strict decoupling between Data Models, Business Services, Persistence, and Presentation Layers.
- **Repository Pattern**: Data persistence abstracted into `FileRepository` class for clean file operations.
- **Service Layer Pattern**: Business calculations (GPA, attendance percentage, analytics) isolated in dedicated service classes.
- **Defensive Programming**: Robust regex and type checking in `InputValidator` preventing invalid entries and terminal crashes.

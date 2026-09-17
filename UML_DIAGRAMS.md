# System UML Diagrams

This document contains full UML specifications (Class Diagram, Sequence Diagram, Component Diagram, and Use Case Diagram) for the **Student Performance & Academic Analytics System**.

---

## 1. Class Diagram (UML)

```mermaid
classDiagram
    class Student {
        -String studentId
        -String name
        -String rollNo
        -String department
        -int semester
        -String email
        +getStudentId() String
        +toCsv() String
        +fromCsv(String) Student
    }

    class Course {
        -String courseCode
        -String title
        -int credits
        -double maxMarks
        +getCourseCode() String
        +getCredits() int
    }

    class AcademicRecord {
        -String studentId
        -String courseCode
        -double internalMarks
        -double examMarks
        -double maxInternal
        -double maxExam
        +getTotalMarks() double
        +getPercentage() double
        +getGrade() String
        +getGradePoint() double
        +isPassed() boolean
    }

    class AttendanceRecord {
        -String studentId
        -String courseCode
        -int totalClasses
        -int attendedClasses
        +getAttendancePercentage() double
        +getEligibilityStatus() String
        +isEligibleForExam() boolean
    }

    class FileRepository {
        -String dataDirPath
        +loadStudents() List~Student~
        +saveStudents(List~Student~)
        +loadAcademicRecords() List~AcademicRecord~
        +saveAcademicRecords(List~AcademicRecord~)
        +loadAttendanceRecords() List~AttendanceRecord~
        +saveAttendanceRecords(List~AttendanceRecord~)
    }

    class StudentService {
        -Map~String, Student~ studentMap
        +addStudent(Student) boolean
        +updateStudent(Student) boolean
        +deleteStudent(String) boolean
        +getStudentById(String) Student
        +searchStudentsByName(String) List~Student~
    }

    class AcademicService {
        -List~AcademicRecord~ academicRecords
        -Map~String, Course~ courseCatalog
        +addOrUpdateMarks(AcademicRecord)
        +calculateGPA(String) double
        +calculateOverallPercentage(String) double
    }

    class AttendanceService {
        -List~AttendanceRecord~ attendanceRecords
        +addOrUpdateAttendance(AttendanceRecord)
        +getOverallAttendancePercentage(String) double
    }

    class AnalyticsService {
        +getAllStudentSummaries() List~StudentPerformanceSummary~
        +getTopPerformers(int) List~StudentPerformanceSummary~
        +getGradeDistribution() Map~String, Integer~
        +getAtRiskStudents() List~StudentPerformanceSummary~
    }

    class ReportService {
        +generateStudentTranscriptReport(String) String
        +generateClassPerformanceReport() String
        +generateAtRiskReport() String
    }

    StudentService --> FileRepository
    AcademicService --> FileRepository
    AttendanceService --> FileRepository
    AnalyticsService --> StudentService
    AnalyticsService --> AcademicService
    AnalyticsService --> AttendanceService
    ReportService --> StudentService
    ReportService --> AcademicService
    ReportService --> AttendanceService
    ReportService --> AnalyticsService
    AcademicRecord "many" -- "1" Student
    AcademicRecord "many" -- "1" Course
    AttendanceRecord "many" -- "1" Student
```

---

## 2. Sequence Diagram (Marks Recording & GPA Calculation)

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Faculty / Admin
    participant Main as Main CLI
    participant Service as AcademicService
    participant Model as AcademicRecord
    participant Repo as FileRepository

    Admin->>Main: Enter Marks (StudentId, Course, Internal, Exam)
    Main->>Service: addOrUpdateMarks(AcademicRecord)
    Service->>Model: Instantiate & Compute Total, Grade, Point
    Model-->>Service: Return Grade ('S') & Point (10.0)
    Service->>Repo: saveAcademicRecords(records)
    Repo-->>Service: Data Saved to CSV
    Service-->>Main: Return Success Feedback
    Main-->>Admin: Display Recorded Grade & Pass/Fail Status
```

---

## 3. Component Diagram

```mermaid
graph TD
    UI[Console User Interface] --> CLI[Main Navigation Controller]
    CLI --> SM[Student Management Module]
    CLI --> AM[Academic Records Module]
    CLI --> AT[Attendance Management Module]
    CLI --> AN[Analytics Engine]
    CLI --> RP[Report Generator]

    AN --> SM
    AN --> AM
    AN --> AT
    RP --> AN

    SM --> REPO[File Repository Layer]
    AM --> REPO
    AT --> REPO

    REPO --> FS[(CSV Data Files)]
    RP --> OS[(TXT Report Files)]
```

---

## 4. Use Case Diagram

```mermaid
graph LR
    Faculty((Faculty / Admin))

    Faculty --> UC1(Manage Students)
    Faculty --> UC2(Record Subject Marks)
    Faculty --> UC3(Record Attendance)
    Faculty --> UC4(View Performance Analytics)
    Faculty --> UC5(Identify At-Risk Students)
    Faculty --> UC6(Export Text Reports)
    Faculty --> UC7(Run System Diagnostics)
```

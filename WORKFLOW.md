# System Operational Workflow & User Walkthrough Guide

This document presents a step-by-step operational walkthrough of the **Student Performance & Academic Analytics System**.

---

## Workflow Step 1: System Initialization & Seed Loading

Upon launching the application (`java -cp bin com.vityarthi.analytics.Main`), the system verifies the presence of the `data/` directory.

- If data files (`students.csv`, `academic_records.csv`, `attendance.csv`) do not exist, `FileRepository` automatically seeds initial demo data containing 5 students, course marks, and attendance records.
- The Main Banner and Navigation Menu are displayed:

```
==========================================================================================
     __   _____ _____                       _   _                                         
     \ \ / /_  |_   _|   _  __ _ _ __| |_| |__ (_)                                
      \ V /  | | | |  | | | |/ _` | '__| __| '_ \| |                                
       | |  _| |_| |  | |_| | (_| | |  | |_| | | | |                                
       |_| |_____|_|   \__, |\__,_|_|   \__|_| |_|_|                                
                       |___/                                                        
               STUDENT PERFORMANCE & ACADEMIC ANALYTICS SYSTEM                             
==========================================================================================

==========================================================================================
                                MAIN SYSTEM NAVIGATION MENU
==========================================================================================
  1. Student Management (Add, Update, Delete, Search)
  2. Academic Marks & Grade Management
  3. Attendance & Examination Eligibility
  4. Performance Analytics & Insights Engine
  5. Report Generation & Text File Export
  6. Run Automated System Test Suite & Diagnostics
  7. Save & Exit Application
------------------------------------------------------------------------------------------
Select menu option [1-7]:
```

---

## Workflow Step 2: Student Management Operations

Navigate to Option `1` to open the Student Management sub-menu:

```
==========================================================================================
                               REGISTERED STUDENTS DIRECTORY
==========================================================================================
ID         | Name                 | Roll No      | Dept     | Sem  | Email                         
------------------------------------------------------------------------------------------
STU101     | Aarav Sharma         | 21BCE1001    | CSE      | 5    | aarav.sharma@vityarthi.ac.in  
STU102     | Ananya Verma         | 21BCE1002    | CSE      | 5    | ananya.verma@vityarthi.ac.in  
STU103     | Rohan Gupta          | 21ECE1003    | ECE      | 5    | rohan.gupta@vityarthi.ac.in   
STU104     | Priya Nair           | 21MECH1004   | MECH     | 5    | priya.nair@vityarthi.ac.in    
STU105     | Vikram Singh         | 21BCE1005    | CSE      | 5    | vikram.singh@vityarthi.ac.in  
------------------------------------------------------------------------------------------
```

### Adding a New Student
- Select Option `2`.
- Input student metadata (ID, Name, Roll No, Department, Semester, Email).
- `InputValidator` checks email regex and prevents duplicate Student IDs.

---

## Workflow Step 3: Academic Marks & Grade Management

Navigate to Option `2` from the main menu:

- Enter internal marks (out of 50) and exam marks (out of 50) for a course (e.g. `CSE3001`).
- The system computes:
  - Total Marks = `48.00 + 47.00 = 95.00 / 100.00`
  - Percentage = `95.00%`
  - Assigned Grade = `S` (10.0 points)
  - Pass Status = `PASS`

---

## Workflow Step 4: Attendance & Exam Clearance Tracking

Navigate to Option `3` from the main menu:

- Record conducted and attended classes per course.
- View individual student summary:

```
==========================================================================================
                       ATTENDANCE & ELIGIBILITY REPORT: Aarav Sharma (STU101)
==========================================================================================
Course     | Total Classes   | Attended        | Attendance % | Exam Eligibility    
------------------------------------------------------------------------------------------
CSE3001    | 40              | 38              | 95.00        | ELIGIBLE            
CSE3002    | 40              | 37              | 92.50        | ELIGIBLE            
MAT2001    | 40              | 39              | 97.50        | ELIGIBLE            
------------------------------------------------------------------------------------------
  Overall Attendance Percentage: 95.00%
  Overall Examination Status: ELIGIBLE
------------------------------------------------------------------------------------------
```

---

## Workflow Step 5: Performance Analytics & At-Risk Detection

Navigate to Option `4` from the main menu:

### Top Performers Rank List
```
==========================================================================================
                       TOP 3 ACADEMIC PERFORMERS (RANK LIST)
==========================================================================================
Rank   | ID         | Name                 | Dept       | CGPA     | Avg %   
------------------------------------------------------------------------------------------
#1     | STU101     | Aarav Sharma         | CSE        | 9.64     | 92.00   
#2     | STU102     | Ananya Verma         | CSE        | 8.64     | 79.67   
#3     | STU103     | Rohan Gupta          | ECE        | 6.60     | 59.33   
------------------------------------------------------------------------------------------
```

### Grade Distribution Visual Analysis
```
==========================================================================================
                          ACADEMIC GRADE DISTRIBUTION ANALYSIS
==========================================================================================
Grade    | Count      | Percentage   | Visual Bar                    
------------------------------------------------------------------------------------------
S        | 2          | 15.38%       | ██████                        
A        | 3          | 23.08%       | █████████                     
B        | 1          | 7.69%        | ███                           
C        | 1          | 7.69%        | ███                           
D        | 2          | 15.38%       | ██████                        
E        | 1          | 7.69%        | ███                           
F        | 3          | 23.08%       | █████████                     
------------------------------------------------------------------------------------------
```

### Academically At-Risk Detection
```
==========================================================================================
                        ACADEMICALLY AT-RISK STUDENTS DETECTION
==========================================================================================
ID         | Name                 | GPA    | Att %    | Risk Factors / Reason(s)           
------------------------------------------------------------------------------------------
STU104     | Priya Nair           | 2.50   | 62.50    | Failed in 1 subject(s); Low Attendance (62.50% < 75%)
STU105     | Vikram Singh         | 0.00   | 71.25    | Low GPA (0.00 < 5.0); Failed in 2 subject(s); Low Attendance (71.25% < 75%)
------------------------------------------------------------------------------------------
```

---

## Workflow Step 6: Text File Report Generation

Navigate to Option `5` to export official text reports into the `./reports/` directory:

1. `reports/student_transcript_STU101.txt`
2. `reports/class_performance_report.txt`
3. `reports/at_risk_students_report.txt`

All records are automatically saved to CSV storage upon exiting the application (`Option 7`).

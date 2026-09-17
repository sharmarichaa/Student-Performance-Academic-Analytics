package com.vityarthi.analytics;

import com.vityarthi.analytics.model.*;
import com.vityarthi.analytics.repository.FileRepository;
import com.vityarthi.analytics.service.*;
import com.vityarthi.analytics.test.TestRunner;
import com.vityarthi.analytics.util.InputValidator;
import com.vityarthi.analytics.util.UIConsole;

import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for Student Performance & Academic Analytics System.
 */
public class Main {

    private final StudentService studentService;
    private final AcademicService academicService;
    private final AttendanceService attendanceService;
    private final AnalyticsService analyticsService;
    private final ReportService reportService;
    private final Scanner scanner;

    public Main() {
        FileRepository repository = new FileRepository("data");
        repository.seedInitialDataIfEmpty();

        this.studentService = new StudentService(repository);
        this.academicService = new AcademicService(repository);
        this.attendanceService = new AttendanceService(repository);
        this.analyticsService = new AnalyticsService(studentService, academicService, attendanceService);
        this.reportService = new ReportService("reports", studentService, academicService, attendanceService, analyticsService);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        UIConsole.printBanner();
        boolean running = true;

        while (running) {
            UIConsole.printHeader("MAIN SYSTEM NAVIGATION MENU");
            System.out.println("  1. Student Management (Add, Update, Delete, Search)");
            System.out.println("  2. Academic Marks & Grade Management");
            System.out.println("  3. Attendance & Examination Eligibility");
            System.out.println("  4. Performance Analytics & Insights Engine");
            System.out.println("  5. Report Generation & Text File Export");
            System.out.println("  6. Run Automated System Test Suite & Diagnostics");
            System.out.println("  7. Save & Exit Application");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select menu option [1-7]: ", 1, 7);
            switch (choice) {
                case 1:
                    handleStudentManagementMenu();
                    break;
                case 2:
                    handleAcademicMarksMenu();
                    break;
                case 3:
                    handleAttendanceMenu();
                    break;
                case 4:
                    handleAnalyticsMenu();
                    break;
                case 5:
                    handleReportMenu();
                    break;
                case 6:
                    runDiagnostics();
                    break;
                case 7:
                    saveAllData();
                    UIConsole.printSuccess("All data saved to CSV disk storage. Thank you for using VITyarthi Academic System!");
                    running = false;
                    break;
            }
        }
    }

    // ==========================================
    // MODULE 1: STUDENT MANAGEMENT
    // ==========================================
    private void handleStudentManagementMenu() {
        boolean back = false;
        while (!back) {
            UIConsole.printHeader("STUDENT MANAGEMENT MODULE");
            System.out.println("  1. View All Students");
            System.out.println("  2. Add New Student Record");
            System.out.println("  3. Update Existing Student");
            System.out.println("  4. Delete Student Record");
            System.out.println("  5. Search Student (by ID, Roll No, or Name)");
            System.out.println("  6. Back to Main Menu");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select option [1-6]: ", 1, 6);
            switch (choice) {
                case 1:
                    studentService.displayAllStudents();
                    break;
                case 2:
                    addNewStudent();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    deleteStudent();
                    break;
                case 5:
                    searchStudent();
                    break;
                case 6:
                    back = true;
                    break;
            }
        }
    }

    private void addNewStudent() {
        UIConsole.printSubHeader("Add New Student");
        String id = InputValidator.readNonEmptyString(scanner, "Enter Student ID (e.g. STU106): ");
        if (studentService.getStudentById(id) != null) {
            UIConsole.printError("Student with ID '" + id + "' already exists!");
            return;
        }

        String name = InputValidator.readNonEmptyString(scanner, "Enter Full Name: ");
        String roll = InputValidator.readNonEmptyString(scanner, "Enter Roll Number (e.g. 21BCE1006): ");
        String dept = InputValidator.readNonEmptyString(scanner, "Enter Department/Branch (e.g. CSE): ");
        int sem = InputValidator.readInt(scanner, "Enter Semester [1-10]: ", 1, 10);
        String email = InputValidator.readEmail(scanner, "Enter Email Address: ");

        Student s = new Student(id, name, roll, dept, sem, email);
        if (studentService.addStudent(s)) {
            UIConsole.printSuccess("Student record added successfully!");
        } else {
            UIConsole.printError("Failed to add student record.");
        }
    }

    private void updateStudent() {
        UIConsole.printSubHeader("Update Student Record");
        String id = InputValidator.readNonEmptyString(scanner, "Enter Student ID to update: ");
        Student existing = studentService.getStudentById(id);
        if (existing == null) {
            UIConsole.printError("Student not found with ID: " + id);
            return;
        }

        System.out.println("Current Info: " + existing);
        String name = InputValidator.readNonEmptyString(scanner, "Enter New Name [" + existing.getName() + "]: ");
        String roll = InputValidator.readNonEmptyString(scanner, "Enter New Roll No [" + existing.getRollNo() + "]: ");
        String dept = InputValidator.readNonEmptyString(scanner, "Enter New Dept [" + existing.getDepartment() + "]: ");
        int sem = InputValidator.readInt(scanner, "Enter New Semester [1-10]: ", 1, 10);
        String email = InputValidator.readEmail(scanner, "Enter New Email [" + existing.getEmail() + "]: ");

        Student updated = new Student(id, name, roll, dept, sem, email);
        if (studentService.updateStudent(updated)) {
            UIConsole.printSuccess("Student updated successfully!");
        } else {
            UIConsole.printError("Update failed.");
        }
    }

    private void deleteStudent() {
        UIConsole.printSubHeader("Delete Student Record");
        String id = InputValidator.readNonEmptyString(scanner, "Enter Student ID to delete: ");
        Student s = studentService.getStudentById(id);
        if (s == null) {
            UIConsole.printError("Student not found.");
            return;
        }

        if (InputValidator.readConfirmation(scanner, "Are you sure you want to delete student " + s.getName())) {
            if (studentService.deleteStudent(id)) {
                UIConsole.printSuccess("Student record removed.");
            } else {
                UIConsole.printError("Delete failed.");
            }
        }
    }

    private void searchStudent() {
        UIConsole.printSubHeader("Search Student");
        System.out.println("  1. Search by Student ID");
        System.out.println("  2. Search by Roll Number");
        System.out.println("  3. Search by Name (Partial Match)");
        int choice = InputValidator.readInt(scanner, "Choice [1-3]: ", 1, 3);

        if (choice == 1) {
            String id = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
            Student s = studentService.getStudentById(id);
            if (s != null) {
                System.out.println("\n" + s);
                academicService.displayStudentAcademicSummary(s);
            } else UIConsole.printError("No student found with ID: " + id);
        } else if (choice == 2) {
            String roll = InputValidator.readNonEmptyString(scanner, "Enter Roll No: ");
            Student s = studentService.getStudentByRollNo(roll);
            if (s != null) {
                System.out.println("\n" + s);
                academicService.displayStudentAcademicSummary(s);
            } else UIConsole.printError("No student found with Roll No: " + roll);
        } else if (choice == 3) {
            String name = InputValidator.readNonEmptyString(scanner, "Enter Name Search Term: ");
            List<Student> results = studentService.searchStudentsByName(name);
            if (results.isEmpty()) {
                UIConsole.printWarning("No matching students found.");
            } else {
                System.out.println("\nMatching Students (" + results.size() + "):");
                for (Student s : results) {
                    System.out.println("  - " + s);
                }
            }
        }
    }

    // ==========================================
    // MODULE 2: ACADEMIC MARKS & GRADES
    // ==========================================
    private void handleAcademicMarksMenu() {
        boolean back = false;
        while (!back) {
            UIConsole.printHeader("ACADEMIC MARKS & GRADE MANAGEMENT");
            System.out.println("  1. View Course Catalog");
            System.out.println("  2. Record / Update Marks for a Student");
            System.out.println("  3. View Individual Student Academic Summary & Transcript");
            System.out.println("  4. Remove Subject Marks Record");
            System.out.println("  5. Back to Main Menu");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select option [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    displayCourses();
                    break;
                case 2:
                    recordMarks();
                    break;
                case 3:
                    viewStudentAcademicSummary();
                    break;
                case 4:
                    removeAcademicRecord();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    private void displayCourses() {
        UIConsole.printSubHeader("Available Course Catalog");
        for (Course c : academicService.getCourseCatalog().values()) {
            System.out.println("  • " + c);
        }
    }

    private void recordMarks() {
        UIConsole.printSubHeader("Record/Update Student Marks");
        String studentId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
        Student s = studentService.getStudentById(studentId);
        if (s == null) {
            UIConsole.printError("Student not found.");
            return;
        }

        displayCourses();
        String courseCode = InputValidator.readNonEmptyString(scanner, "Enter Course Code (e.g. CSE3001): ").toUpperCase();
        double internal = InputValidator.readDouble(scanner, "Enter Internal Marks (0.00 - 50.00): ", 0.0, 50.0);
        double exam = InputValidator.readDouble(scanner, "Enter Exam Marks (0.00 - 50.00): ", 0.0, 50.0);

        AcademicRecord record = new AcademicRecord(studentId, courseCode, internal, exam);
        academicService.addOrUpdateMarks(record);
        UIConsole.printSuccess("Marks recorded! Grade: " + record.getGrade() + " | Status: " + (record.isPassed() ? "PASS" : "FAIL"));
    }

    private void viewStudentAcademicSummary() {
        String studentId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
        Student s = studentService.getStudentById(studentId);
        if (s != null) {
            academicService.displayStudentAcademicSummary(s);
        } else {
            UIConsole.printError("Student not found.");
        }
    }

    private void removeAcademicRecord() {
        String studentId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
        String courseCode = InputValidator.readNonEmptyString(scanner, "Enter Course Code: ");
        if (academicService.removeRecord(studentId, courseCode)) {
            UIConsole.printSuccess("Academic record removed.");
        } else {
            UIConsole.printError("Record not found.");
        }
    }

    // ==========================================
    // MODULE 3: ATTENDANCE & ELIGIBILITY
    // ==========================================
    private void handleAttendanceMenu() {
        boolean back = false;
        while (!back) {
            UIConsole.printHeader("ATTENDANCE & EXAM ELIGIBILITY MANAGEMENT");
            System.out.println("  1. Record / Update Course Attendance");
            System.out.println("  2. View Individual Student Attendance & Eligibility");
            System.out.println("  3. Back to Main Menu");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select option [1-3]: ", 1, 3);
            switch (choice) {
                case 1:
                    recordAttendance();
                    break;
                case 2:
                    viewAttendance();
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private void recordAttendance() {
        UIConsole.printSubHeader("Record Course Attendance");
        String studentId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
        Student s = studentService.getStudentById(studentId);
        if (s == null) {
            UIConsole.printError("Student not found.");
            return;
        }

        String courseCode = InputValidator.readNonEmptyString(scanner, "Enter Course Code: ").toUpperCase();
        int total = InputValidator.readInt(scanner, "Enter Total Conducted Classes: ", 1, 200);
        int attended = InputValidator.readInt(scanner, "Enter Attended Classes: ", 0, total);

        AttendanceRecord ar = new AttendanceRecord(studentId, courseCode, total, attended);
        attendanceService.addOrUpdateAttendance(ar);
        UIConsole.printSuccess("Attendance recorded! Percentage: " + String.format("%.2f%%", ar.getAttendancePercentage()) + " | Status: " + ar.getEligibilityStatus());
    }

    private void viewAttendance() {
        String studentId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
        Student s = studentService.getStudentById(studentId);
        if (s != null) {
            attendanceService.displayStudentAttendance(s);
        } else {
            UIConsole.printError("Student not found.");
        }
    }

    // ==========================================
    // MODULE 4: PERFORMANCE ANALYTICS
    // ==========================================
    private void handleAnalyticsMenu() {
        boolean back = false;
        while (!back) {
            UIConsole.printHeader("PERFORMANCE ANALYTICS & INSIGHTS ENGINE");
            System.out.println("  1. Class Metrics & Averages Overview");
            System.out.println("  2. Top Academic Performers (Rank List)");
            System.out.println("  3. Grade Distribution Analysis");
            System.out.println("  4. Academically At-Risk Students Detection");
            System.out.println("  5. Back to Main Menu");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select option [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    analyticsService.displayClassPerformanceOverview();
                    break;
                case 2:
                    int topN = InputValidator.readInt(scanner, "How many top performers to view? ", 1, 50);
                    analyticsService.displayTopPerformers(topN);
                    break;
                case 3:
                    analyticsService.displayGradeDistribution();
                    break;
                case 4:
                    analyticsService.displayAtRiskStudents();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    // ==========================================
    // MODULE 5: REPORT GENERATION
    // ==========================================
    private void handleReportMenu() {
        boolean back = false;
        while (!back) {
            UIConsole.printHeader("REPORT GENERATION & FILE EXPORT MODULE");
            System.out.println("  1. Export Individual Student Official Transcript (.txt)");
            System.out.println("  2. Export Class Performance Summary Report (.txt)");
            System.out.println("  3. Export Academically At-Risk Alert Report (.txt)");
            System.out.println("  4. Generate All Reports at Once");
            System.out.println("  5. Back to Main Menu");
            UIConsole.printDivider();

            int choice = InputValidator.readInt(scanner, "Select option [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    String sId = InputValidator.readNonEmptyString(scanner, "Enter Student ID: ");
                    reportService.generateStudentTranscriptReport(sId);
                    break;
                case 2:
                    reportService.generateClassPerformanceReport();
                    break;
                case 3:
                    reportService.generateAtRiskReport();
                    break;
                case 4:
                    generateAllReports();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    private void generateAllReports() {
        UIConsole.printInfo("Generating reports for all students...");
        for (Student s : studentService.getAllStudents()) {
            reportService.generateStudentTranscriptReport(s.getStudentId());
        }
        reportService.generateClassPerformanceReport();
        reportService.generateAtRiskReport();
        UIConsole.printSuccess("All text reports generated successfully in './reports/' directory.");
    }

    private void runDiagnostics() {
        UIConsole.printHeader("RUNNING SYSTEM DIAGNOSTICS & TEST SUITE");
        TestRunner.runAllTests();
    }

    private void saveAllData() {
        studentService.saveChanges();
        academicService.saveChanges();
        attendanceService.saveChanges();
    }
}

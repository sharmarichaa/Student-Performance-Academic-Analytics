package com.vityarthi.analytics.test;

import com.vityarthi.analytics.model.*;
import com.vityarthi.analytics.repository.FileRepository;
import com.vityarthi.analytics.service.*;
import com.vityarthi.analytics.util.UIConsole;

import java.io.File;
import java.util.List;

/**
 * Diagnostic and automated test suite verifying core system logic and calculations.
 */
public class TestRunner {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {
        passedTests = 0;
        failedTests = 0;

        UIConsole.printHeader("STARTING AUTOMATED TEST SUITE");

        testGradeCalculation();
        testGPAComputation();
        testAttendanceEligibility();
        testStudentServiceCrud();
        testAtRiskDetection();
        testFilePersistence();
        testReportGeneration();

        UIConsole.printDivider();
        System.out.printf(" TEST RESULTS SUMMARY: Total: %d | Passed: %d | Failed: %d%n",
                (passedTests + failedTests), passedTests, failedTests);
        if (failedTests == 0) {
            UIConsole.printSuccess("ALL SYSTEM DIAGNOSTIC TESTS PASSED PERFECTLY!");
        } else {
            UIConsole.printError("SOME TESTS FAILED! Please inspect trace details.");
        }
        UIConsole.printDivider();
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.printf("  [PASS] %-50s (Expected: %s, Got: %s)%n", testName, expected, actual);
            passedTests++;
        } else {
            System.out.printf("  [FAIL] %-50s (Expected: %s, Got: %s)%n", testName, expected, actual);
            failedTests++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.printf("  [PASS] %-50s%n", testName);
            passedTests++;
        } else {
            System.out.printf("  [FAIL] %-50s%n", testName);
            failedTests++;
        }
    }

    private static void testGradeCalculation() {
        UIConsole.printSubHeader("Test 1: Grade & Grade Point Calculation Thresholds");

        AcademicRecord recS = new AcademicRecord("STU_T1", "CSE100", 48.0, 48.0); // 96% -> S
        assertEquals("Grade S Threshold", "S", recS.getGrade());
        assertEquals("Grade S Points", 10.0, recS.getGradePoint());
        assertTrue("Grade S Pass Status", recS.isPassed());

        AcademicRecord recA = new AcademicRecord("STU_T1", "CSE100", 42.0, 43.0); // 85% -> A
        assertEquals("Grade A Threshold", "A", recA.getGrade());
        assertEquals("Grade A Points", 9.0, recA.getGradePoint());

        AcademicRecord recF = new AcademicRecord("STU_T1", "CSE100", 15.0, 15.0); // 30% -> F
        assertEquals("Grade F Threshold", "F", recF.getGrade());
        assertEquals("Grade F Points", 0.0, recF.getGradePoint());
        assertTrue("Grade F Fail Status", !recF.isPassed());
    }

    private static void testGPAComputation() {
        UIConsole.printSubHeader("Test 2: GPA & Weighted Aggregate Computation");

        FileRepository repo = new FileRepository("data_test");
        StudentService sServ = new StudentService(repo);
        AcademicService aServ = new AcademicService(repo);

        Student s = new Student("TEST_GPA", "Test Student", "21TEST", "CSE", 1, "test@vityarthi.ac.in");
        sServ.addStudent(s);

        // CSE3001 (4 credits, 95% -> S = 10.0 pts)
        aServ.addOrUpdateMarks(new AcademicRecord("TEST_GPA", "CSE3001", 48.0, 47.0));
        // MAT2001 (4 credits, 75% -> B = 8.0 pts)
        aServ.addOrUpdateMarks(new AcademicRecord("TEST_GPA", "MAT2001", 38.0, 37.0));

        // Total weighted = (10.0 * 4) + (8.0 * 4) = 40 + 32 = 72
        // Total credits = 8 -> GPA = 72 / 8 = 9.00
        double gpa = aServ.calculateGPA("TEST_GPA");
        assertEquals("GPA Calculation Accuracy", 9.00, Math.round(gpa * 100.0) / 100.0);

        cleanTestDirectory("data_test");
    }

    private static void testAttendanceEligibility() {
        UIConsole.printSubHeader("Test 3: Attendance Percentage & Eligibility Thresholds");

        AttendanceRecord attGood = new AttendanceRecord("STU_T1", "CSE100", 40, 36); // 90%
        assertEquals("Attendance 90% Status", "ELIGIBLE", attGood.getEligibilityStatus());
        assertTrue("Attendance 90% Clearance", attGood.isEligibleForExam());

        AttendanceRecord attWarn = new AttendanceRecord("STU_T1", "CSE100", 40, 31); // 77.5%
        assertEquals("Attendance 77.5% Status", "ELIGIBLE (WARNING)", attWarn.getEligibilityStatus());
        assertTrue("Attendance 77.5% Clearance", attWarn.isEligibleForExam());

        AttendanceRecord attDeb = new AttendanceRecord("STU_T1", "CSE100", 40, 26); // 65.0%
        assertEquals("Attendance 65.0% Status", "DEBARRED", attDeb.getEligibilityStatus());
        assertTrue("Attendance 65.0% Debarred", !attDeb.isEligibleForExam());
    }

    private static void testStudentServiceCrud() {
        UIConsole.printSubHeader("Test 4: Student Service CRUD Operations");

        FileRepository repo = new FileRepository("data_test");
        StudentService sServ = new StudentService(repo);

        Student s = new Student("STU_CRUD", "John Doe", "21BCE999", "CSE", 3, "john@vityarthi.ac.in");
        assertTrue("Add Student", sServ.addStudent(s));
        assertEquals("Get Student By ID", "John Doe", sServ.getStudentById("STU_CRUD").getName());

        s.setName("John Updated");
        assertTrue("Update Student", sServ.updateStudent(s));
        assertEquals("Verify Updated Name", "John Updated", sServ.getStudentById("STU_CRUD").getName());

        assertTrue("Delete Student", sServ.deleteStudent("STU_CRUD"));
        assertTrue("Verify Student Deleted", sServ.getStudentById("STU_CRUD") == null);

        cleanTestDirectory("data_test");
    }

    private static void testAtRiskDetection() {
        UIConsole.printSubHeader("Test 5: Academically At-Risk Detection Engine");

        FileRepository repo = new FileRepository("data_test");
        StudentService sServ = new StudentService(repo);
        AcademicService aServ = new AcademicService(repo);
        AttendanceService attServ = new AttendanceService(repo);
        AnalyticsService analytics = new AnalyticsService(sServ, aServ, attServ);

        Student sGood = new Student("STU_GOOD", "Good Student", "21GOOD", "CSE", 1, "good@vityarthi.ac.in");
        Student sRisk = new Student("STU_RISK", "Risk Student", "21RISK", "CSE", 1, "risk@vityarthi.ac.in");
        sServ.addStudent(sGood);
        sServ.addStudent(sRisk);

        // Good student marks & attendance
        aServ.addOrUpdateMarks(new AcademicRecord("STU_GOOD", "CSE3001", 45.0, 45.0));
        attServ.addOrUpdateAttendance(new AttendanceRecord("STU_GOOD", "CSE3001", 40, 36));

        // Risk student marks (Fail) & attendance (Low)
        aServ.addOrUpdateMarks(new AcademicRecord("STU_RISK", "CSE3001", 15.0, 15.0));
        attServ.addOrUpdateAttendance(new AttendanceRecord("STU_RISK", "CSE3001", 40, 20));

        List<AnalyticsService.StudentPerformanceSummary> atRiskList = analytics.getAtRiskStudents();
        assertEquals("At-Risk Count", 1, atRiskList.size());
        assertEquals("At-Risk Identified ID", "STU_RISK", atRiskList.get(0).student.getStudentId());

        cleanTestDirectory("data_test");
    }

    private static void testFilePersistence() {
        UIConsole.printSubHeader("Test 6: File Persistence (CSV Serialization / Deserialization)");

        FileRepository repo = new FileRepository("data_test");
        Student s = new Student("STU_SER", "Ser Test", "21SER", "ECE", 2, "ser@vityarthi.ac.in");
        repo.saveStudents(List.of(s));

        List<Student> loaded = repo.loadStudents();
        assertEquals("Loaded Count", 1, loaded.size());
        assertEquals("Loaded Student ID", "STU_SER", loaded.get(0).getStudentId());

        cleanTestDirectory("data_test");
    }

    private static void testReportGeneration() {
        UIConsole.printSubHeader("Test 7: Report Generation to Text Files");

        FileRepository repo = new FileRepository("data_test");
        StudentService sServ = new StudentService(repo);
        AcademicService aServ = new AcademicService(repo);
        AttendanceService attServ = new AttendanceService(repo);
        AnalyticsService analytics = new AnalyticsService(sServ, aServ, attServ);
        ReportService reportService = new ReportService("reports_test", sServ, aServ, attServ, analytics);

        Student s = new Student("STU_REP", "Report Test", "21REP", "MECH", 4, "rep@vityarthi.ac.in");
        sServ.addStudent(s);
        aServ.addOrUpdateMarks(new AcademicRecord("STU_REP", "MECH201", 40.0, 40.0));
        attServ.addOrUpdateAttendance(new AttendanceRecord("STU_REP", "MECH201", 40, 35));

        String repFile = reportService.generateStudentTranscriptReport("STU_REP");
        assertTrue("Transcript File Generated", repFile != null && new File(repFile).exists());

        cleanTestDirectory("data_test");
        cleanTestDirectory("reports_test");
    }

    private static void cleanTestDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            dir.delete();
        }
    }
}

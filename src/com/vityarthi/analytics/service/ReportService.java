package com.vityarthi.analytics.service;

import com.vityarthi.analytics.model.AcademicRecord;
import com.vityarthi.analytics.model.AttendanceRecord;
import com.vityarthi.analytics.model.Course;
import com.vityarthi.analytics.model.Student;
import com.vityarthi.analytics.util.UIConsole;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Report Generation Service exporting text reports to disk.
 */
public class ReportService {

    private final String reportsDirPath;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final AttendanceService attendanceService;
    private final AnalyticsService analyticsService;

    public ReportService(String reportsDirPath, StudentService studentService, AcademicService academicService, AttendanceService attendanceService, AnalyticsService analyticsService) {
        this.reportsDirPath = reportsDirPath;
        this.studentService = studentService;
        this.academicService = academicService;
        this.attendanceService = attendanceService;
        this.analyticsService = analyticsService;
        ensureReportsDirectoryExists();
    }

    public ReportService(StudentService studentService, AcademicService academicService, AttendanceService attendanceService, AnalyticsService analyticsService) {
        this("reports", studentService, academicService, attendanceService, analyticsService);
    }

    private void ensureReportsDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(reportsDirPath));
        } catch (IOException e) {
            System.err.println("Error creating reports directory: " + e.getMessage());
        }
    }

    public String generateStudentTranscriptReport(String studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            UIConsole.printError("Student ID not found: " + studentId);
            return null;
        }

        String fileName = reportsDirPath + File.separator + "student_transcript_" + studentId + ".txt";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==========================================================================================");
            writer.println("                    OFFICIAL STUDENT ACADEMIC & ATTENDANCE TRANSCRIPT                    ");
            writer.println("==========================================================================================");
            writer.println(" Generated Date/Time: " + timestamp);
            writer.println("------------------------------------------------------------------------------------------");
            writer.printf(" Student ID   : %-15s | Roll Number : %-15s%n", student.getStudentId(), student.getRollNo());
            writer.printf(" Student Name : %-15s | Department  : %-15s%n", student.getName(), student.getDepartment());
            writer.printf(" Semester     : %-15d | Email       : %-25s%n", student.getSemester(), student.getEmail());
            writer.println("------------------------------------------------------------------------------------------");

            writer.println("\n--- ACADEMIC PERFORMANCE METRICS ---");
            List<AcademicRecord> aRecs = academicService.getStudentRecords(studentId);
            if (aRecs.isEmpty()) {
                writer.println(" No academic marks recorded.");
            } else {
                writer.printf("%-10s | %-25s | %-8s | %-8s | %-8s | %-6s | %-6s | %-6s%n",
                        "Code", "Course Title", "Internal", "Exam", "Total", "Pct%", "Grade", "Status");
                writer.println("------------------------------------------------------------------------------------------");
                for (AcademicRecord r : aRecs) {
                    Course c = academicService.getCourse(r.getCourseCode());
                    String title = (c != null) ? c.getTitle() : r.getCourseCode();
                    if (title.length() > 25) title = title.substring(0, 22) + "...";
                    writer.printf("%-10s | %-25s | %-8.2f | %-8.2f | %-8.2f | %-6.2f | %-6s | %-6s%n",
                            r.getCourseCode(), title, r.getInternalMarks(), r.getExamMarks(),
                            r.getTotalMarks(), r.getPercentage(), r.getGrade(), r.isPassed() ? "PASS" : "FAIL");
                }
                writer.println("------------------------------------------------------------------------------------------");
                writer.printf(" Cumulative GPA (CGPA)     : %.2f / 10.00%n", academicService.calculateGPA(studentId));
                writer.printf(" Aggregate Marks Percentage: %.2f%%%n", academicService.calculateOverallPercentage(studentId));
            }

            writer.println("\n--- ATTENDANCE & EXAM ELIGIBILITY ---");
            List<AttendanceRecord> attRecs = attendanceService.getStudentAttendance(studentId);
            if (attRecs.isEmpty()) {
                writer.println(" No attendance recorded.");
            } else {
                writer.printf("%-10s | %-15s | %-15s | %-12s | %-20s%n",
                        "Course", "Total Classes", "Attended", "Attendance %", "Eligibility Status");
                writer.println("------------------------------------------------------------------------------------------");
                for (AttendanceRecord ar : attRecs) {
                    writer.printf("%-10s | %-15d | %-15d | %-12.2f | %-20s%n",
                            ar.getCourseCode(), ar.getTotalClasses(), ar.getAttendedClasses(),
                            ar.getAttendancePercentage(), ar.getEligibilityStatus());
                }
                writer.println("------------------------------------------------------------------------------------------");
                double overallAtt = attendanceService.getOverallAttendancePercentage(studentId);
                writer.printf(" Overall Attendance Percentage : %.2f%%%n", overallAtt);
                writer.printf(" Final Exam Clearance Status   : %s%n",
                        overallAtt >= 80 ? "ELIGIBLE" : overallAtt >= 75 ? "ELIGIBLE WITH WARNING" : "DEBARRED");
            }

            writer.println("==========================================================================================");
            writer.println("                            END OF OFFICIAL TRANSCRIPT                            ");
            writer.println("==========================================================================================");

            UIConsole.printSuccess("Transcript report successfully generated: " + fileName);
            return fileName;
        } catch (IOException e) {
            UIConsole.printError("Failed to write transcript report: " + e.getMessage());
            return null;
        }
    }

    public String generateClassPerformanceReport() {
        String fileName = reportsDirPath + File.separator + "class_performance_report.txt";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<AnalyticsService.StudentPerformanceSummary> summaries = analyticsService.getAllStudentSummaries();

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==========================================================================================");
            writer.println("                    CLASS PERFORMANCE & ANALYTICS SUMMARY REPORT                          ");
            writer.println("==========================================================================================");
            writer.println(" Generated Date/Time: " + timestamp);
            writer.println(" Total Enrolled Students: " + summaries.size());
            writer.println("------------------------------------------------------------------------------------------");

            writer.println("\n--- STUDENT RANKINGS & OVERVIEW ---");
            writer.printf("%-6s | %-10s | %-20s | %-8s | %-8s | %-8s | %-10s%n",
                    "Rank", "ID", "Name", "CGPA", "Avg %", "Att %", "Status");
            writer.println("------------------------------------------------------------------------------------------");

            int rank = 1;
            for (AnalyticsService.StudentPerformanceSummary s : analyticsService.getTopPerformers(summaries.size())) {
                writer.printf("#%-5d | %-10s | %-20s | %-8.2f | %-8.2f | %-8.2f | %-10s%n",
                        rank++, s.student.getStudentId(), s.student.getName(), s.gpa, s.aggregatePct, s.attendancePct,
                        s.isAtRisk() ? "AT-RISK" : "GOOD");
            }

            writer.println("\n--- GRADE DISTRIBUTION ANALYSIS ---");
            Map<String, Integer> dist = analyticsService.getGradeDistribution();
            int totalGrades = dist.values().stream().mapToInt(Integer::intValue).sum();
            for (Map.Entry<String, Integer> e : dist.entrySet()) {
                double pct = (totalGrades > 0) ? ((double) e.getValue() / totalGrades) * 100.0 : 0.0;
                writer.printf(" Grade %-2s : %-4d student(s) (%.2f%%)%n", e.getKey(), e.getValue(), pct);
            }

            writer.println("==========================================================================================");
            writer.println("                         END OF CLASS PERFORMANCE REPORT                         ");
            writer.println("==========================================================================================");

            UIConsole.printSuccess("Class performance report saved: " + fileName);
            return fileName;
        } catch (IOException e) {
            UIConsole.printError("Failed to write class performance report: " + e.getMessage());
            return null;
        }
    }

    public String generateAtRiskReport() {
        String fileName = reportsDirPath + File.separator + "at_risk_students_report.txt";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<AnalyticsService.StudentPerformanceSummary> atRiskList = analyticsService.getAtRiskStudents();

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==========================================================================================");
            writer.println("                     ACADEMICALLY AT-RISK STUDENTS ALERT REPORT                           ");
            writer.println("==========================================================================================");
            writer.println(" Generated Date/Time: " + timestamp);
            writer.println(" Total Flagged At-Risk Students: " + atRiskList.size());
            writer.println("------------------------------------------------------------------------------------------");

            if (atRiskList.isEmpty()) {
                writer.println(" Excellent! Zero students are currently identified as academically at-risk.");
            } else {
                writer.printf("%-10s | %-20s | %-6s | %-8s | %-40s%n",
                        "ID", "Name", "GPA", "Att %", "Risk Factors");
                writer.println("------------------------------------------------------------------------------------------");
                for (AnalyticsService.StudentPerformanceSummary s : atRiskList) {
                    String reasons = String.join("; ", s.getRiskReasons());
                    writer.printf("%-10s | %-20s | %-6.2f | %-8.2f | %-40s%n",
                            s.student.getStudentId(), s.student.getName(), s.gpa, s.attendancePct, reasons);
                }
            }

            writer.println("==========================================================================================");
            writer.println("                         END OF AT-RISK ALERT REPORT                             ");
            writer.println("==========================================================================================");

            UIConsole.printSuccess("At-risk alert report saved: " + fileName);
            return fileName;
        } catch (IOException e) {
            UIConsole.printError("Failed to write at-risk report: " + e.getMessage());
            return null;
        }
    }
}

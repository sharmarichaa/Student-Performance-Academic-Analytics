package com.vityarthi.analytics.service;

import com.vityarthi.analytics.model.AcademicRecord;
import com.vityarthi.analytics.model.AttendanceRecord;
import com.vityarthi.analytics.model.Student;
import com.vityarthi.analytics.util.UIConsole;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Performance Analytics Engine calculating class statistics, subject performance, top performers,
 * grade distribution, and identifying academically at-risk students.
 */
public class AnalyticsService {

    private final StudentService studentService;
    private final AcademicService academicService;
    private final AttendanceService attendanceService;

    public AnalyticsService(StudentService studentService, AcademicService academicService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.academicService = academicService;
        this.attendanceService = attendanceService;
    }

    public static class StudentPerformanceSummary {
        public Student student;
        public double gpa;
        public double aggregatePct;
        public double attendancePct;
        public int failedCoursesCount;
        public boolean isDebarred;

        public StudentPerformanceSummary(Student student, double gpa, double aggregatePct, double attendancePct, int failedCoursesCount, boolean isDebarred) {
            this.student = student;
            this.gpa = gpa;
            this.aggregatePct = aggregatePct;
            this.attendancePct = attendancePct;
            this.failedCoursesCount = failedCoursesCount;
            this.isDebarred = isDebarred;
        }

        public boolean isAtRisk() {
            return gpa < 5.0 || failedCoursesCount > 0 || isDebarred || attendancePct < 75.0;
        }

        public List<String> getRiskReasons() {
            List<String> reasons = new ArrayList<>();
            if (gpa < 5.0) reasons.add("Low GPA (" + String.format("%.2f", gpa) + " < 5.0)");
            if (failedCoursesCount > 0) reasons.add("Failed in " + failedCoursesCount + " subject(s)");
            if (attendancePct < 75.0) reasons.add("Low Attendance (" + String.format("%.2f", attendancePct) + "% < 75%)");
            return reasons;
        }
    }

    public List<StudentPerformanceSummary> getAllStudentSummaries() {
        List<StudentPerformanceSummary> list = new ArrayList<>();
        List<Student> students = studentService.getAllStudents();

        for (Student s : students) {
            double gpa = academicService.calculateGPA(s.getStudentId());
            double aggPct = academicService.calculateOverallPercentage(s.getStudentId());
            double attPct = attendanceService.getOverallAttendancePercentage(s.getStudentId());

            List<AcademicRecord> aRecs = academicService.getStudentRecords(s.getStudentId());
            int failed = 0;
            for (AcademicRecord r : aRecs) {
                if (!r.isPassed()) failed++;
            }

            List<AttendanceRecord> attRecs = attendanceService.getStudentAttendance(s.getStudentId());
            boolean debarred = false;
            for (AttendanceRecord ar : attRecs) {
                if (!ar.isEligibleForExam()) {
                    debarred = true;
                    break;
                }
            }

            list.add(new StudentPerformanceSummary(s, gpa, aggPct, attPct, failed, debarred));
        }

        return list;
    }

    public void displayClassPerformanceOverview() {
        List<StudentPerformanceSummary> summaries = getAllStudentSummaries();
        if (summaries.isEmpty()) {
            UIConsole.printWarning("No student data available for analytics.");
            return;
        }

        double totalGpa = 0.0;
        double totalPct = 0.0;
        double totalAtt = 0.0;
        for (StudentPerformanceSummary sps : summaries) {
            totalGpa += sps.gpa;
            totalPct += sps.aggregatePct;
            totalAtt += sps.attendancePct;
        }

        double avgGpa = totalGpa / summaries.size();
        double avgPct = totalPct / summaries.size();
        double avgAtt = totalAtt / summaries.size();

        UIConsole.printHeader("CLASS PERFORMANCE & METRICS OVERVIEW");
        System.out.printf("  Total Enrolled Students : %d%n", summaries.size());
        System.out.printf("  Class Average GPA        : %.2f / 10.00%n", avgGpa);
        System.out.printf("  Class Average Marks Pct  : %.2f%%%n", avgPct);
        System.out.printf("  Class Average Attendance : %.2f%%%n", avgAtt);
        UIConsole.printDivider();
    }

    public List<StudentPerformanceSummary> getTopPerformers(int topN) {
        List<StudentPerformanceSummary> summaries = getAllStudentSummaries();
        summaries.sort((a, b) -> Double.compare(b.gpa, a.gpa)); // Descending order
        return summaries.stream().limit(topN).collect(Collectors.toList());
    }

    public void displayTopPerformers(int topN) {
        List<StudentPerformanceSummary> tops = getTopPerformers(topN);
        UIConsole.printHeader("TOP " + topN + " ACADEMIC PERFORMERS (RANK LIST)");
        System.out.printf("%-6s | %-10s | %-20s | %-10s | %-8s | %-8s%n",
                "Rank", "ID", "Name", "Dept", "CGPA", "Avg %");
        UIConsole.printDivider();
        int rank = 1;
        for (StudentPerformanceSummary s : tops) {
            System.out.printf("#%-5d | %-10s | %-20s | %-10s | %-8.2f | %-8.2f%n",
                    rank++, s.student.getStudentId(), s.student.getName(), s.student.getDepartment(), s.gpa, s.aggregatePct);
        }
        UIConsole.printDivider();
    }

    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("S", 0);
        dist.put("A", 0);
        dist.put("B", 0);
        dist.put("C", 0);
        dist.put("D", 0);
        dist.put("E", 0);
        dist.put("F", 0);

        List<AcademicRecord> allRecs = academicService.getAllRecords();
        for (AcademicRecord r : allRecs) {
            String g = r.getGrade();
            dist.put(g, dist.getOrDefault(g, 0) + 1);
        }

        return dist;
    }

    public void displayGradeDistribution() {
        Map<String, Integer> dist = getGradeDistribution();
        int totalGrades = dist.values().stream().mapToInt(Integer::intValue).sum();

        UIConsole.printHeader("ACADEMIC GRADE DISTRIBUTION ANALYSIS");
        if (totalGrades == 0) {
            UIConsole.printWarning("No subject grades recorded.");
            return;
        }

        System.out.printf("%-8s | %-10s | %-12s | %-30s%n", "Grade", "Count", "Percentage", "Visual Bar");
        UIConsole.printDivider();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            String grade = entry.getKey();
            int count = entry.getValue();
            double pct = (totalGrades > 0) ? ((double) count / totalGrades) * 100.0 : 0.0;
            int barLength = (int) Math.round(pct / 2.5); // 100% = 40 blocks
            String bar = "█".repeat(Math.max(0, barLength));
            System.out.printf("%-8s | %-10d | %-11.2f%% | %-30s%n", grade, count, pct, bar);
        }
        UIConsole.printDivider();
    }

    public List<StudentPerformanceSummary> getAtRiskStudents() {
        List<StudentPerformanceSummary> summaries = getAllStudentSummaries();
        List<StudentPerformanceSummary> atRisk = new ArrayList<>();
        for (StudentPerformanceSummary sps : summaries) {
            if (sps.isAtRisk()) {
                atRisk.add(sps);
            }
        }
        return atRisk;
    }

    public void displayAtRiskStudents() {
        List<StudentPerformanceSummary> atRiskList = getAtRiskStudents();
        UIConsole.printHeader("ACADEMICALLY AT-RISK STUDENTS DETECTION");
        if (atRiskList.isEmpty()) {
            UIConsole.printSuccess("No students are currently flagged as academically at-risk!");
            return;
        }

        System.out.printf("%-10s | %-20s | %-6s | %-8s | %-35s%n",
                "ID", "Name", "GPA", "Att %", "Risk Factors / Reason(s)");
        UIConsole.printDivider();
        for (StudentPerformanceSummary sps : atRiskList) {
            String reasons = String.join("; ", sps.getRiskReasons());
            System.out.printf("%-10s | %-20s | %-6.2f | %-8.2f | %-35s%n",
                    sps.student.getStudentId(), sps.student.getName(), sps.gpa, sps.attendancePct, reasons);
        }
        UIConsole.printDivider();
    }
}

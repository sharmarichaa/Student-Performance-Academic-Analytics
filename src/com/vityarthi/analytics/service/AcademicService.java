package com.vityarthi.analytics.service;

import com.vityarthi.analytics.model.AcademicRecord;
import com.vityarthi.analytics.model.Course;
import com.vityarthi.analytics.model.Student;
import com.vityarthi.analytics.repository.FileRepository;
import com.vityarthi.analytics.util.UIConsole;

import java.util.*;

/**
 * Service class managing subject marks, grade calculation, and GPA computation.
 */
public class AcademicService {

    private final List<AcademicRecord> academicRecords;
    private final Map<String, Course> courseCatalog;
    private final FileRepository fileRepository;

    public AcademicService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.academicRecords = new ArrayList<>();
        this.courseCatalog = new HashMap<>();
        initializeCourseCatalog();
        loadFromRepository();
    }

    private void initializeCourseCatalog() {
        courseCatalog.put("CSE3001", new Course("CSE3001", "Software Engineering", 4));
        courseCatalog.put("CSE3002", new Course("CSE3002", "Data Structures & Algorithms", 4));
        courseCatalog.put("ECE3001", new Course("ECE3001", "Digital Signal Processing", 3));
        courseCatalog.put("MECH201", new Course("MECH201", "Thermodynamics", 3));
        courseCatalog.put("MAT2001", new Course("MAT2001", "Differential Equations", 4));
    }

    private void loadFromRepository() {
        academicRecords.clear();
        academicRecords.addAll(fileRepository.loadAcademicRecords());
    }

    public void addCourseToCatalog(Course course) {
        courseCatalog.put(course.getCourseCode(), course);
    }

    public Course getCourse(String courseCode) {
        return courseCatalog.getOrDefault(courseCode, new Course(courseCode, courseCode, 3));
    }

    public Map<String, Course> getCourseCatalog() {
        return courseCatalog;
    }

    public void addOrUpdateMarks(AcademicRecord record) {
        // Remove existing record for same student and course if present
        academicRecords.removeIf(r -> r.getStudentId().equalsIgnoreCase(record.getStudentId())
                && r.getCourseCode().equalsIgnoreCase(record.getCourseCode()));
        academicRecords.add(record);
        saveChanges();
    }

    public boolean removeRecord(String studentId, String courseCode) {
        boolean removed = academicRecords.removeIf(r -> r.getStudentId().equalsIgnoreCase(studentId)
                && r.getCourseCode().equalsIgnoreCase(courseCode));
        if (removed) {
            saveChanges();
        }
        return removed;
    }

    public List<AcademicRecord> getStudentRecords(String studentId) {
        List<AcademicRecord> list = new ArrayList<>();
        for (AcademicRecord r : academicRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId)) {
                list.add(r);
            }
        }
        return list;
    }

    public List<AcademicRecord> getAllRecords() {
        return Collections.unmodifiableList(academicRecords);
    }

    public double calculateGPA(String studentId) {
        List<AcademicRecord> records = getStudentRecords(studentId);
        if (records.isEmpty()) return 0.0;

        double totalWeightedGradePoints = 0.0;
        int totalCredits = 0;

        for (AcademicRecord r : records) {
            Course c = getCourse(r.getCourseCode());
            int credits = (c != null) ? c.getCredits() : 3;
            totalWeightedGradePoints += r.getGradePoint() * credits;
            totalCredits += credits;
        }

        return (totalCredits > 0) ? (totalWeightedGradePoints / totalCredits) : 0.0;
    }

    public double calculateOverallPercentage(String studentId) {
        List<AcademicRecord> records = getStudentRecords(studentId);
        if (records.isEmpty()) return 0.0;

        double totalObtained = 0.0;
        double totalMax = 0.0;

        for (AcademicRecord r : records) {
            totalObtained += r.getTotalMarks();
            totalMax += r.getMaxTotalMarks();
        }

        return (totalMax > 0) ? (totalObtained / totalMax) * 100.0 : 0.0;
    }

    public void displayStudentAcademicSummary(Student student) {
        if (student == null) return;
        List<AcademicRecord> records = getStudentRecords(student.getStudentId());

        UIConsole.printHeader("ACADEMIC PERFORMANCE SUMMARY: " + student.getName() + " (" + student.getStudentId() + ")");
        if (records.isEmpty()) {
            UIConsole.printWarning("No marks recorded for this student.");
            return;
        }

        System.out.printf("%-10s | %-25s | %-8s | %-8s | %-8s | %-6s | %-6s | %-6s%n",
                "Code", "Course Title", "Internal", "Exam", "Total", "Pct%", "Grade", "Status");
        UIConsole.printDivider();

        for (AcademicRecord r : records) {
            Course c = getCourse(r.getCourseCode());
            String title = (c != null) ? c.getTitle() : r.getCourseCode();
            if (title.length() > 25) title = title.substring(0, 22) + "...";

            System.out.printf("%-10s | %-25s | %-8.2f | %-8.2f | %-8.2f | %-6.2f | %-6s | %-6s%n",
                    r.getCourseCode(), title, r.getInternalMarks(), r.getExamMarks(),
                    r.getTotalMarks(), r.getPercentage(), r.getGrade(), r.isPassed() ? "PASS" : "FAIL");
        }

        UIConsole.printDivider();
        double gpa = calculateGPA(student.getStudentId());
        double pct = calculateOverallPercentage(student.getStudentId());
        System.out.printf("  Cumulative GPA (CGPA): %.2f / 10.00%n", gpa);
        System.out.printf("  Overall Aggregate Percentage: %.2f%%%n", pct);
        UIConsole.printDivider();
    }

    public void saveChanges() {
        fileRepository.saveAcademicRecords(academicRecords);
    }
}

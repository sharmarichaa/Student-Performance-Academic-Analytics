package com.vityarthi.analytics.model;

import java.io.Serializable;

/**
 * Model class representing academic marks and performance metrics for a specific course.
 */
public class AcademicRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String courseCode;
    private double internalMarks; // Out of 50 or 40
    private double examMarks;     // Out of 50 or 60
    private double maxInternal;
    private double maxExam;

    public AcademicRecord(String studentId, String courseCode, double internalMarks, double examMarks, double maxInternal, double maxExam) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.internalMarks = internalMarks;
        this.examMarks = examMarks;
        this.maxInternal = maxInternal;
        this.maxExam = maxExam;
    }

    public AcademicRecord(String studentId, String courseCode, double internalMarks, double examMarks) {
        this(studentId, courseCode, internalMarks, examMarks, 50.0, 50.0);
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public double getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(double internalMarks) {
        this.internalMarks = internalMarks;
    }

    public double getExamMarks() {
        return examMarks;
    }

    public void setExamMarks(double examMarks) {
        this.examMarks = examMarks;
    }

    public double getMaxInternal() {
        return maxInternal;
    }

    public double getMaxExam() {
        return maxExam;
    }

    public double getTotalMarks() {
        return internalMarks + examMarks;
    }

    public double getMaxTotalMarks() {
        return maxInternal + maxExam;
    }

    public double getPercentage() {
        double maxTotal = getMaxTotalMarks();
        if (maxTotal <= 0) return 0.0;
        return (getTotalMarks() / maxTotal) * 100.0;
    }

    public String getGrade() {
        double pct = getPercentage();
        if (pct >= 90.0) return "S";
        if (pct >= 80.0) return "A";
        if (pct >= 70.0) return "B";
        if (pct >= 60.0) return "C";
        if (pct >= 50.0) return "D";
        if (pct >= 40.0) return "E";
        return "F";
    }

    public double getGradePoint() {
        String grade = getGrade();
        switch (grade) {
            case "S": return 10.0;
            case "A": return 9.0;
            case "B": return 8.0;
            case "C": return 7.0;
            case "D": return 6.0;
            case "E": return 5.0;
            default: return 0.0;
        }
    }

    public boolean isPassed() {
        return getPercentage() >= 40.0 && !getGrade().equals("F");
    }

    public String toCsv() {
        return String.format("%s,%s,%.2f,%.2f,%.2f,%.2f",
                studentId, courseCode, internalMarks, examMarks, maxInternal, maxExam);
    }

    public static AcademicRecord fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 4) return null;
        String sId = parts[0].trim();
        String cCode = parts[1].trim();
        double internal = Double.parseDouble(parts[2].trim());
        double exam = Double.parseDouble(parts[3].trim());
        double maxInt = parts.length >= 5 ? Double.parseDouble(parts[4].trim()) : 50.0;
        double maxEx = parts.length >= 6 ? Double.parseDouble(parts[5].trim()) : 50.0;
        return new AcademicRecord(sId, cCode, internal, exam, maxInt, maxEx);
    }

    @Override
    public String toString() {
        return String.format("AcademicRecord[Student=%s, Course=%s, Total=%.2f/%.2f (%.2f%%), Grade=%s, Status=%s]",
                studentId, courseCode, getTotalMarks(), getMaxTotalMarks(), getPercentage(), getGrade(), isPassed() ? "PASS" : "FAIL");
    }
}

package com.vityarthi.analytics.model;

import java.io.Serializable;

/**
 * Model class representing attendance records and exam eligibility status.
 */
public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String courseCode;
    private int totalClasses;
    private int attendedClasses;

    public AttendanceRecord(String studentId, String courseCode, int totalClasses, int attendedClasses) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.totalClasses = totalClasses;
        this.attendedClasses = Math.min(attendedClasses, totalClasses);
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = Math.min(attendedClasses, this.totalClasses);
    }

    public double getAttendancePercentage() {
        if (totalClasses <= 0) return 100.0;
        return ((double) attendedClasses / totalClasses) * 100.0;
    }

    public String getEligibilityStatus() {
        double pct = getAttendancePercentage();
        if (pct >= 80.0) {
            return "ELIGIBLE";
        } else if (pct >= 75.0) {
            return "ELIGIBLE (WARNING)";
        } else {
            return "DEBARRED";
        }
    }

    public boolean isEligibleForExam() {
        return getAttendancePercentage() >= 75.0;
    }

    public String toCsv() {
        return String.format("%s,%s,%d,%d", studentId, courseCode, totalClasses, attendedClasses);
    }

    public static AttendanceRecord fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 4) return null;
        return new AttendanceRecord(
                parts[0].trim(),
                parts[1].trim(),
                Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[3].trim())
        );
    }

    @Override
    public String toString() {
        return String.format("Attendance[Student=%s, Course=%s, Attended=%d/%d (%.2f%%), Status=%s]",
                studentId, courseCode, attendedClasses, totalClasses, getAttendancePercentage(), getEligibilityStatus());
    }
}

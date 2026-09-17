package com.vityarthi.analytics.service;

import com.vityarthi.analytics.model.AttendanceRecord;
import com.vityarthi.analytics.model.Student;
import com.vityarthi.analytics.repository.FileRepository;
import com.vityarthi.analytics.util.UIConsole;

import java.util.*;

/**
 * Service class managing Attendance recording and exam eligibility calculations.
 */
public class AttendanceService {

    private final List<AttendanceRecord> attendanceRecords;
    private final FileRepository fileRepository;

    public AttendanceService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.attendanceRecords = new ArrayList<>();
        loadFromRepository();
    }

    private void loadFromRepository() {
        attendanceRecords.clear();
        attendanceRecords.addAll(fileRepository.loadAttendanceRecords());
    }

    public void addOrUpdateAttendance(AttendanceRecord record) {
        attendanceRecords.removeIf(r -> r.getStudentId().equalsIgnoreCase(record.getStudentId())
                && r.getCourseCode().equalsIgnoreCase(record.getCourseCode()));
        attendanceRecords.add(record);
        saveChanges();
    }

    public List<AttendanceRecord> getStudentAttendance(String studentId) {
        List<AttendanceRecord> list = new ArrayList<>();
        for (AttendanceRecord r : attendanceRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId)) {
                list.add(r);
            }
        }
        return list;
    }

    public List<AttendanceRecord> getAllAttendanceRecords() {
        return Collections.unmodifiableList(attendanceRecords);
    }

    public AttendanceRecord getAttendanceRecord(String studentId, String courseCode) {
        for (AttendanceRecord r : attendanceRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId) && r.getCourseCode().equalsIgnoreCase(courseCode)) {
                return r;
            }
        }
        return null;
    }

    public double getOverallAttendancePercentage(String studentId) {
        List<AttendanceRecord> list = getStudentAttendance(studentId);
        if (list.isEmpty()) return 100.0;

        int totalConducted = 0;
        int totalAttended = 0;
        for (AttendanceRecord r : list) {
            totalConducted += r.getTotalClasses();
            totalAttended += r.getAttendedClasses();
        }

        return (totalConducted > 0) ? ((double) totalAttended / totalConducted) * 100.0 : 100.0;
    }

    public void displayStudentAttendance(Student student) {
        if (student == null) return;
        List<AttendanceRecord> records = getStudentAttendance(student.getStudentId());

        UIConsole.printHeader("ATTENDANCE & ELIGIBILITY REPORT: " + student.getName() + " (" + student.getStudentId() + ")");
        if (records.isEmpty()) {
            UIConsole.printWarning("No attendance records found for this student.");
            return;
        }

        System.out.printf("%-10s | %-15s | %-15s | %-12s | %-20s%n",
                "Course", "Total Classes", "Attended", "Attendance %", "Exam Eligibility");
        UIConsole.printDivider();

        for (AttendanceRecord r : records) {
            System.out.printf("%-10s | %-15d | %-15d | %-12.2f | %-20s%n",
                    r.getCourseCode(), r.getTotalClasses(), r.getAttendedClasses(),
                    r.getAttendancePercentage(), r.getEligibilityStatus());
        }

        UIConsole.printDivider();
        double overallPct = getOverallAttendancePercentage(student.getStudentId());
        String status = (overallPct >= 80.0) ? "ELIGIBLE" : (overallPct >= 75.0) ? "ELIGIBLE (WARNING)" : "DEBARRED";
        System.out.printf("  Overall Attendance Percentage: %.2f%%%n", overallPct);
        System.out.printf("  Overall Examination Status: %s%n", status);
        UIConsole.printDivider();
    }

    public void saveChanges() {
        fileRepository.saveAttendanceRecords(attendanceRecords);
    }
}

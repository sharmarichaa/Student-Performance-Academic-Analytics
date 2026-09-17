package com.vityarthi.analytics.repository;

import com.vityarthi.analytics.model.AcademicRecord;
import com.vityarthi.analytics.model.AttendanceRecord;
import com.vityarthi.analytics.model.Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles file-based data persistence reading and writing CSV files.
 */
public class FileRepository {

    private final String dataDirPath;
    private final String studentsFilePath;
    private final String academicFilePath;
    private final String attendanceFilePath;

    public FileRepository(String dataDirPath) {
        this.dataDirPath = dataDirPath;
        this.studentsFilePath = dataDirPath + File.separator + "students.csv";
        this.academicFilePath = dataDirPath + File.separator + "academic_records.csv";
        this.attendanceFilePath = dataDirPath + File.separator + "attendance.csv";
        ensureDataDirectoryExists();
    }

    public FileRepository() {
        this("data");
    }

    private void ensureDataDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(dataDirPath));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(studentsFilePath);
        if (!file.exists()) {
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("studentid")) continue;
                }
                if (line.trim().isEmpty()) continue;
                Student s = Student.fromCsv(line);
                if (s != null) {
                    students.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students file: " + e.getMessage());
        }
        return students;
    }

    public void saveStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentsFilePath))) {
            writer.write("studentId,name,rollNo,department,semester,email");
            writer.newLine();
            for (Student s : students) {
                writer.write(s.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving students file: " + e.getMessage());
        }
    }

    public List<AcademicRecord> loadAcademicRecords() {
        List<AcademicRecord> records = new ArrayList<>();
        File file = new File(academicFilePath);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("studentid")) continue;
                }
                if (line.trim().isEmpty()) continue;
                AcademicRecord r = AcademicRecord.fromCsv(line);
                if (r != null) {
                    records.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading academic records file: " + e.getMessage());
        }
        return records;
    }

    public void saveAcademicRecords(List<AcademicRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(academicFilePath))) {
            writer.write("studentId,courseCode,internalMarks,examMarks,maxInternal,maxExam");
            writer.newLine();
            for (AcademicRecord r : records) {
                writer.write(r.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving academic records file: " + e.getMessage());
        }
    }

    public List<AttendanceRecord> loadAttendanceRecords() {
        List<AttendanceRecord> records = new ArrayList<>();
        File file = new File(attendanceFilePath);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("studentid")) continue;
                }
                if (line.trim().isEmpty()) continue;
                AttendanceRecord r = AttendanceRecord.fromCsv(line);
                if (r != null) {
                    records.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance records file: " + e.getMessage());
        }
        return records;
    }

    public void saveAttendanceRecords(List<AttendanceRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(attendanceFilePath))) {
            writer.write("studentId,courseCode,totalClasses,attendedClasses");
            writer.newLine();
            for (AttendanceRecord r : records) {
                writer.write(r.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving attendance records file: " + e.getMessage());
        }
    }

    /**
     * Initializes dataset with high-quality sample data if files don't exist.
     */
    public void seedInitialDataIfEmpty() {
        File sFile = new File(studentsFilePath);
        if (sFile.exists() && sFile.length() > 30) return;

        List<Student> sampleStudents = Arrays.asList(
                new Student("STU101", "Aarav Sharma", "21BCE1001", "CSE", 5, "aarav.sharma@vityarthi.ac.in"),
                new Student("STU102", "Ananya Verma", "21BCE1002", "CSE", 5, "ananya.verma@vityarthi.ac.in"),
                new Student("STU103", "Rohan Gupta", "21ECE1003", "ECE", 5, "rohan.gupta@vityarthi.ac.in"),
                new Student("STU104", "Priya Nair", "21MECH1004", "MECH", 5, "priya.nair@vityarthi.ac.in"),
                new Student("STU105", "Vikram Singh", "21BCE1005", "CSE", 5, "vikram.singh@vityarthi.ac.in")
        );
        saveStudents(sampleStudents);

        List<AcademicRecord> sampleAcademic = Arrays.asList(
                // Aarav Sharma (Top performer)
                new AcademicRecord("STU101", "CSE3001", 48.0, 47.0), // Total 95 -> S
                new AcademicRecord("STU101", "CSE3002", 44.0, 43.0), // Total 87 -> A
                new AcademicRecord("STU101", "MAT2001", 46.0, 48.0), // Total 94 -> S

                // Ananya Verma (Good performer)
                new AcademicRecord("STU102", "CSE3001", 40.0, 42.0), // Total 82 -> A
                new AcademicRecord("STU102", "CSE3002", 38.0, 37.0), // Total 75 -> B
                new AcademicRecord("STU102", "MAT2001", 42.0, 40.0), // Total 82 -> A

                // Rohan Gupta (Average)
                new AcademicRecord("STU103", "CSE3001", 32.0, 33.0), // Total 65 -> C
                new AcademicRecord("STU103", "ECE3001", 28.0, 30.0), // Total 58 -> D
                new AcademicRecord("STU103", "MAT2001", 30.0, 25.0), // Total 55 -> D

                // Priya Nair (At-risk attendance + marginal marks)
                new AcademicRecord("STU104", "MECH201", 22.0, 24.0), // Total 46 -> E
                new AcademicRecord("STU104", "MAT2001", 18.0, 15.0), // Total 33 -> F (Fail)

                // Vikram Singh (At-risk low marks)
                new AcademicRecord("STU105", "CSE3001", 15.0, 18.0), // Total 33 -> F (Fail)
                new AcademicRecord("STU105", "CSE3002", 18.0, 20.0)  // Total 38 -> F (Fail)
        );
        saveAcademicRecords(sampleAcademic);

        List<AttendanceRecord> sampleAttendance = Arrays.asList(
                new AttendanceRecord("STU101", "CSE3001", 40, 38), // 95%
                new AttendanceRecord("STU101", "CSE3002", 40, 37), // 92.5%
                new AttendanceRecord("STU101", "MAT2001", 40, 39), // 97.5%

                new AttendanceRecord("STU102", "CSE3001", 40, 34), // 85%
                new AttendanceRecord("STU102", "CSE3002", 40, 33), // 82.5%
                new AttendanceRecord("STU102", "MAT2001", 40, 36), // 90%

                new AttendanceRecord("STU103", "CSE3001", 40, 31), // 77.5% (Warning)
                new AttendanceRecord("STU103", "ECE3001", 40, 30), // 75.0% (Warning)
                new AttendanceRecord("STU103", "MAT2001", 40, 32), // 80.0%

                new AttendanceRecord("STU104", "MECH201", 40, 26), // 65.0% (Debarred)
                new AttendanceRecord("STU104", "MAT2001", 40, 24), // 60.0% (Debarred)

                new AttendanceRecord("STU105", "CSE3001", 40, 28), // 70.0% (Debarred)
                new AttendanceRecord("STU105", "CSE3002", 40, 29)  // 72.5% (Debarred)
        );
        saveAttendanceRecords(sampleAttendance);
    }
}

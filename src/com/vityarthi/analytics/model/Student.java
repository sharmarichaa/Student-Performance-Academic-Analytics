package com.vityarthi.analytics.model;

import java.io.Serializable;

/**
 * Model class representing a Student in the system.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private String rollNo;
    private String department;
    private int semester;
    private String email;

    public Student(String studentId, String name, String rollNo, String department, int semester, String email) {
        this.studentId = studentId;
        this.name = name;
        this.rollNo = rollNo;
        this.department = department;
        this.semester = semester;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%d,%s",
                escapeCsv(studentId),
                escapeCsv(name),
                escapeCsv(rollNo),
                escapeCsv(department),
                semester,
                escapeCsv(email));
    }

    public static Student fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 6) return null;
        return new Student(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Integer.parseInt(parts[4].trim()),
                parts[5].trim()
        );
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace(",", " ");
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, RollNo=%s, Dept=%s, Sem=%d, Email=%s]",
                studentId, name, rollNo, department, semester, email);
    }
}

package com.vityarthi.analytics.service;

import com.vityarthi.analytics.model.Student;
import com.vityarthi.analytics.repository.FileRepository;
import com.vityarthi.analytics.util.UIConsole;

import java.util.*;

/**
 * Service class managing Student records operations (CRUD & Search).
 */
public class StudentService {

    private final Map<String, Student> studentMap;
    private final FileRepository fileRepository;

    public StudentService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.studentMap = new HashMap<>();
        loadFromRepository();
    }

    private void loadFromRepository() {
        studentMap.clear();
        List<Student> students = fileRepository.loadStudents();
        for (Student s : students) {
            studentMap.put(s.getStudentId(), s);
        }
    }

    public boolean addStudent(Student student) {
        if (studentMap.containsKey(student.getStudentId())) {
            return false;
        }
        studentMap.put(student.getStudentId(), student);
        saveChanges();
        return true;
    }

    public boolean updateStudent(Student updatedStudent) {
        if (!studentMap.containsKey(updatedStudent.getStudentId())) {
            return false;
        }
        studentMap.put(updatedStudent.getStudentId(), updatedStudent);
        saveChanges();
        return true;
    }

    public boolean deleteStudent(String studentId) {
        if (!studentMap.containsKey(studentId)) {
            return false;
        }
        studentMap.remove(studentId);
        saveChanges();
        return true;
    }

    public Student getStudentById(String studentId) {
        return studentMap.get(studentId);
    }

    public Student getStudentByRollNo(String rollNo) {
        for (Student s : studentMap.values()) {
            if (s.getRollNo().equalsIgnoreCase(rollNo)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> searchStudentsByName(String query) {
        List<Student> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (Student s : studentMap.values()) {
            if (s.getName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>(studentMap.values());
        list.sort(Comparator.comparing(Student::getStudentId));
        return list;
    }

    public void displayAllStudents() {
        List<Student> students = getAllStudents();
        if (students.isEmpty()) {
            UIConsole.printWarning("No student records found.");
            return;
        }

        UIConsole.printHeader("REGISTERED STUDENTS DIRECTORY");
        System.out.printf("%-10s | %-20s | %-12s | %-8s | %-4s | %-30s%n",
                "ID", "Name", "Roll No", "Dept", "Sem", "Email");
        UIConsole.printDivider();
        for (Student s : students) {
            System.out.printf("%-10s | %-20s | %-12s | %-8s | %-4d | %-30s%n",
                    s.getStudentId(), s.getName(), s.getRollNo(), s.getDepartment(), s.getSemester(), s.getEmail());
        }
        UIConsole.printDivider();
    }

    public void saveChanges() {
        fileRepository.saveStudents(getAllStudents());
    }
}

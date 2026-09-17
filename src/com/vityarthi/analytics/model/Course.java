package com.vityarthi.analytics.model;

import java.io.Serializable;

/**
 * Model class representing a Course / Subject.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseCode;
    private String title;
    private int credits;
    private double maxMarks;

    public Course(String courseCode, String title, int credits, double maxMarks) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.maxMarks = maxMarks;
    }

    public Course(String courseCode, String title, int credits) {
        this(courseCode, title, credits, 100.0);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d Credits, Max Marks: %.0f)", courseCode, title, credits, maxMarks);
    }
}

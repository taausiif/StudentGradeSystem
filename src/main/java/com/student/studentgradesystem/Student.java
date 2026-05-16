package com.student.studentgradesystem;

import java.util.ArrayList;
import java.util.List;

public class Student {

    // These are private — only this class can access them directly
    // This is ENCAPSULATION (OOP Pillar #1)
    private String studentId;
    private String name;
    private String email;
    private List<Grade> grades;

    // Constructor — runs when you create a new Student
    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.grades = new ArrayList<>();
    }

    // GETTERS — let other classes READ the data
    public String getStudentId() { return studentId; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public List<Grade> getGrades() { return grades; }

    // SETTERS — let other classes UPDATE the data
    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    // Add a grade to this student
    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    // Calculate GPA automatically from all grades
    public double getGPA() {
        if (grades.isEmpty()) return 0.0;

        double total = 0;
        for (Grade g : grades) {
            total += g.getScore();
        }
        return total / grades.size();
    }

    // This is used to display the student nicely (e.g. in a table)
    @Override
    public String toString() {
        return studentId + " - " + name;
    }


    // Get all grades for a specific semester
    public java.util.List<Grade> getGradesBySemester(int semester) {
        java.util.List<Grade> result = new java.util.ArrayList<>();
        for (Grade g : grades) {
            if (g.getSemester() == semester) {
                result.add(g);
            }
        }
        return result;
    }

    // Calculate GPA for a specific semester
    public double getSemesterGPA(int semester) {
        java.util.List<Grade> semGrades = getGradesBySemester(semester);
        if (semGrades.isEmpty()) return 0.0;
        double total = 0;
        for (Grade g : semGrades) {
            total += g.getGradePoint();
        }
        return total / semGrades.size();
    }
}
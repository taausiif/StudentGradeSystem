package com.student.studentgradesystem;

import java.util.ArrayList;
import java.util.List;

public class Teacher {

    private String teacherId;
    private String name;
    private String subject;
    private List<String> assignedStudentIds;

    // Constructor
    public Teacher(String teacherId, String name, String subject) {
        this.teacherId = teacherId;
        this.name = name;
        this.subject = subject;
        this.assignedStudentIds = new ArrayList<>();
    }

    // Getters
    public String getTeacherId() { return teacherId; }
    public String getName()      { return name; }
    public String getSubject()   { return subject; }

    // Setters
    public void setName(String name)       { this.name = name; }
    public void setSubject(String subject) { this.subject = subject; }

    // Assign a student to this teacher
    public void assignStudent(String studentId) {
        if (!assignedStudentIds.contains(studentId)) {
            assignedStudentIds.add(studentId);
        }
    }

    // Get how many students this teacher has
    public int getStudentCount() {
        return assignedStudentIds.size();
    }

    @Override
    public String toString() {
        return teacherId + " - " + name + " (" + subject + ")";
    }
}

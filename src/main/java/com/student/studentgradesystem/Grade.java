package com.student.studentgradesystem;

public class Grade {

    private String subject;
    private double score;
    private String teacherName;
    private int semester; // 1, 2, 3, 4, 5, 6, 7, 8

    // Constructor now includes semester
    public Grade(String subject, double score, String teacherName, int semester) {
        this.subject = subject;
        this.score = score;
        this.teacherName = teacherName;
        this.semester = semester;
    }

    // Getters
    public String getSubject()     { return subject; }
    public double getScore()       { return score; }
    public String getTeacherName() { return teacherName; }
    public int getSemester()       { return semester; }

    // Setter
    public void setScore(double score) { this.score = score; }

    // Convert number score to letter grade
    public String getLetterGrade() {
        if (score >= 90)      return "A+";
        else if (score >= 85) return "A";
        else if (score >= 80) return "A-";
        else if (score >= 75) return "B+";
        else if (score >= 70) return "B";
        else if (score >= 65) return "B-";
        else if (score >= 60) return "C+";
        else if (score >= 55) return "C";
        else if (score >= 50) return "D";
        else                  return "F";
    }

    // Check if student passed
    public boolean isPassing() {
        return score >= 50;
    }

    // Grade point for GPA calculation
    public double getGradePoint() {
        if (score >= 90)      return 4.00;
        else if (score >= 85) return 3.75;
        else if (score >= 80) return 3.50;
        else if (score >= 75) return 3.25;
        else if (score >= 70) return 3.00;
        else if (score >= 65) return 2.75;
        else if (score >= 60) return 2.50;
        else if (score >= 55) return 2.25;
        else if (score >= 50) return 2.00;
        else                  return 0.00;
    }

    @Override
    public String toString() {
        return "Semester " + semester + " | " + subject +
                ": " + score + " (" + getLetterGrade() + ")";
    }
}
package com.student.studentgradesystem;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private List<Student> students;
    private List<Teacher> teachers;

    // File will be saved in user's home folder
    private static final String SAVE_FILE =
            System.getProperty("user.home") + "/student_data.txt";

    public DataManager() {
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();

        // Try to load saved data first
        // If no saved data exists, load sample data
        if (!loadFromFile()) {
            loadSampleData();
            saveToFile(); // save sample data immediately
        }
    }

    // ── STUDENT METHODS ──────────────────────────

    public void addStudent(Student student) {
        students.add(student);
        saveToFile(); // auto-save every time data changes
    }

    public void removeStudent(String studentId) {
        students.removeIf(s -> s.getStudentId().equals(studentId));
        saveToFile();
    }

    public Student findStudent(String studentId) {
        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) return s;
        }
        return null;
    }

    public List<Student> getAllStudents() { return students; }

    // ── TEACHER METHODS ──────────────────────────

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        saveToFile();
    }

    public List<Teacher> getAllTeachers() { return teachers; }

    // Call this after adding a grade to a student
    public void saveChanges() {
        saveToFile();
    }

    // ── SAVE TO FILE ──────────────────────────────
    // Format: one line per record
    // TEACHER:id,name,subject
    // STUDENT:id,name,email
    // GRADE:studentId,subject,score,teacherName,semester

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {

            // Save teachers
            for (Teacher t : teachers) {
                writer.write("TEACHER:" +
                        escape(t.getTeacherId()) + "," +
                        escape(t.getName()) + "," +
                        escape(t.getSubject()));
                writer.newLine();
            }

            // Save students
            for (Student s : students) {
                writer.write("STUDENT:" +
                        escape(s.getStudentId()) + "," +
                        escape(s.getName()) + "," +
                        escape(s.getEmail()));
                writer.newLine();

                // Save each grade under this student
                for (Grade g : s.getGrades()) {
                    writer.write("GRADE:" +
                            escape(s.getStudentId()) + "," +
                            escape(g.getSubject()) + "," +
                            g.getScore() + "," +
                            escape(g.getTeacherName()) + "," +
                            g.getSemester());
                    writer.newLine();
                }
            }

            System.out.println("✅ Data saved to: " + SAVE_FILE);

        } catch (IOException e) {
            System.err.println("❌ Could not save data: " + e.getMessage());
        }
    }

    // ── LOAD FROM FILE ────────────────────────────

    private boolean loadFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("TEACHER:")) {
                    // Parse teacher
                    String[] parts = line.substring(8).split(",", 3);
                    if (parts.length == 3) {
                        teachers.add(new Teacher(
                                unescape(parts[0]),
                                unescape(parts[1]),
                                unescape(parts[2])
                        ));
                    }

                } else if (line.startsWith("STUDENT:")) {
                    // Parse student
                    String[] parts = line.substring(8).split(",", 3);
                    if (parts.length == 3) {
                        students.add(new Student(
                                unescape(parts[0]),
                                unescape(parts[1]),
                                unescape(parts[2])
                        ));
                    }

                } else if (line.startsWith("GRADE:")) {
                    // Parse grade and attach to correct student
                    String[] parts = line.substring(6).split(",", 5);
                    if (parts.length == 5) {
                        String studentId = unescape(parts[0]);
                        Student student  = findStudent(studentId);
                        if (student != null) {
                            student.addGrade(new Grade(
                                    unescape(parts[1]),
                                    Double.parseDouble(parts[2]),
                                    unescape(parts[3]),
                                    Integer.parseInt(parts[4])
                            ));
                        }
                    }
                }
            }

            System.out.println("✅ Data loaded from: " + SAVE_FILE);
            return !students.isEmpty() || !teachers.isEmpty();

        } catch (IOException e) {
            System.err.println("❌ Could not load data: " + e.getMessage());
            return false;
        }
    }

    // ── HELPER METHODS ────────────────────────────
    // Escape commas in names so they don't break our CSV format
    private String escape(String value) {
        return value.replace(",", "&#44;");
    }

    private String unescape(String value) {
        return value.replace("&#44;", ",");
    }

    // ── SAMPLE DATA ───────────────────────────────
    private void loadSampleData() {
        Teacher t1 = new Teacher("T001", "Mr. Rahman", "Mathematics");
        Teacher t2 = new Teacher("T002", "Ms. Akter",  "English");
        teachers.add(t1);
        teachers.add(t2);

        Student s1 = new Student("S001", "Rahim Hossain", "rahim@email.com");
        Student s2 = new Student("S002", "Karim Uddin",   "karim@email.com");
        Student s3 = new Student("S003", "Sadia Islam",   "sadia@email.com");

        s1.addGrade(new Grade("Mathematics", 85, "Mr. Rahman", 1));
        s1.addGrade(new Grade("English",     78, "Ms. Akter",  1));
        s1.addGrade(new Grade("Physics",     80, "Mr. Islam",  1));
        s1.addGrade(new Grade("Chemistry",   72, "Mr. Rahman", 2));
        s1.addGrade(new Grade("Biology",     88, "Ms. Akter",  2));

        s2.addGrade(new Grade("Mathematics", 92, "Mr. Rahman", 1));
        s2.addGrade(new Grade("English",     88, "Ms. Akter",  1));
        s2.addGrade(new Grade("Chemistry",   76, "Mr. Rahman", 2));
        s2.addGrade(new Grade("Biology",     95, "Ms. Akter",  2));

        s3.addGrade(new Grade("Mathematics", 74, "Mr. Rahman", 1));
        s3.addGrade(new Grade("English",     95, "Ms. Akter",  1));
        s3.addGrade(new Grade("Physics",     68, "Mr. Islam",  2));
        s3.addGrade(new Grade("Chemistry",   82, "Mr. Rahman", 2));

        students.add(s1);
        students.add(s2);
        students.add(s3);
    }
}
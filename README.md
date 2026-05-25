# 🎓 Student Grade Management System

A desktop application built with **Java + JavaFX** for managing 
student records, grades, and semester reports.

## ✨ Features

- 🔐 Login & Registration with role-based access (Admin / Teacher)
- 👤 Add, delete, and search students
- 📊 Add grades per subject with automatic GPA calculation
- 📅 Semester-wise grade reports with pass/fail status
- 📋 Overall academic summary per student
- 💾 Data persistence — saves to file automatically
- 🎨 Clean and professional JavaFX UI

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| JavaFX | Desktop UI framework |
| Maven | Build tool |
| CSS | UI Styling |
| File I/O | Data persistence |

## 🏗️ Project Structure
src/main/java/com.student.studentgradesystem
├── Launcher.java           — App entry point
├── LoginController.java    — Login + Registration
├── User.java               — User model
├── HelloApplication.java   — Main UI
├── DataManager.java        — Data storage + File I/O
├── Student.java            — Student model
├── Grade.java              — Grade + GPA logic
└── Teacher.java            — Teacher model

## 🎯 OOP Concepts Used

- **Encapsulation** — Private fields with getters/setters
- **Abstraction** — DataManager hides storage details
- **Inheritance** — User class ready to be extended
- **Polymorphism** — Grade methods overridable

## 🚀 How to Run

1. Clone the repository
   git clone https://github.com/taausiif/StudentGradeSystem.git
2. Open in **IntelliJ IDEA**
3. Run `Launcher.java`
4. Login with:
   - Admin: `admin` / `admin123`
   - Teacher: `teacher` / `teacher123`

## 👨‍💻 Author

**Abrar Ahmad Tausif & MD. Mohasin Or Rashid**  
University Project — Object Oriented Programming

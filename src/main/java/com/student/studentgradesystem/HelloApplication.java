package com.student.studentgradesystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private DataManager dataManager = new DataManager();
    private TableView<Student> table = new TableView<>();
    private User loggedInUser;
    private TextField searchField = new TextField();
    private Label statusLabel = new Label("Ready");

    @Override
    public void start(Stage stage) {

        // ── TOP HEADER ────────────────────────────────
        String roleText = loggedInUser != null ?
                "  |  " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")" : "";
        Label title = new Label("Student Grade Management System" + roleText);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        // Subtitle showing logged in user
        roleText = loggedInUser != null ?
                loggedInUser.getUsername() + "  |  " + loggedInUser.getRole() : "Guest";
        Label subtitle = new Label("Logged in as: " + roleText);
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web("#AED6F1"));

        VBox titleBox = new VBox(3, title, subtitle);

// App version label on right
        Label versionLabel = new Label("v1.0");
        versionLabel.setFont(Font.font("Segoe UI", 11));
        versionLabel.setTextFill(Color.web("#AED6F1"));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #1B4F72, #2E86C1);"
        );
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, versionLabel);

        // ── TABLE COLUMNS ─────────────────────────────
        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        idCol.setPrefWidth(120);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        // GPA column needs special handling (it's calculated)
        TableColumn<Student, String> gpaCol = new TableColumn<>("GPA");
        gpaCol.setCellValueFactory(data -> {
            double gpa = data.getValue().getGPA();
            String formatted = String.format("%.2f", gpa);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        gpaCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, nameCol, emailCol, gpaCol);
        table.setStyle("-fx-font-size: 14px;");

        // Load students into table
        refreshTable();

        // ── BUTTONS ───────────────────────────────────
        Button addBtn    = new Button("➕ Add Student");
        Button deleteBtn = new Button("🗑 Delete Student");
        Button gradeBtn  = new Button("📊 View Grades");
        Button addGradeBtn = new Button("✏️ Add Grade");
        Button semesterBtn = new Button("📅 Semester Report");


        // Style buttons
        String btnStyle = "-fx-background-color: #2E86C1; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
        addGradeBtn.setStyle(btnStyle.replace("#2E86C1", "#8E44AD"));
        addBtn.setStyle(btnStyle);
        deleteBtn.setStyle(btnStyle.replace("#2E86C1", "#C0392B"));
        gradeBtn.setStyle(btnStyle.replace("#2E86C1", "#1E8449"));
        semesterBtn.setStyle(btnStyle.replace("#2E86C1", "#6C3483"));


        // Button actions
        addBtn.setOnAction(e -> showAddStudentDialog(stage));
        deleteBtn.setOnAction(e -> deleteSelectedStudent());
        gradeBtn.setOnAction(e -> showGrades());
        addGradeBtn.setOnAction(e -> showAddGradeDialog(stage));
        semesterBtn.setOnAction(e -> showSemesterReport(stage));

        // Search bar
        searchField.setPromptText("🔍 Search student by name...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-font-size: 13px; -fx-padding: 8;");

// Clear search button
        Button clearBtn = new Button("✕");
        clearBtn.setStyle(
                "-fx-background-color: #BDC3C7; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-padding: 8 12 8 12; -fx-cursor: hand;"
        );
        clearBtn.setOnAction(e -> {
            searchField.clear();
            refreshTable();
        });

// Live search as user types
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchStudents(newVal.trim());
        });

        HBox searchBar = new HBox(8, searchField, clearBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        HBox buttons = new HBox(12, addBtn, deleteBtn, gradeBtn, addGradeBtn, semesterBtn);

        VBox toolBar = new VBox(8, buttons, searchBar);
        toolBar.setPadding(new Insets(12, 20, 12, 20));
        toolBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D5E8F3;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // ── MAIN LAYOUT ───────────────────────────────
        // Status bar at the bottom
        statusLabel.setPadding(new Insets(5, 20, 5, 20));
        statusLabel.setStyle(
                "-fx-background-color: #1B4F72;" +
                        "-fx-text-fill: #AED6F1;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 6 20 6 20;"
        );
        statusLabel.setMaxWidth(Double.MAX_VALUE);

// Update status with total students
        statusLabel.setText("Total students: " + dataManager.getAllStudents().size());

        VBox root = new VBox(header, toolBar, table, statusLabel);
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(root, 750, 560);
        String css = getClass().getResource("style.css") != null
                ? getClass().getResource("style.css").toExternalForm()
                : null;
        if (css != null) {
            scene.getStylesheets().add(css);
        }
        stage.setTitle("Student Grade System");
        stage.setScene(scene);
        stage.show();
    }

    // Reload table from DataManager
    private void refreshTable() {
        ObservableList<Student> data =
                FXCollections.observableArrayList(dataManager.getAllStudents());
        table.setItems(data);

        // Update status bar
        if (statusLabel != null) {
            statusLabel.setText("Total students: " + dataManager.getAllStudents().size());
        }

        // Clear search field
        if (searchField != null) {
            searchField.clear();
        }
    }

    // Show dialog to add a new student
    private void showAddStudentDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Student");
        dialog.initOwner(owner);

        TextField idField    = new TextField();
        TextField nameField  = new TextField();
        TextField emailField = new TextField();

        idField.setPromptText("e.g. S004");
        nameField.setPromptText("e.g. John Doe");
        emailField.setPromptText("e.g. john@email.com");

        Button saveBtn = new Button("Save Student");
        saveBtn.setStyle("-fx-background-color: #2E86C1; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-padding: 8 20 8 20;");

        saveBtn.setOnAction(e -> {
            String id    = idField.getText().trim();
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
                showAlert("Please fill in all fields!");
                return;
            }

            dataManager.addStudent(new Student(id, name, email));
            refreshTable();
            dialog.close();
        });

        VBox form = new VBox(10,
                new Label("Student ID:"), idField,
                new Label("Name:"),      nameField,
                new Label("Email:"),     emailField,
                saveBtn
        );
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        dialog.setScene(new Scene(form, 300, 260));
        dialog.show();
    }

    // Delete selected student from table
    private void deleteSelectedStudent() {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student to delete!");
            return;
        }
        dataManager.removeStudent(selected.getStudentId());
        refreshTable();
    }

    // Show grades of selected student
    private void showGrades() {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student first!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Grades for: ").append(selected.getName()).append("\n\n");

        if (selected.getGrades().isEmpty()) {
            sb.append("No grades recorded yet.");
        } else {
            for (Grade g : selected.getGrades()) {
                sb.append("📚 ").append(g.getSubject())
                        .append(":  ").append(g.getScore())
                        .append("  (").append(g.getLetterGrade()).append(")\n");
            }
            sb.append("\n🎯 GPA: ").append(String.format("%.2f", selected.getGPA()));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Grade Report");
        alert.setHeaderText(selected.getName() + "'s Grades");
        alert.setContentText(sb.toString());
        alert.show();
    }

    // Helper to show error messages
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void showAddGradeDialog(Stage owner) {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student first!");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Add Grade for " + selected.getName());
        dialog.initOwner(owner);

        // Input fields
        TextField subjectField = new TextField();
        TextField scoreField   = new TextField();
        TextField teacherField = new TextField();
        ComboBox<Integer> semesterBox = new ComboBox<>();
        semesterBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        semesterBox.setValue(1);
        semesterBox.setMaxWidth(Double.MAX_VALUE);

        subjectField.setPromptText("e.g. Mathematics");
        scoreField.setPromptText("e.g. 85");
        teacherField.setPromptText("e.g. Mr. Rahman");

        Button saveBtn = new Button("Save Grade");
        saveBtn.setStyle("-fx-background-color: #8E44AD; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-padding: 8 20 8 20;");

        Label resultLabel = new Label("");
        resultLabel.setTextFill(Color.GREEN);

        saveBtn.setOnAction(e -> {
            String subject = subjectField.getText().trim();
            String scoreText = scoreField.getText().trim();
            String teacher = teacherField.getText().trim();

            // Validate inputs
            if (subject.isEmpty() || scoreText.isEmpty() || teacher.isEmpty()) {
                showAlert("Please fill in all fields!");
                return;
            }

            // Make sure score is a valid number
            double score;
            try {
                score = Double.parseDouble(scoreText);
                if (score < 0 || score > 100) {
                    showAlert("Score must be between 0 and 100!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Score must be a number (e.g. 85)!");
                return;
            }

            // Add grade to the selected student
            int semester = semesterBox.getValue();
            selected.addGrade(new Grade(subject, score, teacher, semester));

            dataManager.saveChanges(); // save to file immediately

            // Show success message
            resultLabel.setText("✅ Grade added! GPA is now: " +
                    String.format("%.2f", selected.getGPA()));

            // Clear fields for another entry
            subjectField.clear();
            scoreField.clear();
            teacherField.clear();

            // Refresh the table so GPA updates instantly
            refreshTable();
        });

        VBox form = new VBox(10,
                new Label("Subject:"),       subjectField,
                new Label("Score (0-100):"), scoreField,
                new Label("Teacher Name:"),  teacherField,
                new Label("Semester:"),      semesterBox,
                saveBtn,
                resultLabel
        );
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        dialog.setScene(new Scene(form, 300, 300));
        dialog.show();
    }
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    private void searchStudents(String keyword) {
        // If search is empty, show all students
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        // Filter students whose name contains the keyword (case-insensitive)
        ObservableList<Student> filtered = FXCollections.observableArrayList();

        for (Student s : dataManager.getAllStudents()) {
            if (s.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(s);
            }
        }

        table.setItems(filtered);

        // Show how many results found at the bottom
        if (filtered.isEmpty()) {
            statusLabel.setText("No students found matching: \"" + keyword + "\"");
        } else {
            statusLabel.setText("Found " + filtered.size() + " student(s) matching: \"" + keyword + "\"");
        }
    }


    private void showSemesterReport(Stage owner) {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student first!");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Semester Report — " + selected.getName());
        dialog.initOwner(owner);

        // ── HEADER ────────────────────────────────────
        Label headerLabel = new Label("📅  Semester Report");
        headerLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        headerLabel.setTextFill(Color.WHITE);

        Label studentLabel = new Label(selected.getName() + "  |  ID: " + selected.getStudentId());
        studentLabel.setFont(Font.font("Segoe UI", 12));
        studentLabel.setTextFill(Color.web("#AED6F1"));

        VBox headerBox = new VBox(3, headerLabel, studentLabel);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #1B4F72, #2E86C1);");

        // ── SEMESTER TABS ─────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white;");

        boolean hasAnyGrade = false;

        for (int sem = 1; sem <= 8; sem++) {
            java.util.List<Grade> semGrades = selected.getGradesBySemester(sem);
            if (semGrades.isEmpty()) continue;

            hasAnyGrade = true;

            // Create tab for this semester
            Tab tab = new Tab("Semester " + sem);

            // Table for this semester's grades
            TableView<Grade> gradeTable = new TableView<>();
            gradeTable.setStyle("-fx-font-size: 13px;");

            TableColumn<Grade, String> subjectCol = new TableColumn<>("Subject");
            subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
            subjectCol.setPrefWidth(150);

            TableColumn<Grade, Double> scoreCol = new TableColumn<>("Score");
            scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
            scoreCol.setPrefWidth(80);

            TableColumn<Grade, String> letterCol = new TableColumn<>("Grade");
            letterCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(
                            data.getValue().getLetterGrade()));
            letterCol.setPrefWidth(70);

            TableColumn<Grade, String> pointCol = new TableColumn<>("Grade Point");
            pointCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(
                            String.format("%.2f", data.getValue().getGradePoint())));
            pointCol.setPrefWidth(100);

            TableColumn<Grade, String> teacherCol = new TableColumn<>("Teacher");
            teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
            teacherCol.setPrefWidth(130);

            TableColumn<Grade, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data ->
                    new javafx.beans.property.SimpleStringProperty(
                            data.getValue().isPassing() ? "✅ Pass" : "❌ Fail"));
            statusCol.setPrefWidth(80);

            gradeTable.getColumns().addAll(
                    subjectCol, scoreCol, letterCol, pointCol, teacherCol, statusCol);
            gradeTable.setItems(
                    FXCollections.observableArrayList(semGrades));

            // Semester summary footer
            double semGPA   = selected.getSemesterGPA(sem);
            long passCount  = semGrades.stream().filter(Grade::isPassing).count();
            long failCount  = semGrades.size() - passCount;

            Label gpaLabel  = new Label(String.format("Semester GPA:  %.2f", semGPA));
            Label passLabel = new Label("Passed:  " + passCount + "  subjects");
            Label failLabel = new Label("Failed:  " + failCount + "  subjects");

            gpaLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            gpaLabel.setTextFill(Color.web("#1B4F72"));
            passLabel.setTextFill(Color.web("#1E8449"));
            failLabel.setTextFill(failCount > 0 ? Color.RED : Color.GRAY);

            // Color code GPA
            if (semGPA >= 3.5)       gpaLabel.setTextFill(Color.web("#1E8449"));
            else if (semGPA >= 2.5)  gpaLabel.setTextFill(Color.web("#D4AC0D"));
            else                     gpaLabel.setTextFill(Color.RED);

            HBox footer = new HBox(30, gpaLabel, passLabel, failLabel);
            footer.setPadding(new Insets(10, 15, 10, 15));
            footer.setAlignment(Pos.CENTER_LEFT);
            footer.setStyle(
                    "-fx-background-color: #EBF5FB;" +
                            "-fx-border-color: #AED6F1;" +
                            "-fx-border-width: 1 0 0 0;"
            );

            VBox tabContent = new VBox(gradeTable, footer);
            VBox.setVgrow(gradeTable, Priority.ALWAYS);
            tab.setContent(tabContent);
            tabPane.getTabs().add(tab);
        }

        // If no grades at all
        if (!hasAnyGrade) {
            Label noData = new Label("No grades recorded for any semester yet.");
            noData.setFont(Font.font(14));
            noData.setTextFill(Color.GRAY);
            noData.setPadding(new Insets(40));

            VBox root = new VBox(headerBox, noData);
            dialog.setScene(new Scene(root, 680, 200));
            dialog.show();
            return;
        }

        // ── OVERALL SUMMARY TAB ───────────────────────
        Tab summaryTab = new Tab("📊 Overall Summary");
        TableView<String[]> summaryTable = new TableView<>();
        summaryTable.setStyle("-fx-font-size: 13px;");

        TableColumn<String[], String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        semCol.setPrefWidth(100);

        TableColumn<String[], String> subjectsCol = new TableColumn<>("Subjects");
        subjectsCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        subjectsCol.setPrefWidth(80);

        TableColumn<String[], String> gpaCol2 = new TableColumn<>("GPA");
        gpaCol2.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        gpaCol2.setPrefWidth(80);

        TableColumn<String[], String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        resultCol.setPrefWidth(120);

        summaryTable.getColumns().addAll(semCol, subjectsCol, gpaCol2, resultCol);

        ObservableList<String[]> summaryData = FXCollections.observableArrayList();
        for (int sem = 1; sem <= 8; sem++) {
            java.util.List<Grade> semGrades = selected.getGradesBySemester(sem);
            if (semGrades.isEmpty()) continue;
            double gpa = selected.getSemesterGPA(sem);
            String result = gpa >= 2.0 ? "✅ Promoted" : "❌ Failed";
            summaryData.add(new String[]{
                    "Semester " + sem,
                    semGrades.size() + " subjects",
                    String.format("%.2f", gpa),
                    result
            });
        }
        summaryTable.setItems(summaryData);

        // Overall GPA
        double overallGPA = selected.getGPA();
        Label overallLabel = new Label(
                String.format("Overall GPA:  %.2f  |  Total Subjects:  %d",
                        overallGPA, selected.getGrades().size()));
        overallLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        overallLabel.setTextFill(Color.web("#1B4F72"));
        overallLabel.setPadding(new Insets(10, 15, 10, 15));

        HBox overallBox = new HBox(overallLabel);
        overallBox.setStyle(
                "-fx-background-color: #D6EAF8;" +
                        "-fx-border-color: #AED6F1;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        VBox summaryContent = new VBox(summaryTable, overallBox);
        VBox.setVgrow(summaryTable, Priority.ALWAYS);
        summaryTab.setContent(summaryContent);
        tabPane.getTabs().add(summaryTab);

        // ── LAYOUT ────────────────────────────────────
        VBox root = new VBox(headerBox, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 700, 480);
        String css = getClass().getResource("style.css") != null
                ? getClass().getResource("style.css").toExternalForm() : null;
        if (css != null) scene.getStylesheets().add(css);

        dialog.setScene(scene);
        dialog.show();
    }

}

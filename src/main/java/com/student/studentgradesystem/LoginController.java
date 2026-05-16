package com.student.studentgradesystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    private List<User> users = new ArrayList<>();
    private Stage primaryStage;

    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Pre-made accounts — in a real app these would be in a database
        users.add(new User("admin",   "admin123",   "ADMIN"));
        users.add(new User("teacher", "teacher123", "TEACHER"));
    }

    public Scene getLoginScene() {

        // ── HEADER ────────────────────────────────────
        Label appTitle = new Label("Student Grade System");
        appTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        appTitle.setTextFill(Color.WHITE);

        HBox header = new HBox(appTitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #1B4F72;");

        // ── LOGIN FORM ────────────────────────────────
        Label loginTitle = new Label("🔐  Login to Continue");
        loginTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        loginTitle.setTextFill(Color.web("#1B4F72"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font(13));

        Button loginBtn = new Button("Login");
        loginBtn.setStyle(
                "-fx-background-color: #1B4F72; -fx-text-fill: white;" +
                        "-fx-font-size: 14px; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
        );
        Button registerBtn = new Button("Create New Account");
        registerBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #1B4F72;" +
                        "-fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true;"
        );
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // ── HINT LABEL ────────────────────────────────
        Label hintLabel = new Label(
                "💡 Admin: admin / admin123\n" +
                        "💡 Teacher: teacher / teacher123"
        );
        hintLabel.setFont(Font.font(11));
        hintLabel.setTextFill(Color.GRAY);
        hintLabel.setAlignment(Pos.CENTER);

        // ── LOGIN BUTTON ACTION ───────────────────────
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠️ Please enter username and password!");
                return;
            }

            User loggedInUser = authenticate(username, password);

            if (loggedInUser != null) {
                // ✅ Login successful — open main app
                openMainApp(loggedInUser);
            } else {
                // ❌ Wrong credentials
                errorLabel.setText("❌ Wrong username or password!");
                passwordField.clear();
            }
        });

        // Allow pressing Enter key to login
        passwordField.setOnAction(e -> loginBtn.fire());
        registerBtn.setOnAction(e -> showRegisterDialog(primaryStage));

        // ── LAYOUT ────────────────────────────────────
        VBox form = new VBox(12,
                loginTitle,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                errorLabel,
                loginBtn,
                registerBtn,
                hintLabel
        );
        form.setPadding(new Insets(30));
        form.setMaxWidth(320);
        form.setStyle("-fx-background-color: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);");

        StackPane center = new StackPane(form);
        center.setStyle("-fx-background-color: #EBF5FB;");

        VBox root = new VBox(header, center);
        VBox.setVgrow(center, Priority.ALWAYS);

        return new Scene(root, 650, 500);
    }

    // Check username and password against our user list
    private User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }

    // Open the main app after successful login
    private void openMainApp(User user) {
        HelloApplication mainApp = new HelloApplication();
        mainApp.setLoggedInUser(user);
        try {
            mainApp.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            // Show the real error on screen so we can see what's wrong
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Failed to open main app");
            alert.setContentText(e.getMessage() != null ? e.getMessage() : e.toString());
            alert.show();
        }
    }
    private void showRegisterDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Create New Account");
        dialog.initOwner(owner);

        // ── HEADER ────────────────────────────────────
        Label headerLabel = new Label("Create New Account");
        headerLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        headerLabel.setTextFill(Color.WHITE);

        HBox dialogHeader = new HBox(headerLabel);
        dialogHeader.setAlignment(Pos.CENTER);
        dialogHeader.setPadding(new Insets(15));
        dialogHeader.setStyle("-fx-background-color: #1B4F72;");

        // ── FORM FIELDS ───────────────────────────────
        TextField newUsername = new TextField();
        newUsername.setPromptText("Choose a username");
        newUsername.setStyle("-fx-font-size: 13px; -fx-padding: 8;");

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Choose a password");
        newPassword.setStyle("-fx-font-size: 13px; -fx-padding: 8;");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm your password");
        confirmPassword.setStyle("-fx-font-size: 13px; -fx-padding: 8;");

        // Role selector
        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("TEACHER", "ADMIN");
        roleBox.setValue("TEACHER"); // default
        roleBox.setMaxWidth(Double.MAX_VALUE);
        roleBox.setStyle("-fx-font-size: 13px;");

        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font(12));

        Button saveBtn = new Button("Register");
        saveBtn.setStyle(
                "-fx-background-color: #1B4F72; -fx-text-fill: white;" +
                        "-fx-font-size: 14px; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
        );
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        // ── REGISTER BUTTON ACTION ────────────────────
        saveBtn.setOnAction(e -> {
            String username = newUsername.getText().trim();
            String password = newPassword.getText().trim();
            String confirm  = confirmPassword.getText().trim();
            String role     = roleBox.getValue();

            // Validate — all fields must be filled
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("⚠️ Please fill in all fields!");
                return;
            }

            // Username must be at least 3 characters
            if (username.length() < 3) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("⚠️ Username must be at least 3 characters!");
                return;
            }

            // Password must be at least 6 characters
            if (password.length() < 6) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("⚠️ Password must be at least 6 characters!");
                return;
            }

            // Passwords must match
            if (!password.equals(confirm)) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("⚠️ Passwords do not match!");
                confirmPassword.clear();
                return;
            }

            // Username must not already exist
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("⚠️ Username already taken!");
                    return;
                }
            }

            // ✅ All checks passed — create the new user
            users.add(new User(username, password, role));

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("✅ Account created! You can now login.");

            // Clear fields
            newUsername.clear();
            newPassword.clear();
            confirmPassword.clear();

            // Close dialog after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(dialog::close);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        // ── LAYOUT ────────────────────────────────────
        VBox form = new VBox(10,
                new Label("Username:"),         newUsername,
                new Label("Password:"),         newPassword,
                new Label("Confirm Password:"), confirmPassword,
                roleLabel,                      roleBox,
                statusLabel,
                saveBtn
        );
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        VBox root = new VBox(dialogHeader, form);
        dialog.setScene(new Scene(root, 320, 380));
        dialog.show();
    }
}
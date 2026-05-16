package com.student.studentgradesystem;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        LoginController login = new LoginController(stage);
        stage.setScene(login.getLoginScene());
        stage.setTitle("Student Grade System — Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
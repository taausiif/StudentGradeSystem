module com.student.studentgradesystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.student.studentgradesystem to javafx.fxml;
    exports com.student.studentgradesystem;
}
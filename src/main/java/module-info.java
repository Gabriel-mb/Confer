module com.example.app_epi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.app_epi to javafx.fxml;
    opens models to javafx.base;
    exports com.example.app_epi;
}
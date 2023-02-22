package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
import dao.EquipmentsDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Equipment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class EquipmentInputsController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private DatePicker date;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField equipmentNameInput;
    @FXML
    private TextField equipmentIdInput;


    public void onSaveButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }
    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                ((TextField) node).setFocusTraversable(false);
            }
        }
    }
}
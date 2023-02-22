package com.example.app_epi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField employeeId;

    public void onSaveButtonClick(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipmentInputs-view.fxml")); //Diferente dos outros loads
        Parent root = (Parent) loader.load();

        EquipmentInputsController equipmentInputsController = loader.getController();
        equipmentInputsController.setEmployee(employeeId.getText(), nameInput.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void onBackButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
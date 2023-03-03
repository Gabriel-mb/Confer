package com.example.app_epi;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DevolutionController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker dateDevolution;
    @FXML
    private MFXTextField fine;
    @FXML
    private Label idEquipmentLabel;
    @FXML
    private Label equipmentNameLabel;
    @FXML
    private Label dateBorrowedLabel;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private int statusId = -1;


    public void onSaveButtonClick(ActionEvent event) throws IOException {
        if (statusId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor selecione o estado do equipamento");
            alert.showAndWait();
            return;
        }


    }


    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                ((TextField) node).setFocusTraversable(false);
            }
        }

        statusComboBox.getItems().addAll("DEVOLVIDO", "NÃO LOCALIZADO", "DANIFICADO", "ROUBADO");
    }
    public void anchorPane_dragged(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setY(event.getScreenY() - y);
        stage.setX(event.getScreenX() - x);

    }

    public void anchorPane_pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    public void onCloseButtonClick(ActionEvent event) {
        System.exit(0);
    }

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setData(String employee, String idEmployee, String idEquip, String equipment, String dateBorrowed) {
        nameLabel.setText(employee);
        idLabel.setText(idEmployee);
        idEquipmentLabel.setText(idEquip);
        equipmentNameLabel.setText(equipment);
        dateBorrowedLabel.setText(dateBorrowed);
    }

    public void setStatusComboBox (ActionEvent event) {
        String selectedItem = statusComboBox.getValue();
        int selectedValue = -1;

        switch (selectedItem) {
            case "DEVOLVIDO" -> selectedValue = 1;
            case "NÃO LOCALIZADO" -> selectedValue = 2;
            case "DANIFICADO" -> selectedValue = 3;
            case "ROUBADO" -> selectedValue = 4;
        }
        statusId = selectedValue;
    }
}
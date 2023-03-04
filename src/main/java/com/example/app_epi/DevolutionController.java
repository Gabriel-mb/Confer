package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.HistoryDAO;
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
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import models.History;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

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
    private Label supplierLabel;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private int statusId = -1;


    public void onSaveButtonClick(ActionEvent event) throws IOException, SQLException {
        Connection connection = new ConnectionDAO().connect();
        if (statusId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor selecione o estado do equipamento");
            alert.showAndWait();
            return;
        }
        //"DEVOLVIDO" -> 1;
        //"NÃO LOCALIZADO" -> 2;
        //"DANIFICADO" -> 3;
        //"ROUBADO" -> 4;
        switch (statusId) {
            case 1:
                //apenas salvar no histórico e tirar de borrowed, porém não excluir de equipments.
                HistoryDAO historyDAO = new HistoryDAO(connection);
                Integer supId = historyDAO.getSupplierId(supplierLabel.getText());
                History history = new History(parseInt(idEquipmentLabel.getText()), supId, equipmentNameLabel.getText(), parseInt(idLabel.getText()), nameLabel.getText(), Date.valueOf(dateBorrowedLabel.getText()), statusId, Date.valueOf(dateDevolution.getValue()), BigDecimal.valueOf(Float.valueOf(fine.getText())));
                historyDAO.create(history);
                BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
                borrowedDAO.delete(parseInt(idEquipmentLabel.getText()), supId);
                break;
                //casos 2, 3 e 4 excluir de equipments e salvar no history.
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
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
        //formata a data do datepicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        dateDevolution.setConverterSupplier(converterSupplier);

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

    public void setData(String employee, String idEmployee, String idEquip, String equipment, String dateBorrowed, String supplierName) {
        nameLabel.setText(employee);
        idLabel.setText(idEmployee);
        idEquipmentLabel.setText(idEquip);
        equipmentNameLabel.setText(equipment);
        dateBorrowedLabel.setText(dateBorrowed);
        supplierLabel.setText(supplierName);
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
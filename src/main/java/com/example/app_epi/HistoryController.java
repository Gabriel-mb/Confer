package com.example.app_epi;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.History;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class HistoryController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    @FXML
    private TableView<History> table;
    @FXML
    private TableColumn<History, Integer> idColumn;
    @FXML
    private TableColumn<History, String> supplierNameColumn;
    @FXML
    private TableColumn<History, String> nameEquipColumn;
    @FXML
    private TableColumn<History, Integer> idEmployeeColumn;
    @FXML
    private TableColumn<History, String> nameEmployeeColumn;
    @FXML
    private TableColumn<History, Date> dateBorrowedColumn;
    @FXML
    private TableColumn<History, String> statusColumn;
    @FXML
    private TableColumn<History, Date> dateDevolutionColumn;
    @FXML
    private TableColumn<History, BigDecimal> fineColumn;

    private ObservableList<History> historyList;

    // ideia: após o usuário relatar a devolução, adicionar os dados no histórico.
    // Fará uma transferência da table equipments para histórico, apagando de um e passando pra outro dependendo do status da ferramenta(Roubado, Danificado, Não localizado, Devolvido)
    // No caso de devolvido ele fará apenas uma cópia e irá manter o equipment.
    // Problema na supplierId: caso um supplier seja excluído do DB ele pode retornar um erro.
    public void onMenuButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
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

    public void setTableHistory() throws SQLException {
        Connection connection = new ConnectionDAO().connect();
        HistoryDAO historyDAO = new HistoryDAO(connection);
        historyList = FXCollections.observableList(historyDAO.listHistory());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipment"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        nameEquipColumn.setCellValueFactory(new PropertyValueFactory<>("nameEquip"));
        idEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("idEmployee"));
        nameEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("nameEmployee"));
        dateBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDay"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        dateDevolutionColumn.setCellValueFactory(new PropertyValueFactory<>("devolutionDay"));
        fineColumn.setCellValueFactory(new PropertyValueFactory<>("fine"));

        table.setItems(historyList);
    }
}
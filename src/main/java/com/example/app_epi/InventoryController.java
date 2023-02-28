package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EmployeeDAO;
import dao.EquipmentsDAO;
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
import models.Borrowed;
import models.Employee;
import models.Equipment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import static java.lang.Integer.parseInt;


public class InventoryController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private ObservableList<Equipment> equipmentsStatus;
    @FXML
    private TableView<Equipment> table;
    @FXML
    private TableColumn<Equipment, Integer> idColumn;
    @FXML
    private TableColumn<Equipment, String> nameEquipColumn;
    @FXML
    private TableColumn<Equipment, String> statusColumn;
    @FXML
    private TableColumn<Equipment, String> nameEmployeeColumn;
    @FXML
    private TableColumn<Equipment, Date> dateColumn;

    public void onMenuButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setTableEquipments() throws SQLException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        equipmentsStatus = FXCollections.observableList(equipmentsDAO.listEquipmentsStatus());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipment"));
        nameEquipColumn.setCellValueFactory(new PropertyValueFactory<>("nameEquip"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("nameEmployee"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        table.setItems(equipmentsStatus);
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
}
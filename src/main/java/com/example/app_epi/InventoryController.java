package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EquipmentsDAO;
import dao.HistoryDAO;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Borrowed;
import models.Equipment;
import models.Supplier;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

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
    private ObservableList<Supplier> suppliersNames;

    @FXML
    private TableView<Equipment> table;
    @FXML
    private TableColumn<Equipment, Integer> idColumn;
    @FXML
    private TableColumn<Equipment, String> nameEquipColumn;
    @FXML
    private TableColumn<Equipment, String> nameSupplier;
    @FXML
    private TableColumn<Equipment, String> statusColumn;
    @FXML
    private TableColumn<Equipment, String> nameEmployeeColumn;
    @FXML
    private TableColumn<Equipment, Date> dateColumn;
    @FXML
    private ComboBox<Supplier> supplierDropDown;
    @FXML
    private MFXTextField idEquipment;
    @FXML
    private MFXTextField name;


    public void onMenuButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setTableEquipments() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        equipmentsStatus = FXCollections.observableList(equipmentsDAO.listEquipmentsStatus());
        equipmentsStatus.sort(Comparator.comparingInt(Equipment::getIdEquipment));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipment"));
        nameEquipColumn.setCellValueFactory(new PropertyValueFactory<>("nameEquip"));
        nameSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("nameEmployee"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        table.setItems(equipmentsStatus);
    }
    public void setSupplierDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        suppliersNames = FXCollections.observableList(equipmentsDAO.selectSupplier());
        supplierDropDown.setItems(suppliersNames);
    }

    public void onIncludeButtonClick() {
        if (Objects.equals(name.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Insira um nome para a ferramenta!");
            alert.showAndWait();
            return;
        }
        try {
            Connection connection = new ConnectionDAO().connect();
            EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
            equipmentsDAO.create(parseInt(idEquipment.getText()), name.getText(), String.valueOf(supplierDropDown.getValue()));
            setTableEquipments();
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Patrimônio já cadastrado!");
            alert.showAndWait();
        }
    }

    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
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

    public void onCloseButtonClick() {
        System.exit(0);
    }
    public void onRemoveButtonClick() throws SQLException, IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Tem certeza que deseja continuar?");
        alert.setContentText("Esta ação não pode ser desfeita.");
        ButtonType yesButton = new ButtonType("Sim");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton, yesButton);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == yesButton) {
            SelectionModel<Equipment> selectionModel = table.getSelectionModel();
            int selectedIndex = selectionModel.getSelectedIndex();
            Equipment item = equipmentsStatus.get(selectedIndex);

            removeData(item.getIdEquipment(), item.getSupplierName());

            equipmentsStatus.remove(selectedIndex);
            table.setItems(equipmentsStatus);
        }
    }
    public void removeData(Integer idEquip, String supplierName) throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        equipmentsDAO.delete(idEquip,equipmentsDAO.readId(supplierName));
    }
}
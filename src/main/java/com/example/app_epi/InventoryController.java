package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EquipmentsDAO;
import dao.HistoryDAO;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
    MFXTableView<Equipment> table;

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

        MFXTableColumn<Equipment> idColumn = new MFXTableColumn<>("Patrimônio", Comparator.comparing(Equipment::getIdEquipment));
        MFXTableColumn<Equipment> nameEquipColumn = new MFXTableColumn<>("Ferramenta", Comparator.comparing(Equipment::getNameEquip));
        MFXTableColumn<Equipment> nameSupplier = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Equipment::getSupplierName));
        MFXTableColumn<Equipment> statusColumn = new MFXTableColumn<>("Status", Comparator.comparing(Equipment::getStatus));
        MFXTableColumn<Equipment> nameEmployeeColumn = new MFXTableColumn<>("Funcionário");
        MFXTableColumn<Equipment> dateColumn = new MFXTableColumn<>("Data");

        idColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getIdEquipment));
        nameEquipColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getNameEquip));
        nameSupplier.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getSupplierName));
        statusColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getStatus));
        nameEmployeeColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getNameEmployee));
        dateColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getDate));
        nameEquipColumn.setAlignment(Pos.CENTER_LEFT);
        nameEquipColumn.setPrefWidth(330);

        table.getTableColumns().addAll(idColumn, nameEquipColumn, nameSupplier, statusColumn, nameEmployeeColumn, dateColumn);
        table.getFilters().addAll(
                new IntegerFilter<>("Patrimônio", Equipment::getIdEquipment),
                new StringFilter<>("Ferramenta", Equipment::getNameEquip),
                new StringFilter<>("Fornecedor", Equipment::getSupplierName),
                new StringFilter<>("Status", Equipment::getStatus),
                new StringFilter<>("Funcionário", Equipment::getNameEmployee)
        );
        table.setItems(FXCollections.observableList(equipmentsDAO.listEquipmentsStatus()));
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
    private void initialize(URL location, ResourceBundle resources) {
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
            SelectionModel<Equipment> selectionModel = (SelectionModel<Equipment>) table.getSelectionModel();
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
package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EquipmentsDAO;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import models.Equipment;
import models.Supplier;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class InventoryController {
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private ObservableList<Supplier> suppliersNames;

    @FXML
    MFXTableView<Equipment> table;

    @FXML
    private ComboBox<Supplier> supplierDropDown;
    @FXML
    private MFXTextField idEquipment;
    @FXML
    private MFXTextField name;
    @FXML
    private MFXButton minimizeButton;


    public void onMenuButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setTableEquipments() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);

        MFXTableColumn<Equipment> idColumn = new MFXTableColumn<>("Patrimônio", Comparator.comparing(Equipment::getIdEquipment));
        MFXTableColumn<Equipment> nameEquipColumn = new MFXTableColumn<>("Ferramenta");
        MFXTableColumn<Equipment> nameSupplier = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Equipment::getSupplierName));
        MFXTableColumn<Equipment> statusColumn = new MFXTableColumn<>("Status", Comparator.comparing(Equipment::getStatus));
        MFXTableColumn<Equipment> nameEmployeeColumn = new MFXTableColumn<>("Funcionário");
        MFXTableColumn<Equipment> dateColumn = new MFXTableColumn<>("Data");

        idColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getIdEquipment));
        nameEquipColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getNameEquip));
        nameSupplier.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getSupplierName));
        statusColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(models.Equipment::getStatus));
        nameEmployeeColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(item -> Optional.ofNullable(item.getNameEmployee()).orElse(" ")));
        dateColumn.setRowCellFactory(Equipment -> new MFXTableRowCell<>(item -> {
            Date dateValue = item.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateValue != null ? dateFormat.format(dateValue) : " ";
        }));
        nameEquipColumn.setPrefWidth(300);

        table.getTableColumns().addAll(idColumn, nameEquipColumn, nameSupplier, statusColumn, nameEmployeeColumn, dateColumn);
        table.getFilters().addAll(
                new IntegerFilter<>("Patrimônio", Equipment::getIdEquipment),
                new StringFilter<>("Ferramenta", Equipment::getNameEquip),
                new StringFilter<>("Fornecedor", Equipment::getSupplierName),
                new StringFilter<>("Status", Equipment::getStatus),
                new StringFilter<>("Funcionário", Equipment::getNameEmployee)
        );
        table.setItems(equipmentsDAO.listEquipmentsStatus());
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
            table.setItems(equipmentsDAO.listEquipmentsStatus());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Ferramenta inserida com sucesso!");
            alert.showAndWait();
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
            Connection connection = new ConnectionDAO().connect();
            EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
            ObservableList<Equipment> equipmentsStatus = FXCollections.observableArrayList();
            MultipleSelectionModel<Equipment> selectionModel = (MultipleSelectionModel<Equipment>) table.getSelectionModel();
            List<Equipment> selectedItems = selectionModel.getSelectedValues();

            for (Equipment item : selectedItems) {
                removeData(item.getIdEquipment(), item.getSupplierName());
                equipmentsStatus.remove(item);
            }
            table.setItems(equipmentsDAO.listEquipmentsStatus());
        }
    }
    public void removeData(Integer idEquip, String supplierName) throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        equipmentsDAO.delete(idEquip,equipmentsDAO.readId(supplierName));
    }
    @FXML
    public void minimizeClick() {
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
    }
}
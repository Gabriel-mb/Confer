package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EquipmentsDAO;
import dao.StockDAO;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.Equipment;
import models.Stock;
import models.Supplier;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class StockController {
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private ObservableList<Supplier> suppliersNames;


    @FXML
    MFXTableView<Stock> table;

    @FXML
    private ComboBox<Supplier> supplierDropDown;
    @FXML
    private MFXTextField quantity;
    @FXML
    private ComboBox<Stock> equipmentDropDown;
    @FXML
    private MFXButton minimizeButton;
    @FXML
    private TextField filterTextField;



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
        StockDAO stockDAO = new StockDAO(connection);

        MFXTableColumn<Stock> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(Stock::getEquipmentName));
        MFXTableColumn<Stock> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(Stock::getQuantity));
        MFXTableColumn<Stock> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Stock::getSupplierName));

        equipmentName.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Stock::getEquipmentName));
        quantity.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Stock::getQuantity));
        supplierName.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Stock::getSupplierName));

        equipmentName.setPrefWidth(300);
        quantity.setPrefWidth(150);
        supplierName.setPrefWidth(300);


        table.getTableColumns().addAll(equipmentName, quantity, supplierName);
        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", Stock::getEquipmentName),
                new IntegerFilter<>("Quantidade", Stock::getQuantity),
                new StringFilter<>("Fornecedor", Stock::getSupplierName)
        );
        table.setItems(stockDAO.listStock());
    }

    public void setSupplierDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        suppliersNames = FXCollections.observableList(stockDAO.selectSupplier());
        supplierDropDown.setItems(suppliersNames);
    }
    public void setEquipmentDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        ObservableList<Stock> stockNames = FXCollections.observableList(stockDAO.selectNames());
        equipmentDropDown.setItems(stockNames);
        equipmentDropDown.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (stockNames != null) {
                ObservableList<Stock> filteredList = FXCollections.observableArrayList();
                for (Stock stock : stockNames) {
                    if (stock.getEquipmentName().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(stock);
                    }
                }
                equipmentDropDown.setItems(filteredList);
                equipmentDropDown.show();
            }
        });
    }

    public void onIncludeButtonClick() {
        if (Objects.equals(String.valueOf(equipmentDropDown.getValue()), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Insira um nome para a ferramenta!");
            alert.showAndWait();
            return;
        }
        if (Objects.equals(quantity.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Insira uma quantidade valida!");
            alert.showAndWait();
            return;
        }
        try {
            Connection connection = new ConnectionDAO().connect();
            StockDAO stockDAO = new StockDAO(connection);
            stockDAO.createOrUpdateStock(parseInt(quantity.getText()), String.valueOf(equipmentDropDown.getValue()), String.valueOf(supplierDropDown.getValue()));
            table.setItems(stockDAO.listStock());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Equipamento inserido com sucesso!");
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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

        if (result.isPresent() && result.get() == yesButton) {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Quantidade a ser removida");
            inputDialog.setHeaderText(null);
            inputDialog.setContentText("Digite a quantidade a ser removida:");

            Optional<String> quantityResult = inputDialog.showAndWait();

            if (quantityResult.isPresent() && !quantityResult.get().isEmpty()) {
                String inputText = quantityResult.get();
                try {
                    Connection connection = new ConnectionDAO().connect();
                    StockDAO stockDAO = new StockDAO(connection);
                    MultipleSelectionModel<Stock> selectionModel = (MultipleSelectionModel<Stock>) table.getSelectionModel();
                    List<Stock> selectedItems = selectionModel.getSelectedValues();

                    for (Stock item : selectedItems) {
                        stockDAO.decreaseOrDeleteStock(Integer.parseInt(inputText), item.getEquipmentName(), item.getSupplierName());
                    }
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Estoque Alterado!");
                    alert.setHeaderText(null);
                    alert.setContentText("Estoque alterado com sucesso!");
                    alert.showAndWait();
                    table.setItems(stockDAO.listStock());
                } catch (NumberFormatException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erro");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("A quantidade inserida não é válida.");
                    errorAlert.showAndWait();
                }
            }
        }
    }
    @FXML
    public void minimizeClick() {
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
    }
}
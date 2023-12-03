package com.example.app_epi;

import dao.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
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
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import models.Borrowed;
import models.Stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

public class StockEquipInputsController {
    ObservableList<Borrowed> borrowingsList = FXCollections.observableArrayList();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private MFXDatePicker date;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField epiQuantity;
    @FXML
    private ComboBox<Stock> equipmentName;
    @FXML
    private TableView<Borrowed> table;
    @FXML
    private TableColumn<Borrowed, String> nameColumn;
    @FXML
    private TableColumn<Borrowed, Integer> quantityColumn;
    @FXML
    private TableColumn<Borrowed, java.util.Date> dateColumn;
    @FXML
    private TableColumn<Borrowed, String> supplierColumn;
    @FXML
    private MFXButton minimizeButton;
    private Double x;
    private Double y;
    private Boolean confirmation = false;
    @FXML
    private ComboBox<models.Supplier> supplierComboBox;


    public void onSaveButtonClick(ActionEvent event) throws IOException {
        try {
            Connection connection = new ConnectionDAO().connect();
            StockDAO stockDAO = new StockDAO(connection);
            for (Borrowed item : borrowingsList) {
                if (!stockDAO.searchBorrowed(item)) {
                    Stock stock = stockDAO.readByNameAndSupplier(item.getEquipmentName(), item.getSupplierName());
                    int updatedStock = stock.getQuantity() - item.getQuantity();
                    stockDAO.updateStock(item.getEquipmentName(), stockDAO.getSupplierId(item.getSupplierName()), updatedStock);
                    stockDAO.create(new Borrowed(parseInt(idLabel.getText()), item.getEquipmentName(), item.getDate(), item.getSupplierName(), item.getQuantity()));
                } else {
                    if (!stockDAO.checkDate(item)) {
                        Stock stock = stockDAO.readByNameAndSupplier(item.getEquipmentName(), item.getSupplierName());
                        int updatedStock = stock.getQuantity() - item.getQuantity();
                        stockDAO.updateStock(item.getEquipmentName(), stockDAO.getSupplierId(item.getSupplierName()), updatedStock);
                        stockDAO.create(new Borrowed(parseInt(idLabel.getText()), item.getEquipmentName(), item.getDate(), item.getSupplierName(), item.getQuantity()));
                    }
                    else {
                        Stock stock = stockDAO.readByNameAndSupplier(item.getEquipmentName(), item.getSupplierName());
                        int updatedStock = stock.getQuantity() - item.getQuantity();
                        stockDAO.updateStock(item.getEquipmentName(), stockDAO.getSupplierId(item.getSupplierName()), updatedStock);
                        stockDAO.updateBorrowedStock(item.getQuantity(),item.getEquipmentName(), stockDAO.getSupplierId(item.getEquipmentName()), item.getDate());
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Equipamentos inseridos com sucesso!");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("epiCard-view.fxml"));
            Parent root = loader.load();
            StockEquipCardController stockEquipCardController = loader.getController();
            stockEquipCardController.setTableEmployee(idLabel.getText());

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            stage.show();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onIncludeButtonClick() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        String selectedEquipment = String.valueOf(equipmentName.getValue());
        String selectedSupplier = String.valueOf(supplierComboBox.getValue());

        boolean equipmentExists = false;
        if (equipmentName.getValue() == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione um equipamento!");
        } else if (date.getValue() == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione uma data válida!");
        } else if (selectedSupplier == null) {
            showErrorAlert("Erro", "Ocorreu um erro", "Selecione um fornecedor!");
        } else {
            equipmentExists = false;

            Stock stock = stockDAO.readByNameAndSupplier(String.valueOf(equipmentName.getValue()), String.valueOf(supplierComboBox.getValue()));
            if (stock == null) {
                showErrorAlert("Erro", "Estoque vazio!", "Não há estoque deste equipamento.");
                return;
            } else {
                int quantityToBorrow = parseInt(epiQuantity.getText());
                int currentStock = stock.getQuantity();

                if (currentStock < quantityToBorrow) {
                    showErrorAlert("Erro", "Estoque insuficiente", "Não há estoque suficiente para emprestar a quantidade desejada do equipamento.");
                    return;
                }
            }
            for (Borrowed item : borrowingsList) {
                if (item.getEquipmentName().equals(selectedEquipment) && item.getSupplierName().equals(selectedSupplier)) {
                    item.setQuantity(parseInt(epiQuantity.getText()));
                    break;
                }
            }
        }
        if (!equipmentExists) {
            Borrowed newItem = new Borrowed(selectedSupplier, selectedEquipment, Date.valueOf(date.getValue()), parseInt(epiQuantity.getText()));
            borrowingsList.add(newItem);
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        table.setItems(borrowingsList);
    }

    public void onRemoveButtonClick() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        Borrowed selectedBorrowed = table.getSelectionModel().getSelectedItem();

        if (selectedBorrowed != null) {
            // Check if the selected tool is already allocated in stockBorrowed
            if (stockDAO.checkDate(selectedBorrowed)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Confirmação");
                alert.setHeaderText("Tem certeza que deseja continuar?");
                alert.setContentText("Esta ação não pode ser desfeita.");
                ButtonType yesButton = new ButtonType("Sim");
                ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(cancelButton, yesButton);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == yesButton) {
                    stockDAO.remove(selectedBorrowed.getEquipmentName(), stockDAO.getSupplierId(selectedBorrowed.getSupplierName()),selectedBorrowed.getDate());
                    borrowingsList.remove(selectedBorrowed);
                    table.getItems().remove(selectedBorrowed);
                    showSucessAlert("Sucesso", "Ferramenta Removida", "Ferramenta retornada ao estoque.");
                }
            } else {
                borrowingsList.remove(selectedBorrowed);
                table.getItems().remove(selectedBorrowed);
                showSucessAlert("Sucesso", "Ferramenta Removida", "Ferramenta removida com sucesso.");
            }
        }
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }

    public void setTable(ObservableList<Borrowed> list, Boolean confirm) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        table.setItems(borrowingsList);
        confirmation = confirm;

        borrowingsList.addAll(list);
    }

    @FXML
    private void initialize() throws SQLException, IOException {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        setStockItemsDropDown();
        setSupplierDropDown();
        //formata a data do datepicker
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Supplier<StringConverter<LocalDate>> converterSupplier = () -> new LocalDateStringConverter(dateFormatter, null);
        date.setConverterSupplier(converterSupplier);
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

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void setStockItemsDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        ObservableList<Stock> equipmentNames = FXCollections.observableList(stockDAO.selectStock());
        equipmentName.setItems(equipmentNames);
    }

    public void setSupplierDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        StockDAO stockDAO = new StockDAO(connection);
        ObservableList<models.Supplier> suppliersNames = FXCollections.observableList(stockDAO.selectSupplier());
        supplierComboBox.setItems(suppliersNames);
    }

    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void showSucessAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void onBackButtonClick(MouseEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("epiCard-view.fxml"));
        Parent root = loader.load();

        StockEquipCardController stockEquipCardController = loader.getController();
        stockEquipCardController.setTableEmployee(idLabel.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    @FXML
    public void minimizeClick() {
        minimizeButton.setOnAction(e ->
                ((Stage) ((Button) e.getSource()).getScene().getWindow()).setIconified(true)
        );
    }
}
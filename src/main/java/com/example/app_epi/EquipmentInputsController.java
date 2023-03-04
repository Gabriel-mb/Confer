package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EquipmentsDAO;
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

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Borrowed;
import models.Equipment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class EquipmentInputsController {
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
    private TextField equipmentIdInput;
    @FXML
    private ComboBox<String> equipmentName;
    ObservableList<Borrowed> borrowingsList = FXCollections.observableArrayList();
    @FXML
    private TableView<Borrowed> table;
    @FXML
    private TableColumn<Borrowed, String> nameColumn;
    @FXML
    private TableColumn<Borrowed, Integer> idColumn;
    @FXML
    private TableColumn<Borrowed, java.util.Date> dateColumn;
    @FXML
    private TableColumn<Borrowed, String> supplierColumn;
    private Double x;
    private Double y;
    private Boolean confirmation = false;
    private String equipName;
    private String supplierName;



    public void onSaveButtonClick(ActionEvent event) throws IOException {
        int id = 0;
        int index = 0;
        try {
            Connection connection = new ConnectionDAO().connect();
            BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
            borrowingsList.sort(Comparator.comparingInt(Borrowed::getIdEquipment));
            if (!confirmation) {
                for (Borrowed item : borrowingsList) {
                    id = item.getIdEquipment();
                    borrowedDAO.create(new Borrowed(parseInt(idLabel.getText()), item.getIdEquipment(), item.getDate()));
                    index++;
                }
            } else {
                for (Borrowed item : borrowingsList) {
                    if (borrowedDAO.readId(item.getIdEquipment()) == null && confirmation) {
                        borrowedDAO.create(new Borrowed(parseInt(idLabel.getText()), item.getIdEquipment(), item.getDate()));
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Ferramentas inseridas com sucesso!");
            alert.showAndWait();

            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Ferramenta com matrícula: " + id +" já alocada!");
            alert.showAndWait();

            borrowingsList.remove(index);
        }
    }

    public void onSearchButtonClick(ActionEvent event) throws IOException, SQLException {
        if (equipmentIdInput.getText() == "")  {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma matrícula adequada!");
            alert.showAndWait();
            return;
        }
        Connection connection = new ConnectionDAO().connect();
        EquipmentsDAO equipmentsDAO = new EquipmentsDAO(connection);
        List<Equipment> equipmentList = equipmentsDAO.readId(parseInt(equipmentIdInput.getText()));
        System.out.println(equipmentList);
        if (equipmentList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Nenhum equipamento encontrado!");
            alert.showAndWait();
            return;
        } else {
            for (Equipment i : equipmentList) {
                if (equipmentName.getItems().contains(i.getSupplierName() + " - " + i.getName())) return;
                equipmentName.getItems().addAll(i.getSupplierName() + " - " + i.getName());
            }
        }
    }

    public void onIncludeButtonClick(ActionEvent event) throws IOException{
        splitSelection();
        for (Borrowed borrowed : table.getItems()) {
            Integer id = idColumn.getCellData(borrowed);
            String supplier = supplierColumn.getCellData(borrowed);
            if (parseInt(equipmentIdInput.getText()) == id && Objects.equals(supplierName, supplier)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Ocorreu um erro");
                alert.setContentText("Ferramenta já cadastrada!");
                alert.showAndWait();

                equipmentName.getItems().clear();
                equipmentIdInput.setText("");
                date.setValue(null);

                return;
            }
        }
        if(equipmentName.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma matrícula válida");
            alert.showAndWait();
        } else if (date.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor insira uma data válida");
            alert.showAndWait();
        }  else {
            borrowingsList.add(new Borrowed(equipName, parseInt(equipmentIdInput.getText()), Date.valueOf(date.getValue()), supplierName));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipment"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
            table.setItems(borrowingsList);

            equipmentName.getItems().clear();
            equipmentIdInput.setText("");
            date.setValue(null);
        }
    }

    private void splitSelection() {
        String selection = equipmentName.getValue();
        String[] sections = selection.split(" - ");

        supplierName = sections[0];
        equipName = sections[1];
    }

    public void onRemoveButtonClick(ActionEvent event) throws SQLException {
        SelectionModel<Borrowed> selectionModel = table.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();

        removeData(borrowingsList.get(selectedIndex).getIdEquipment());

        borrowingsList.remove(selectedIndex);
        table.setItems(borrowingsList);
    }

    public void removeData(Integer id) throws SQLException {
        Connection connection = new ConnectionDAO().connect();
        BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
        if(borrowedDAO.readId(id) != null) borrowedDAO.delete(id);
    }

    public void setEmployee(String id, String name) {
        idLabel.setText(id);
        nameLabel.setText(name);
    }
    public void setTable(ObservableList<Borrowed> list, Boolean confirm) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, String>("equipmentName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, Integer>("idEquipment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, java.util.Date>("date"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        table.setItems(list);
        confirmation = confirm;

        borrowingsList.addAll(list);
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

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onDevolutionClick(ActionEvent event) throws IOException {
        //remover o dado do borrowed e gerar dado no histórico
        FXMLLoader loader = new FXMLLoader(getClass().getResource("devolution-view.fxml"));
        Parent root = (Parent) loader.load();
        DevolutionController devolutionController = loader.getController();

        SelectionModel<Borrowed> selectionModel = table.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();

        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Por favor selecione um item que quer devolver.");
            alert.showAndWait();
            return;
        }

        Borrowed itemSelected = borrowingsList.get(selectedIndex);
        devolutionController.setData(nameLabel.getText(), idLabel.getText(), String.valueOf(itemSelected.getIdEquipment()), itemSelected.getEquipmentName(), String.valueOf(itemSelected.getDate()), itemSelected.getSupplierName());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}
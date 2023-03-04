package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EmployeeDAO;
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
import models.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class CardController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label employeeId;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField newEmployeeId;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Borrowed> table;
    @FXML
    private TableColumn<Borrowed, String> nameColumn;
    @FXML
    private TableColumn<Borrowed, Integer> idColumn;
    @FXML
    private TableColumn<Borrowed, Date> dateColumn;
    @FXML
    private TableColumn<Borrowed, String> supplierColumn;

    private ObservableList<Borrowed> borrowingsList;

    private Double x;
    private Double y;


    @FXML
    private void initialize() throws SQLException {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                ((TextField) node).setFocusTraversable(false);
            }
        }
    }

    public void onSearchButtonClick(MouseEvent event) throws SQLException, IOException {
        if (newEmployeeId.getText().length() != 8) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Matrícula deve ter 8 digitos!");
            alert.showAndWait();
            return;
        }

        Connection connection = new ConnectionDAO().connect();
        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        Employee employee = employeeDAO.readId(parseInt(newEmployeeId.getText()));

        if (employee == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Nenhum funcionário encontrado!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("card-view.fxml")); //Diferente dos outros loads
        Parent root = (Parent) loader.load();

        CardController cardController = loader.getController();
        cardController.setTableEmployee(newEmployeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onMenuButtonClick(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onDeleteButtonClick(ActionEvent event) throws IOException, SQLException {
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
            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
            employeeDAO.delete(parseInt(employeeId.getText()));

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ficha Deletada");
            alert.setHeaderText(null);
            alert.setContentText("A ficha foi deletada com sucesso!");
            alert.showAndWait();

            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void onRemoveButtonClick(ActionEvent event) throws SQLException {
        SelectionModel<Borrowed> selectionModel = table.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        ObservableList<Borrowed> data = table.getItems();

        Connection connection = new ConnectionDAO().connect();
        BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
        borrowedDAO.delete(data.get(selectedIndex).getIdEquipment());

        data.remove(selectedIndex);
        table.refresh();
    }

    public void setTableEmployee(String id) throws SQLException {
        //Preenche a TableView de ferramentas pesquisando o ID do funcionario na DataBase
        employeeId.setText(id);

        Connection connection = new ConnectionDAO().connect();
        BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
        borrowingsList = FXCollections.observableList(borrowedDAO.listBorrowed(Integer.valueOf(employeeId.getText())));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        table.setItems(borrowingsList);

        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        Employee employee = employeeDAO.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());
    }
    public void onCloseButtonClick(ActionEvent event) {
        System.exit(0);
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
    public void onModifyButtonClick (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipmentInputsModify-view.fxml"));
        Parent root = (Parent) loader.load();
        EquipmentInputsController equipmentInputsController = loader.getController();
        equipmentInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
        equipmentInputsController.setTable(borrowingsList, true);


        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}
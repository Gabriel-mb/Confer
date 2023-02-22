package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Borrowed;
import models.Employee;

import java.io.IOException;
import java.net.URL;
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
    @FXML private TableView<Borrowed> table;
    @FXML private TableColumn<Borrowed, String> nameColumn;
    @FXML private TableColumn<Borrowed, Integer> idColumn;
    @FXML private TableColumn<Borrowed, Date> dateColumn;


    ObservableList<Borrowed> borrowingsList;

    {
        try {
            borrowingsList = FXCollections.observableList(parseBorrowingsList());
            System.out.println(borrowingsList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void initialize() throws SQLException{
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                ((TextField) node).setFocusTraversable(false);
            }
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, String >("equipmentName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, Integer>("idEquipment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Borrowed, Date >("date"));

        table.setItems(borrowingsList);
    }

    private List<Borrowed> parseBorrowingsList() throws SQLException{
        Connection connection = new ConnectionDAO().connect();
        BorrowedDAO borrowedDAO = new BorrowedDAO(connection);

        return borrowedDAO.listBorrowed(parseInt("employeeId.getText()"));
    }



    public void onSearchButtonClick(ActionEvent event) throws SQLException ,IOException {
        if (newEmployeeId.getText().length() != 8){
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
        cardController.setEmployee(String.valueOf(employee.getId()),employee.getName());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void onMenuButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void onDeleteButtonClick(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Tem certeza que deseja continuar?");
        alert.setContentText("Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (((Optional<?>) result).get() == ButtonType.OK){
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    public void setEmployee(String id, String name) throws SQLException {
        employeeId.setText(id);
        nameLabel.setText(name);

    }
}
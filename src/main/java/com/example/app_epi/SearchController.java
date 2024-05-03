package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class SearchController {
    @FXML
    public AnchorPane anchorPane;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField employeeId;
    @FXML
    private MFXButton minimizeButton;
    private Double x = 0.0;
    private Double y = 0.0;


    public void onSearchButtonClick(ActionEvent event) throws SQLException, IOException {

        //como fazer uma condition que exiba mensagem de erro caso id seja menor do que 8 caracteres ou que nenhum funcionario foi encontrado
        //Criar connection no search e fechar apenas quando voltar ao menu/deletar?
        if (employeeId.getText().length() != 8) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Matrícula deve ter 8 digitos!");
            alert.showAndWait();
            return;
        }

        Connection connection = new ConnectionDAO().connect();
        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        Employee employee = employeeDAO.readId(parseInt(employeeId.getText()));

        if (employee == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Nenhum funcionário encontrado!");
            alert.showAndWait();
            return;
        }

        //Envia o id para o cardController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patCard-view.fxml"));
        Parent root = loader.load();
        CardController cardController = loader.getController();
        cardController.setTableEmployee(employeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();

        connection.close();
    }

    public void onCreateButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("create-view.fxml")));
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
                node.setFocusTraversable(false);
            }
        }
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
        employeeId.setOnAction(event -> {
            Connection connection = null;
            try {
                connection = new ConnectionDAO().connect();
                EmployeeDAO employeeDAO = new EmployeeDAO(connection);
                Employee employee = employeeDAO.readId(parseInt(employeeId.getText()));

                if (employee == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Ocorreu um erro");
                    alert.setContentText("Nenhum funcionário encontrado!");
                    alert.showAndWait();
                    return;
                }

                //Envia o id para o cardController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("patCard-view.fxml"));
                Parent root = loader.load();
                CardController cardController = loader.getController();
                cardController.setTableEmployee(employeeId.getText());

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                scene.setFill(Color.TRANSPARENT);
                stage.show();

                connection.close();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onCloseButtonClick() {
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

    public void onInventoryButtonClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patrimony-view.fxml"));
        Parent root = loader.load();
        InventoryController inventoryController = loader.getController();
        inventoryController.setTableEquipments();
        inventoryController.setSupplierDropDown();
        inventoryController.table.autosizeColumnsOnInitialization();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
    public void onStockButtonClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipments-view.fxml"));
        Parent root = loader.load();
        StockController StockController = loader.getController();
        StockController.setTableEquipments();
        StockController.setSupplierDropDown();
        StockController.setEquipmentDropDown();
        StockController.table.autosizeColumnsOnInitialization();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onHistoryButtonClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("history-view.fxml"));
        Parent root = loader.load();
        HistoryController historyController = loader.getController();
        historyController.setTableHistory();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
    public void onEmployeeButtonClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("employee-view.fxml"));
        Parent root = loader.load();
        EmployeeController employeeController = loader.getController();
        employeeController.setTableEmployees();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}

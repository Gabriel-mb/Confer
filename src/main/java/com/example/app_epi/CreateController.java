package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class CreateController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField employeeId;
    @FXML
    private AnchorPane anchorPane;

    public void onSaveButtonClick(ActionEvent event) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipmentInputs-view.fxml")); //Diferente dos outros loads
        Parent root = loader.load();

        //como fazer uma condition que exiba mensagem de erro caso id seja menor do que 8 caracteres ou que nenhum funcionario foi encontrado
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

        //consulta para verificar se ja existe um funcionário cadastrado
        if (employeeDAO.checkIfIdExists(parseInt(employeeId.getText()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Este funcionário já existe!");
            alert.setContentText("Você deve digitar uma matricula valida!");
            alert.showAndWait();
            return;
        } else {
            Employee employee = new Employee(parseInt(employeeId.getText()), nameInput.getText());
            employeeDAO.create(employee);
        }

        EquipmentInputsController equipmentInputsController = loader.getController();
        equipmentInputsController.setEmployee(employeeId.getText(), nameInput.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void onBackButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("search-view.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
}
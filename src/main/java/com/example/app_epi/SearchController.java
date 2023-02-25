package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
    private Button searchButton;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("card-view.fxml"));
        Parent root = (Parent) loader.load();
        CardController cardController = loader.getController();
        cardController.setCardEmployee(employeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        connection.close();
    }

    public void onCreateButtonClick(ActionEvent event) throws IOException {

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("create-view.fxml")));
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
        employeeId.setOnKeyPressed(this::pressEnter);
    }

    public void pressEnter(KeyEvent keyEvent) {
        employeeId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (event.isShiftDown()) {
                    employeeId.appendText(System.getProperty("line.separator"));
                } else {
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
                    try {
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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("card-view.fxml"));
                        Parent root = (Parent) loader.load();
                        CardController cardController = loader.getController();
                        cardController.setCardEmployee(employeeId.getText());

                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
}

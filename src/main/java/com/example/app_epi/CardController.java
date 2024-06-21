package com.example.app_epi;

import dao.BorrowedDAO;
import dao.ConnectionDAO;
import dao.EmployeeDAO;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
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
import models.Borrowed;
import models.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

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
    private MFXTableView<Borrowed> table;
    @FXML
    private MFXButton minimizeButton;
    @FXML
    private MFXDatePicker datePicker;
    @FXML
    private MFXButton resetButton;

    private ObservableList<Borrowed> borrowingsList;
    private ObservableList<Borrowed> filteredItems;

    private Double x;
    private Double y;


    @FXML
    private void initialize() {
        // percorre todos os nós da cena e define o foco como não transversável para os TextFields
        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.setFocusTraversable(false);
            }
        }
        newEmployeeId.setOnAction(event -> {
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
        minimizeButton.setOnAction(e ->
                ( (Stage) ( (Button) e.getSource() ).getScene().getWindow() ).setIconified(true)
        );
        datePicker.setOnAction(event -> onDatePickerSelect());
        resetButton.setOnAction(event -> resetDatePicker());
        filteredItems = FXCollections.observableArrayList();
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("patCard-view.fxml")); //Diferente dos outros loads
        Parent root = loader.load();

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

    public void setTableEmployee(String id) throws SQLException, IOException {
        //Preenche a TableView de ferramentas pesquisando o ID do funcionario na DataBase
        employeeId.setText(id);

        Connection connection = new ConnectionDAO().connect();
        BorrowedDAO borrowedDAO = new BorrowedDAO(connection);
        borrowingsList = FXCollections.observableList(borrowedDAO.listBorrowed(Integer.valueOf(employeeId.getText())));

        MFXTableColumn<Borrowed> idEquipment = new MFXTableColumn<>("Patrimônio", Comparator.comparing(Borrowed::getIdEquipment));
        MFXTableColumn<Borrowed> supplierName = new MFXTableColumn<>("Fornecedor", Comparator.comparing(Borrowed::getSupplierName));
        MFXTableColumn<Borrowed> equipmentName = new MFXTableColumn<>("Ferramenta", Comparator.comparing(Borrowed::getEquipmentName));
        MFXTableColumn<Borrowed> date = new MFXTableColumn<>("Data", Comparator.comparing(Borrowed::getDate));

        idEquipment.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Borrowed::getIdEquipment));
        supplierName.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Borrowed::getSupplierName));
        equipmentName.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Borrowed::getEquipmentName));
        date.setRowCellFactory(Stock -> new MFXTableRowCell<>(models.Borrowed::getDate));

        table.getTableColumns().addAll(idEquipment, supplierName, equipmentName, date);
        table.getFilters().addAll(
                new StringFilter<>("Ferramenta", Borrowed::getEquipmentName),
                new IntegerFilter<>("Patrimônio", Borrowed::getIdEquipment),
                new StringFilter<>("Fornecedor", Borrowed::getSupplierName)
        );
        equipmentName.setPrefWidth(350);
        table.setItems(borrowingsList);

        EmployeeDAO employeeDAO = new EmployeeDAO(connection);
        Employee employee = employeeDAO.readId(parseInt(employeeId.getText()));
        nameLabel.setText(employee.getName());
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
    public void onModifyButtonClick (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patInputsModify-view.fxml"));
        Parent root = loader.load();
        EquipmentInputsController equipmentInputsController = loader.getController();
        equipmentInputsController.setEmployee(employeeId.getText(), nameLabel.getText());
        equipmentInputsController.setTable(borrowingsList, true);


        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onPrintButtonClick () throws JRException, SQLException, IOException {

        ObservableList<Borrowed> filteredItems = table.getTransformableList();

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(filteredItems);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", itemsJRBean);
        parameters.put("employeeName", nameLabel.getText());
        parameters.put("employeeId", parseInt(employeeId.getText()));
        parameters.put("image", ClassLoader.getSystemResourceAsStream("LogoCorel.png"));

        InputStream inputStream = getClass().getResourceAsStream("/CardPrint.jrxml");

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
    }
    public void onEquipButtonClick(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("equipCard-view.fxml"));
        Parent root = loader.load();

        StockEquipCardController stockEquipCardController = loader.getController();
        stockEquipCardController.setTableEmployee(employeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onEpiButtonClick(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("epiCard-view.fxml"));
        Parent root = loader.load();

        EpiCardController epiCardController = loader.getController();
        epiCardController.setTableEmployee(employeeId.getText());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void onDatePickerSelect() {
        LocalDate selectedDate = datePicker.getValue();
        filteredItems.clear(); // Limpa a lista de itens filtrados

        for (Borrowed item : borrowingsList) {
            LocalDate itemDate = item.getDate().toLocalDate();
            if (itemDate.equals(selectedDate)) {
                filteredItems.add(item);
            }
        }

        table.setItems(filteredItems);
    }
    public void resetDatePicker() {
        table.setItems(borrowingsList);
    }
}
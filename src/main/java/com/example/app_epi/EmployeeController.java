package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
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
import models.Employee;
import models.Equipment;
import models.Supplier;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class EmployeeController {
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;

    @FXML
    MFXTableView<Employee> table;

    @FXML
    private MFXTextField idEmployee;
    @FXML
    private MFXTextField employeeName;
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

    public void setTableEmployees() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EmployeeDAO employeeDAO = new EmployeeDAO(connection);

        MFXTableColumn<Employee> idColumn = new MFXTableColumn<>("Matrícula", Comparator.comparing(Employee::getId));
        MFXTableColumn<Employee> nameEmployeeColumn = new MFXTableColumn<>("Funcionário", Comparator.comparing(Employee::getName));

        idColumn.setRowCellFactory(Employee -> new MFXTableRowCell<>(models.Employee::getId));
        nameEmployeeColumn.setRowCellFactory(Employee -> new MFXTableRowCell<>(models.Employee::getName));

        nameEmployeeColumn.setPrefWidth(600);

        table.getTableColumns().addAll(idColumn, nameEmployeeColumn);
        table.getFilters().addAll(
                new StringFilter<>("Funcionário", Employee::getName),
                new IntegerFilter<>("Matrícula", Employee::getId)
        );
        table.setItems(employeeDAO.listEmployees());
    }

    public void onIncludeButtonClick() {
        if (Objects.equals(employeeName.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Insira um nome para o funcionário!");
            alert.showAndWait();
            return;
        }
        try {
            Connection connection = new ConnectionDAO().connect();
            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
            employeeDAO.create(parseInt(idEmployee.getText()), employeeName.getText());
            table.setItems(employeeDAO.listEmployees());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Funcionário cadastrado com sucesso!");
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Funcionário já cadastrado!");
            alert.showAndWait();
        }
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
            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
            ObservableList<Employee> employees = FXCollections.observableArrayList();
            MultipleSelectionModel<Employee> selectionModel = (MultipleSelectionModel<Employee>) table.getSelectionModel();
            List<Employee> selectedItems = selectionModel.getSelectedValues();

            for (Employee item : selectedItems) {
                employeeDAO.delete(item.getId());
                employees.remove(item);
            }
            table.setItems(employeeDAO.listEmployees());
        }
    }
    public void onPrintButtonClick() throws JRException {

        ObservableList<Employee> filteredItems = table.getTransformableList();

        // Converta os itens filtrados em uma fonte de dados do JasperReports
        JRBeanCollectionDataSource filteredItemsJRBean = new JRBeanCollectionDataSource(filteredItems);

        // Parâmetros para o relatório
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", filteredItemsJRBean);

        // Carregue o arquivo do relatório
        InputStream inputStream = getClass().getResourceAsStream("/employeePrint.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        // Compile o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // Preencha o relatório com os parâmetros e a fonte de dados filtrada
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        // Exiba o relatório
        JasperViewer.viewReport(jasperPrint, false);
    }
}
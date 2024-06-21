package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EpiDAO;
import dao.StockDAO;
import io.github.palexdev.materialfx.controls.*;
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
import models.Epi;
import models.Stock;
import models.Supplier;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class EpiStockController {
    @FXML
    private AnchorPane anchorPane;
    private Double x;
    private Double y;
    private ObservableList<Supplier> suppliersNames;


    @FXML
    MFXTableView<Epi> table;
    @FXML
    private MFXTextField quantity;
    @FXML
    private MFXFilterComboBox<Epi> epiDropDown;
    @FXML
    private MFXTextField numCa;
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

    public void setTableEquipments() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EpiDAO epiDAO = new EpiDAO(connection);

        // Obtém a lista de estoque
        ObservableList<Epi> epiList = FXCollections.observableArrayList(epiDAO.listStock());

        // Ordena a lista de estoque pelo nome do equipamento em ordem alfabética
        Collections.sort(epiList, Comparator.comparing(Epi::getEpiName));

        MFXTableColumn<Epi> epiName = new MFXTableColumn<>("Equipamentos", Comparator.comparing(Epi::getEpiName));
        MFXTableColumn<Epi> numCa = new MFXTableColumn<>("C.A", Comparator.comparing(Epi::getNumCa));
        MFXTableColumn<Epi> quantity = new MFXTableColumn<>("Quantidade", Comparator.comparing(Epi::getQuantity));

        epiName.setRowCellFactory(Epi -> new MFXTableRowCell<>(models.Epi::getEpiName));
        numCa.setRowCellFactory(Epi -> new MFXTableRowCell<>(models.Epi::getNumCa));
        quantity.setRowCellFactory(Epi -> new MFXTableRowCell<>(models.Epi::getQuantity));

        epiName.setPrefWidth(500);
        numCa.setPrefWidth(150);
        quantity.setPrefWidth(150);

        table.getTableColumns().addAll(epiName, numCa, quantity);
        table.getFilters().addAll(
                new StringFilter<>("Epi", Epi::getEpiName),
                new IntegerFilter<>("C.A", Epi::getNumCa)
        );
        table.setItems(epiList);
    }

    public void setEpiDropDown() throws SQLException, IOException {
        Connection connection = new ConnectionDAO().connect();
        EpiDAO epiDAO = new EpiDAO(connection);
        ObservableList<Epi> epiNames = FXCollections.observableList(epiDAO.listStock());
        // Usamos um HashSet temporário para armazenar os objetos Stock únicos com base no nome do equipamento
        HashSet<String> uniqueEpiNames = new HashSet<>();
        ObservableList<Epi> uniqueEpisNames = FXCollections.observableArrayList();

        for (Epi epi : epiNames) {
            String epiName = epi.getEpiName();
            if (!uniqueEpiNames.contains(epiName)) {
                uniqueEpiNames.add(epiName);
                uniqueEpisNames.add(epi);
            }
        }

        // Ordena a lista de objetos Stock em ordem alfabética
        uniqueEpisNames.sort((e1, e2) -> e1.getEpiName().compareToIgnoreCase(e2.getEpiName()));

        epiDropDown.setItems(uniqueEpisNames);
    }

    public void onIncludeButtonClick() {
        if (Objects.equals(String.valueOf(epiDropDown.getValue()), "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText("Insira um nome para o Epi!");
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
            EpiDAO epiDAO = new EpiDAO(connection);
            epiDAO.createOrUpdateStock(parseInt(quantity.getText()), epiDropDown.getText(), parseInt(numCa.getText()));
            ObservableList<Epi> epiList = FXCollections.observableArrayList(epiDAO.listStock());
            Collections.sort(epiList, Comparator.comparing(Epi::getEpiName));
            table.setItems(epiList);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Epi inserido com sucesso!");
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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

        if (result.isPresent() && result.get() == yesButton) {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Quantidade a ser removida");
            inputDialog.setHeaderText(null);
            inputDialog.setContentText("Digite a quantidade a ser removida:");

            Optional<String> quantityResult = inputDialog.showAndWait();

            if (quantityResult.isPresent() && !quantityResult.get().isEmpty()) {
                try {
                    Connection connection = new ConnectionDAO().connect();
                    EpiDAO epiDAO = new EpiDAO(connection);
                    MultipleSelectionModel<Epi> selectionModel = (MultipleSelectionModel<Epi>) table.getSelectionModel();
                    List<Epi> selectedItems = selectionModel.getSelectedValues();

                    for (Epi item : selectedItems) {
                        epiDAO.decreaseOrDeleteStock(Integer.parseInt(quantityResult.get()), item.getEpiName(), item.getNumCa());
                    }
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Estoque Alterado!");
                    alert.setHeaderText(null);
                    alert.setContentText("Estoque alterado com sucesso!");
                    alert.showAndWait();
                    ObservableList<Epi> epiList = FXCollections.observableArrayList(epiDAO.listStock());
                    Collections.sort(epiList, Comparator.comparing(Epi::getEpiName));
                    table.setItems(epiList);
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
}
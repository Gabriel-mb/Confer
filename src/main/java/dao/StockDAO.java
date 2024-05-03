package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Borrowed;
import models.Stock;
import models.Supplier;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {
    private final Connection connection;

    public StockDAO(Connection connection) {
        this.connection = connection;
    }

    public Stock readByNameAndSupplier(String equipmentName, String supplierName) throws SQLException, IOException {
        String query = "SELECT * FROM stock WHERE equipmentName = ? AND supplier = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setString(1, equipmentName);
        int supplierId = getSupplierId(supplierName);
        preparedStatement.setInt(2, supplierId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Stock stock = new Stock(resultSet.getInt("quantity"), equipmentName, supplierId);
            preparedStatement.close();
            resultSet.close();
            return stock;
        }
        return null;
    }


    public List<Borrowed> stockListBorrowed(Integer id) throws SQLException {
        String sql = "SELECT stockBorrowed.equipmentName, stockBorrowed.quantity, stockBorrowed.date, sup.name " +
                "FROM stockBorrowed " +
                "INNER JOIN supplier sup ON stockBorrowed.supplierId = sup.supplierId " +
                "WHERE stockBorrowed.employeeId = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<Borrowed> borrowings = new ArrayList<>();

        while (rst.next()) {
            String name = rst.getString(1);
            Integer quantity = rst.getInt(2);
            Date date = rst.getDate(3);
            String supplierName = rst.getString(4);

            borrowings.add(new Borrowed(id, name, date, supplierName, quantity));
        }
        pstm.close();
        rst.close();

        return borrowings;
    }

    public int getSupplierId(String name) throws SQLException {
        String sql = "SELECT supplierId FROM supplier WHERE name = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, name);
        pstm.execute();

        ResultSet rst = pstm.getResultSet();

        if (rst.next()) {
            return rst.getInt(1);
        }
        return 0;
    }

    public void updateStock(String equipmentName, Integer supplierId, Integer quantity) throws IOException {
        String updateQuery = "UPDATE stock SET quantity = ? WHERE equipmentName = ? AND supplier = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setString(2, equipmentName);
            preparedStatement.setInt(3, supplierId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(Borrowed stockBorrowed) {

        String insertQuery = "INSERT INTO stockBorrowed (quantity, equipmentName, supplierId, employeeId, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, stockBorrowed.getQuantity());
            preparedStatement.setString(2, stockBorrowed.getEquipmentName());
            preparedStatement.setInt(3, getSupplierId(stockBorrowed.getSupplierName()));
            preparedStatement.setInt(4, stockBorrowed.getIdEmployee());
            preparedStatement.setDate(5, stockBorrowed.getDate());

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean searchBorrowed(Borrowed selectedBorrowed) {
        String query = "SELECT * FROM stockBorrowed WHERE equipmentName = ? AND supplierId = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, selectedBorrowed.getEquipmentName());
            preparedStatement.setInt(2, getSupplierId(selectedBorrowed.getSupplierName()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public List<String> selectStock() throws SQLException {
        String sql = "SELECT equipmentName FROM stock;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery(sql);
        List<String> stockItems = new ArrayList<>();

        while (rst.next()) {
            String stockItem = rst.getString(1);
            stockItems.add(stockItem);
        }
        pstm.close();
        rst.close();
        return stockItems;
    }

    public List<Supplier> selectSupplier() throws SQLException {

        String sql = "SELECT name FROM supplier;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery(sql);
        List<Supplier> suppliers = new ArrayList<>();

        while (rst.next()) {
            Supplier supplier = new Supplier(rst.getString(1));
            suppliers.add(supplier);
        }
        pstm.close();
        rst.close();
        return suppliers;
    }

    public List<Stock> selectNames() throws SQLException {

        String sql = "SELECT equipmentName FROM stock;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery(sql);
        List<Stock> stockNames = new ArrayList<>();

        while (rst.next()) {
            Stock stock = new Stock(rst.getString(1));
            stockNames.add(stock);
        }
        pstm.close();
        rst.close();
        return stockNames;
    }

    public void remove(String equipmentName, Integer supplierId, Date date) throws SQLException {
        String updateQuery = "UPDATE stock s\n" +
                "JOIN stockBorrowed sb ON s.equipmentName = sb.equipmentName AND s.supplier = sb.supplierId\n" +
                "SET s.quantity = s.quantity + sb.quantity\n" +
                "WHERE sb.equipmentName = ? AND sb.supplierId = ? AND DATE(sb.date) = ?";

        String deleteQuery = "DELETE FROM stockBorrowed WHERE equipmentName = ? AND supplierId = ? AND DATE(date) = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            updateStatement.setString(1, equipmentName);
            updateStatement.setInt(2, supplierId);
            updateStatement.setDate(3, new java.sql.Date(date.getTime()));
            updateStatement.executeUpdate();

            deleteStatement.setString(1, equipmentName);
            deleteStatement.setInt(2, supplierId);
            deleteStatement.setDate(3, new java.sql.Date(date.getTime()));
            deleteStatement.executeUpdate();
        }
    }


    public ObservableList<Stock> listStock() throws SQLException {
        ObservableList<Stock> stockList = FXCollections.observableArrayList();
        String sql = "SELECT stock.equipmentName, stock.quantity, supplier.name AS supplierName " +
                "FROM stock " +
                "INNER JOIN supplier ON stock.supplier = supplier.supplierId";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String equipmentName = rs.getString("equipmentName");
                int quantity = rs.getInt("quantity");
                String supplierName = rs.getString("supplierName");

                Stock stock = new Stock(quantity, equipmentName, supplierName);
                stockList.add(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return stockList;
    }

    public void createOrUpdateStock(Integer quantity, String equipmentName, String supplierName) {

        // Primeiro, verifique se o equipamento já existe no estoque
        String checkIfExistsQuery = "SELECT COUNT(*) FROM stock WHERE equipmentName = ? AND supplier = ?";
        String insertQuery = "INSERT INTO stock (quantity, equipmentName, supplier) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE stock SET quantity = quantity + ? WHERE equipmentName = ? AND supplier = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement checkIfExistsStmt = connection.prepareStatement(checkIfExistsQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            checkIfExistsStmt.setString(1, equipmentName);
            checkIfExistsStmt.setInt(2, getSupplierId(supplierName));

            ResultSet resultSet = checkIfExistsStmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // O equipamento já existe no estoque, atualize a quantidade
                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, equipmentName);
                updateStmt.setInt(3, getSupplierId(supplierName));
                updateStmt.executeUpdate();
            } else {
                // O equipamento não existe no estoque, insira um novo registro
                insertStmt.setInt(1, quantity);
                insertStmt.setString(2, equipmentName);
                insertStmt.setInt(3, getSupplierId(supplierName));
                insertStmt.executeUpdate();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void decreaseOrDeleteStock(Integer quantity, String equipmentName, String supplierName) {

        // Primeiro, verifique se o equipamento já existe no estoque
        String checkIfExistsQuery = "SELECT quantity FROM stock WHERE equipmentName = ? AND supplier = ?";
        String updateQuery = "UPDATE stock SET quantity = quantity - ? WHERE equipmentName = ? AND supplier = ?";
        String deleteQuery = "DELETE FROM stock WHERE equipmentName = ? AND supplier = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement checkIfExistsStmt = connection.prepareStatement(checkIfExistsQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            checkIfExistsStmt.setString(1, equipmentName);
            checkIfExistsStmt.setInt(2, getSupplierId(supplierName));

            ResultSet resultSet = checkIfExistsStmt.executeQuery();

            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                if (quantity >= currentQuantity) {
                    // A quantidade é maior ou igual ao estoque atual, então exclua o item
                    deleteStmt.setString(1, equipmentName);
                    deleteStmt.setInt(2, getSupplierId(supplierName));
                    deleteStmt.executeUpdate();
                } else {
                    // A quantidade é menor que o estoque atual, atualize a quantidade
                    updateStmt.setInt(1, quantity);
                    updateStmt.setString(2, equipmentName);
                    updateStmt.setInt(3, getSupplierId(supplierName));

                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkDate(Borrowed newItem) throws SQLException {
        String query = "SELECT * FROM stockBorrowed WHERE equipmentName = ? AND supplierId = ? AND DATE(date) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newItem.getEquipmentName());
            stmt.setInt(2, getSupplierId(newItem.getSupplierName()));

            // Ajuste para considerar apenas a parte da data
            java.sql.Date sqlDate = new java.sql.Date(newItem.getDate().getTime());
            stmt.setDate(3, sqlDate);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateBorrowedStock(Integer quantity, String equipmentName, Integer supplierId, Date date, Integer employeeId) throws SQLException {
        String updateQuery = "UPDATE stockBorrowed SET quantity = ? WHERE equipmentName = ? AND supplierId = ? AND date = ? AND employeeId = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, equipmentName);
            updateStatement.setInt(3, supplierId);
            updateStatement.setDate(4, date);
            updateStatement.setInt(5, employeeId);
            updateStatement.executeUpdate();
        }
    }
    public ObservableList<Borrowed> employeeListBorrowed(Integer id) throws SQLException {
        String sql = "SELECT stockBorrowed.equipmentName, stockBorrowed.quantity, stockBorrowed.date, sup.name " +
                "FROM stockBorrowed " +
                "INNER JOIN supplier sup ON stockBorrowed.supplierId = sup.supplierId " +
                "WHERE stockBorrowed.employeeId = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        ObservableList<Borrowed> borrowings = FXCollections.observableArrayList();

        while (rst.next()) {
            String name = rst.getString(1);
            Integer quantity = rst.getInt(2);
            Date date = rst.getDate(3);
            String supplierName = rst.getString(4);

            borrowings.add(new Borrowed(id, name, date, supplierName, quantity));
        }
        pstm.close();
        rst.close();

        return borrowings;
    }
}
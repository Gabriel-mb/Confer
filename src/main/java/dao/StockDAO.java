package dao;

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

    public List<Stock> selectStock() throws SQLException {
        String sql = "SELECT equipmentName FROM stock;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery(sql);
        List<Stock> stockItems = new ArrayList<>();

        while (rst.next()) {
            Stock stockItem = new Stock(rst.getString(1));
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

    public Boolean searchStock(String name, Integer supplierId) {
        String query = "SELECT * FROM stock WHERE equipmentName = ? AND supplier = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, supplierId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}

package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Borrowed;
import models.Epi;
import models.Stock;
import models.Supplier;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpiDAO {
    private final Connection connection;

    public EpiDAO(Connection connection) {
        this.connection = connection;
    }


    public void updateStock(String epiName, Integer numCa, Integer quantity) throws IOException {
        String updateQuery = "UPDATE epis SET quantity = ? WHERE epiName = ? AND numCa = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setString(2, epiName);
            preparedStatement.setInt(3, numCa);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void remove(String epiName, Integer numCa, Date date) throws SQLException {
        String updateQuery = "UPDATE epis e\n" +
                "JOIN episBorrowed eb ON e.epiName = eb.epiName AND e.numCa = eb.numCa\n" +
                "SET e.quantity = e.quantity + eb.quantity\n" +
                "WHERE eb.epiName = ? AND eb.numCa = ? AND DATE(eb.date) = ?";

        String deleteQuery = "DELETE FROM episBorrowed WHERE epiName = ? AND numCa = ? AND DATE(date) = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            updateStatement.setString(1, epiName);
            updateStatement.setInt(2, numCa);
            updateStatement.setDate(3, new Date(date.getTime()));
            updateStatement.executeUpdate();

            deleteStatement.setString(1, epiName);
            deleteStatement.setInt(2, numCa);
            deleteStatement.setDate(3, new Date(date.getTime()));
            deleteStatement.executeUpdate();
        }
    }


    public ObservableList<Epi> listStock() throws SQLException {
        ObservableList<Epi> epiList = FXCollections.observableArrayList();
        String sql = "SELECT epiName, numCa, quantity FROM epis;";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String epiName = rs.getString("epiName");
                int numCa = rs.getInt("numCa");
                int quantity = rs.getInt("quantity");

                Epi epi = new Epi(epiName, numCa, quantity);
                epiList.add(epi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return epiList;
    }

    public void createOrUpdateStock(Integer quantity, String epiName, Integer numCa) {

        // Primeiro, verifique se o equipamento já existe no estoque
        String checkIfExistsQuery = "SELECT COUNT(*) FROM epis WHERE epiName = ? AND numCa = ?";
        String insertQuery = "INSERT INTO epis (epiName, numCa, quantity) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE epis SET quantity = quantity + ? WHERE epiName = ? AND numCa = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement checkIfExistsStmt = connection.prepareStatement(checkIfExistsQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            checkIfExistsStmt.setString(1, epiName);
            checkIfExistsStmt.setInt(2, numCa);

            ResultSet resultSet = checkIfExistsStmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // O equipamento já existe no estoque, atualize a quantidade
                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, epiName);
                updateStmt.setInt(3, numCa);
                updateStmt.executeUpdate();
            } else {
                // O equipamento não existe no estoque, insira um novo registro
                insertStmt.setString(1, epiName);
                insertStmt.setInt(2, numCa);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void decreaseOrDeleteStock(Integer quantity, String epiName, Integer numCa) {

        // Primeiro, verifique se o equipamento já existe no estoque
        String checkIfExistsQuery = "SELECT quantity FROM epis WHERE epiName = ? AND numCa = ?";
        String updateQuery = "UPDATE epis SET quantity = quantity - ? WHERE epiName = ? AND numCa = ?";
        String deleteQuery = "DELETE FROM epis WHERE epiName = ? AND numCa = ?";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement checkIfExistsStmt = connection.prepareStatement(checkIfExistsQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            checkIfExistsStmt.setString(1, epiName);
            checkIfExistsStmt.setInt(2, numCa);

            ResultSet resultSet = checkIfExistsStmt.executeQuery();

            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                if (quantity >= currentQuantity) {
                    // A quantidade é maior ou igual ao estoque atual, então exclua o item
                    deleteStmt.setString(1, epiName);
                    deleteStmt.setInt(2, numCa);
                    deleteStmt.executeUpdate();
                } else {
                    // A quantidade é menor que o estoque atual, atualize a quantidade
                    updateStmt.setInt(1, quantity);
                    updateStmt.setString(2, epiName);
                    updateStmt.setInt(3, numCa);

                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Epi searchEpi(Integer cA) throws SQLException {
        // Corrige a consulta SQL, selecionando todas as colunas com '*'
        String query = "SELECT * FROM epis WHERE numCa = ?";

        // Cria o PreparedStatement com a consulta correta
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        // Define o parâmetro da consulta
        preparedStatement.setInt(1, cA);
        // Executa a consulta (sem passar a query aqui novamente)
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Cria um objeto Epi com os dados recuperados do ResultSet
            Epi epi = new Epi(resultSet.getString("epiName"), resultSet.getInt("numCa"), resultSet.getInt("quantity"));
            // Fecha o PreparedStatement e o ResultSet
            preparedStatement.close();
            resultSet.close();
            return epi;
        }

        // Fecha o PreparedStatement e o ResultSet se nenhum resultado for encontrado
        preparedStatement.close();
        resultSet.close();
        return null;
    }


    public ObservableList<Epi> episListBorrowed(Integer id) throws SQLException {
        String sql = "SELECT episBorrowed.epiName, episBorrowed.numCa, episBorrowed.quantity, episBorrowed.date, episBorrowed.employeeId " +
                "FROM episBorrowed WHERE episBorrowed.employeeId = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<Epi> epis = new ArrayList<>();

        while (rst.next()) {
            String epiName = rst.getString(1);
            Integer numCa = rst.getInt(2);
            Integer quantity = rst.getInt(3);
            Date date = rst.getDate(4);
            Integer employeeId = rst.getInt(5);

            epis.add(new Epi(quantity, numCa, epiName, date, id));
        }
        pstm.close();
        rst.close();

        return FXCollections.observableArrayList(epis);
    }

    public void updateBorrowedEpi(Integer quantity, String epiName, Integer numCa, Date date, Integer employeeId) throws SQLException {
        String updateQuery = "UPDATE episBorrowed SET quantity = ? WHERE epiName = ? AND numCa = ? AND date = ? AND employeeId = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, epiName);
            updateStatement.setInt(3, numCa);
            updateStatement.setDate(4, date);
            updateStatement.setInt(5, employeeId);
            updateStatement.executeUpdate();
        }
    }

    public void createBorrowed(Epi epiBorrowed, Integer employeeId) {

        String insertQuery = "INSERT INTO episBorrowed (epiName, numCa, quantity, date, employeeId) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = new ConnectionDAO().connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, epiBorrowed.getEpiName());
            preparedStatement.setInt(2, epiBorrowed.getNumCa());
            preparedStatement.setInt(3, epiBorrowed.getQuantity());
            preparedStatement.setDate(4, epiBorrowed.getDate());
            preparedStatement.setInt(5, employeeId);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkDate(Epi epi) throws SQLException {
        String query = "SELECT * FROM episBorrowed WHERE epiName = ? AND numCa = ? AND DATE(date) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, epi.getEpiName());
            stmt.setInt(2, epi.getNumCa());

            // Ajuste para considerar apenas a parte da data
            java.sql.Date sqlDate = new java.sql.Date(epi.getDate().getTime());
            stmt.setDate(3, sqlDate);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public ObservableList<Epi> episList(Integer id) throws SQLException {
        String sql = "SELECT epis.epiName, epis.numCa, epis.quantity" +
                "FROM epis WHERE epis.numCa = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<Epi> epis = new ArrayList<>();

        while (rst.next()) {
            String epiName = rst.getString(1);
            Integer numCa = rst.getInt(2);
            Integer quantity = rst.getInt(3);

            epis.add(new Epi(epiName, numCa, quantity));
        }
        pstm.close();
        rst.close();

        return FXCollections.observableArrayList(epis);
    }
}
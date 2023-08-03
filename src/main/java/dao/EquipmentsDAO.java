package dao;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Equipment;
import models.Supplier;

public class EquipmentsDAO {
    private final Connection connection;
    public EquipmentsDAO(Connection connection) { this.connection = connection; }

    public void create(Integer id, String name, String supplierName) throws SQLException { // Implementar um verificador para confirmar que a operação foi realizada
        String sql = "INSERT INTO equipments (IDEQUIPMENT, NAME, supplierId) " +
                "SELECT ?, ?, supplierId FROM supplier WHERE name = ?";


        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.setString(2, name);
        pstm.setString(3, supplierName);

        pstm.execute();

        pstm.close();
    }

    public ObservableList<Equipment> listEquipmentsStatus() throws SQLException {
        String sql = "SELECT e.idEquipment AS equipments_idEquipment,\n" +
                "       e.name AS equipmentName,\n" +
                "       s.name AS supplierName,\n" +
                "       emp.name AS employeeName,\n" +
                "       CASE WHEN b.idEquipment IS NOT NULL THEN 'em uso' ELSE 'Armazenado' END AS Status,\n" +
                "       b.date\n" +
                "FROM supplier s\n" +
                "LEFT JOIN equipments e ON e.supplierId = s.supplierId\n" +
                "LEFT JOIN (\n" +
                "    SELECT b1.idEquipment, b1.supplierId, b1.idEmployee, b1.date\n" +
                "    FROM borrowed b1\n" +
                "    WHERE b1.supplierId = b1.supplierId\n" +
                "    ORDER BY b1.date DESC\n" +
                ") b ON e.idEquipment = b.idEquipment AND s.supplierId = b.supplierId\n" +
                "LEFT JOIN employee emp ON emp.idEmployee = b.idEmployee;\n";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        ObservableList<Equipment> equipmentStatus = FXCollections.observableArrayList();

        while (rst.next()) {
            Integer idEquipment = rst.getInt("equipments_idEquipment");
            String nameEquip = rst.getString("equipmentName");
            String supplierName = rst.getString("supplierName");
            String status = rst.getString("status");
            String nameEmployee = rst.getString("employeeName");
            Date date = rst.getDate("date");

            equipmentStatus.add(new Equipment(idEquipment, nameEquip, supplierName, status, nameEmployee, date));
        }

        pstm.close();
        rst.close();
        return equipmentStatus;
    }

    public List<Equipment> readId(Integer id) throws SQLException{
        String sql = "SELECT e.idEquipment, e.name, sup.name AS supplierName " +
                "FROM equipments e " +
                "JOIN supplier sup on e.supplierId = sup.supplierId " +
                "WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        List<Equipment> equipmentList = new ArrayList<>();
        while (rst.next()) {

            Equipment equipment = new Equipment(rst.getInt(1), rst.getString(2), rst.getString(3));
            equipmentList.add(equipment);
        }
        pstm.close();
        rst.close();

        return equipmentList;

    }
    //
    public void updateId(Integer id, Integer newid) throws SQLException{
        verifyId(id);
        verifyId(newid);
        String sql = "UPDATE equipments SET IDEQUIPMENT = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, newid);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void updateName(Integer id, String name) throws SQLException {
        verifyId(id);
        String sql = "UPDATE equipments SET NAME = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, name);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void delete(Integer id) throws SQLException {
        verifyId(id);
        String sql = "DELETE FROM equipments WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        Integer rows = pstm.executeUpdate();

        pstm.close();
    }

    public void verifyId(Integer id) throws SQLException {
        if (id.toString().length() != 8){
            throw new SQLException("O número ID da ferramenta deve ter exatamente 8 caracteres!");
        }
    }

    public List<Supplier> selectSupplier() throws SQLException {

        String sql = "SELECT name FROM supplier;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery(sql);
        List<Supplier> suppliers = new ArrayList<>();

        while (rst.next()){
            Supplier supplier = new Supplier(rst.getString(1));
            suppliers.add(supplier);
        }
        pstm.close();
        rst.close();
        return suppliers;
    }

    public void delete(Integer idEquip, Integer supplierId) throws SQLException {

        String sql = "DELETE FROM equipments WHERE IDEQUIPMENT = ? AND supplierId = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, idEquip);
        pstm.setInt(2, supplierId);

        Integer rows = pstm.executeUpdate();

        pstm.close();
    }

    public Integer readId (String supplierName) throws SQLException {
        String sql = "SELECT supplierId FROM supplier WHERE name = ?;";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, supplierName);

        Integer supplierId = null;
        if (pstm.execute()) {
            ResultSet rst = pstm.getResultSet();
            if (rst.next()) {
               supplierId = rst.getInt(1);
            }
            rst.close();
        }

        pstm.close();
        return supplierId;
    }
}

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Equipment;

public class EquipmentsDAO {
    private final Connection connection;
    public EquipmentsDAO(Connection connection) { this.connection = connection; }

    public void create(Integer id, String name) throws SQLException { // Implementar um verificador para confirmar que a operação foi realizada
        String sql = "INSERT INTO EQUIPMENTS (IDEQUIPMENT, NAME) VALUES (?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.setString(2, name);

        pstm.execute();

        pstm.close();
    }

    public List<Equipment> listEquipmentsStatus() throws SQLException {
        String sql = "SELECT e.idEquipment, e.name, " +
                "CASE WHEN b.idEquipment IS NOT NULL THEN 'em uso' ELSE 'Armazenado' END AS Status, " +
                "emp.name, b.date " +
                "FROM equipments e " +
                "LEFT JOIN borrowed b ON e.idEquipment = b.idEquipment " +
                "LEFT JOIN employee emp ON emp.idEmployee = b.idEmployee";
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery(sql);
        List<Equipment> equipmentStatus = new ArrayList<>();

        while(rst.next()) {
            Integer idEquipment = rst.getInt(1);
            String nameEquip = rst.getString(2);
            String status = rst.getString(3);
            String nameEmployee = rst.getString(4);
            Date date = rst.getDate(5);

            equipmentStatus.add(new Equipment(idEquipment, nameEquip, status, nameEmployee, date));
        }
        stmt.close();
        rst.close();

        return equipmentStatus;
    }
    public List<Equipment> readId(Integer id) throws SQLException{
        String sql = "SELECT e.idEquipment, e.name, sup.name AS supplierName " +
                "FROM EQUIPMENTS e " +
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
        String sql = "UPDATE EQUIPMENTS SET IDEQUIPMENT = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, newid);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void updateName(Integer id, String name) throws SQLException {
        verifyId(id);
        String sql = "UPDATE EQUIPMENTS SET NAME = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, name);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void delete(Integer id) throws SQLException {
        verifyId(id);
        String sql = "DELETE FROM EQUIPMENTS WHERE IDEQUIPMENT = ?";

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
}

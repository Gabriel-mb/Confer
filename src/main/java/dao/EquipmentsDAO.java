package dao;

import java.sql.*;
import models.Equipment;

public class EquipmentsDAO {
    private final Connection connection;
    public EquipmentsDAO(Connection connection) { this.connection = connection; }

    public void create(Equipment equipment) throws SQLException { // Implementar um verificador para confirmar que a operação foi realizada
        String sql = "INSERT INTO EQUIPMENTS (IDEQUIPMENT, NAME) VALUES (?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, equipment.getIdEquipment());
        pstm.setString(2, equipment.getName());

        pstm.execute();

        pstm.close();
    }

    public void readName(String name) throws SQLException {
        String sql = "SELECT * FROM EQUIPMENTS WHERE NAME LIKE ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, "%" + name + "%");

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        while (rst.next()) {
            Equipment equipment = new Equipment(rst.getInt(1),rst.getString(2));
        }

        pstm.close();
        rst.close();
    }

    public void readId(Integer id) throws SQLException{
        verifyId(id);
        String sql = "SELECT * FROM EQUIPMENTS WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        int counter = 0;
        while (rst.next()) {
            Equipment equipment = new Equipment(rst.getInt(1), rst.getString(2));
            counter++;
        }

        pstm.close();
        rst.close();
    }

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

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Borrowed;
import models.Employee;

public class BorrowedDAO {
    private final Connection connection;
    public BorrowedDAO(Connection connection) { this.connection = connection; }

    public void create(Borrowed borrowed) throws SQLException { // Implementar um verificador para confirmar que a operação foi realizada
        String sql = "INSERT INTO BORROWED (IDEMPLOYEE, IDEQUIPMENT, DATE) VALUES (?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, borrowed.getIdEmployee());
        pstm.setInt(2, borrowed.getIdEquipment());
        pstm.setDate(3, borrowed.getDate());

        pstm.execute();

        pstm.close();
    }

    public List<Borrowed> listBorrowed(Integer id) throws SQLException {
        String sql = "SELECT equipments.name, borrowed.idEquipment, borrowed.date, sup.name " +
                "FROM equipments " +
                "INNER JOIN borrowed " +
                "ON equipments.idEquipment = borrowed.idEquipment AND equipments.supplierId = borrowed.supplierId " +
                "JOIN supplier sup ON equipments.supplierId = sup.supplierId WHERE borrowed.idEmployee = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<Borrowed> borrowings = new ArrayList<>();

        while  (rst.next()) {
            String name = rst.getString(1);
            Integer idEquip = rst.getInt(2);
            Date date = rst.getDate(3);
            String supplierName = rst.getString(4);

            borrowings.add(new Borrowed(name,idEquip,date, supplierName));
        }
        pstm.close();
        rst.close();

        return borrowings;
    }

    public void readName(String name) throws SQLException { //FAZER INNER JOIN PARA USAR NOME DO FUNCIONÁRIO!
        String sql = "SELECT * FROM BORROWED WHERE NAME LIKE ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, "%" + name + "%");

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        while (rst.next()) {
            Borrowed borrowed = new Borrowed(rst.getInt(1),rst.getInt(2), rst.getDate(3));
        }

        pstm.close();
        rst.close();
    }

    public Borrowed readId(Integer id) throws SQLException{
        String sql = "SELECT * FROM BORROWED WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        pstm.execute();

        ResultSet rst = pstm.getResultSet();

        if (rst.next()) {
            Borrowed borrowed = new Borrowed(rst.getInt(1), rst.getInt(2), rst.getDate(3));
            pstm.close();
            rst.close();
            return borrowed;
        }

        pstm.close();
        rst.close();
        return null;
    }

    public void updateQuantity(Integer id, Integer quantity) throws SQLException {
        String sql = "UPDATE BORROWED SET QUANTITY = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, quantity);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void delete(Integer idEquip, Integer supplierId) throws SQLException {

        String sql = "DELETE FROM BORROWED WHERE IDEQUIPMENT = ? AND supplierId = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, idEquip);
        pstm.setInt(2, supplierId);

        Integer rows = pstm.executeUpdate();

        pstm.close();
    }



}

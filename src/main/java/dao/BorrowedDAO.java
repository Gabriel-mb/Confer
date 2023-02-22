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
        String sql = "INSERT INTO BORROWED (IDEMPLOYEE, IDEQUIPMENT, QUANTITY) VALUES (?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, borrowed.getIdEmployee());
        pstm.setInt(2, borrowed.getIdEquipment());
        pstm.setInt(3, borrowed.getQuantity());

        pstm.execute();

        pstm.close();
    }

    public List<Borrowed> listBorrowed(Integer id) throws SQLException {
        String sql = "SELECT equipments.name, borrowed.idEquipment, borrowed.date " +
                "FROM equipments " +
                "INNER JOIN borrowed " +
                "ON equipments.idEquipment = borrowed.idEquipment WHERE borrowed.idEmployee = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        List<Borrowed> borrowings = new ArrayList<>();

        while  (rst.next()) {
            String name = rst.getString(1);
            Integer idEquip = rst.getInt(2);
            Date date = rst.getDate(3);

            borrowings.add(new Borrowed(name,idEquip,date));
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
            Borrowed borrowed = new Borrowed(rst.getInt(1),rst.getInt(2), rst.getInt(3));
            System.out.println(borrowed);
        }

        pstm.close();
        rst.close();
    }

    public void readId(Integer id) throws SQLException{
        verifyId(id);
        String sql = "SELECT * FROM BORROWED WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        int counter = 0;
        while (rst.next()) {
            Borrowed borrowed = new Borrowed(rst.getInt(1), rst.getInt(2), rst.getInt(3));
            System.out.println(borrowed);
            counter++;
        }

        if (counter == 0) System.out.println("Nenhum empréstimo encontrado!");

        pstm.close();
        rst.close();
    }

    public void updateQuantity(Integer id, Integer quantity) throws SQLException {
        verifyId(id);
        String sql = "UPDATE BORROWED SET QUANTITY = ? WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, quantity);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void delete(Integer id) throws SQLException {
        verifyId(id);
        String sql = "DELETE FROM BORROWED WHERE IDEQUIPMENT = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        Integer rows = pstm.executeUpdate();

        if (rows == 0) {
            System.out.println("Não existe nenhuma ferramenta com esse ID: " + id);
        } else System.out.println("Ferramenta com ID : " + id + " foi excluída com sucesso!");

        pstm.close();
    }

    public void verifyId(Integer id) throws SQLException {
        if (id.toString().length() != 8){
            throw new SQLException("O número ID do empréstimo deve ter exatamente 8 caracteres!");
        }
    }

}

package dao;

import models.Borrowed;
import models.History;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    private final Connection connection;
    public HistoryDAO(Connection connection) { this.connection = connection; }

    public void create(History history) throws SQLException {
        String sql = "INSERT INTO HISTORY (idEquipment, supplierId, nameEquip, nameEmployee, borrowedDay, statusId, devolutionDay, fine ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, history.getIdEquipment());
        pstm.setInt(2, history.getSupplierId());
        pstm.setString(3, history.getNameEquip());
        pstm.setString(4, history.getNameEmployee());
        pstm.setDate(5, history.getBorrowedDay());
        pstm.setInt(6, history.getStatusId());
        pstm.setDate(7, history.getDevolutionDay());
        pstm.setBigDecimal(8, history.getFine());


        pstm.execute();

        pstm.close();
    }

    public List<History> listHistory() throws SQLException {
        String sql = "SELECT h.idEquipment, sup.name AS supplierName, h.nameEquip, h.idEmployee, h.nameEmployee, h.borrowedDay, s.name AS statusName, h.devolutionDay, h.fine " +
                "FROM history h " +
                "JOIN status s ON h.statusId = s.statusId " +
                "JOIN supplier sup ON h.supplierId = sup.supplierId";
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery(sql);

        List<History> historyList = new ArrayList<>();

        while  (rst.next()) {
            Integer idEquipment = rst.getInt(1);
            String supplierName = rst.getString(2);
            String nameEquip = rst.getString(3);
            Integer idEmployee = rst.getInt(4);
            String nameEmployee = rst.getString(5);
            java.sql.Date borrowedDay = rst.getDate(6);
            String statusName = rst.getString(7);
            java.sql.Date devolutionDay = rst.getDate(8);
            BigDecimal fine = rst.getBigDecimal(9);

            historyList.add(new History(idEquipment, supplierName, nameEquip, idEmployee, nameEmployee, borrowedDay, statusName, devolutionDay, fine));
        }
        stmt.close();
        rst.close();

        return historyList;
    }
}

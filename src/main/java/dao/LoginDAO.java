package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {
    private final Connection connection;
    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean verification (String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username='" + username + "' && password='" + password+ "'";
        PreparedStatement pstm = connection.prepareStatement(sql);

        try (ResultSet rs = pstm.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return true; // Retorna verdadeiro se encontrou alguma correspondência
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

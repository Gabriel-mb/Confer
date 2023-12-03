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

    public boolean verification(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, username);
            pstm.setString(2, password);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next(); // Retorna verdadeiro se encontrou alguma correspondência
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

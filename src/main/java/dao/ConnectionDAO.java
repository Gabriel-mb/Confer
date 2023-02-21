package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDAO {
    public Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/app_epi", "root", "LWR507705!lwr");
    }
}

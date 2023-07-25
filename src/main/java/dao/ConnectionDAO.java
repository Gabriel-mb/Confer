package dao;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDAO {
    public Connection connect() throws SQLException, IOException {
        Properties properties = new Properties();
        InputStream inStream = getClass().getResourceAsStream("/db.prop");
        properties.load(inStream);

        String dbUrl = properties.getProperty("db.url");
        String dbUsername = properties.getProperty("db.username");
        String dbPassword = properties.getProperty("db.password");

        return DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
    }
}

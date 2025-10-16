package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            String url = "jdbc:sqlserver://MAYTINHCUABON:1433;databaseName=JoyLeeBook_v2;encrypt=true;trustServerCertificate=true;";
            String username = "sa";
            String password = "admin123";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, username, password);
        }catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
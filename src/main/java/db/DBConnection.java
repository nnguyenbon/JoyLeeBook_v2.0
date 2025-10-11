package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection is a utility class that provides a method to establish a connection to the database.
 * It uses JDBC to connect to a SQL Server database with the specified URL, username, and password.
 * <p>
 * The connection parameters are hardcoded for simplicity but should be externalized in a real application.
 * <p>
 * Example usage:
 * <pre>
 *     Connection conn = DBConnection.getConnection();
 * </pre>
 * <p>
 * Make sure to handle SQLExceptions and ClassNotFoundExceptions when calling getConnection().
 * <p>
 * Note: This class currently returns null if the connection fails, which should be handled appropriately by the caller.
 *
 * @author HaiDD-dev
 */
public class DBConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=JoyLeeBook_V2;encrypt=true;trustServerCertificate=true;";
            String username = "sa";
            String password = "";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
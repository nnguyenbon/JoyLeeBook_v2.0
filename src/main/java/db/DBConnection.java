package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection is a utility class that provides a method to establish
 * a connection to the SQL Server database.
 * <p>
 * It uses JDBC to connect to the database with the specified URL,
 * username, and password.
 * <p>
 * Make sure to include the SQL Server JDBC driver in your project dependencies.
 * <p>
 * Example usage:
 * <pre>
 *     Connection conn = DBConnection.getConnection();
 * </pre>
 * <p>
 * Note: Handle SQLExceptions and ClassNotFoundExceptions appropriately
 * when calling the getConnection method.
 *
 * @author HaiDD-dev
 */
public class DBConnection {

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=JoyLeeBook_V2;encrypt=true;trustServerCertificate=true;";
            String username = "sa";
            String password = "dOhAI5%lqs#tppqrp";
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
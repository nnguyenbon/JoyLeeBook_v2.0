package services.account;

import dao.*;
import db.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountServices {
    private final UserDAO userDAO;
    private final StaffDAO staffDAO;
    private final ReadingHistoryDAO readingHistoryDAO;

    public AccountServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.staffDAO = new StaffDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.readingHistoryDAO = new ReadingHistoryDAO(connection);
    }


}

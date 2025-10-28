package services.general;

import dao.BadgeDAO;
import dao.BadgesUserDAO;
import db.DBConnection;
import model.Badge;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BadgesServices {
    private final Connection connection;
    private final BadgeDAO badgeDAO;
    private BadgesUserDAO badgesUserDAO;
    public BadgesServices() throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
        this.badgesUserDAO = new BadgesUserDAO(connection);
        this.badgeDAO = new BadgeDAO(connection);
    }

    public List<Badge> badgeListFromUser(int userId) throws SQLException, ClassNotFoundException {

        return badgesUserDAO.getBadgesByUserId(userId);
    }

    public List<Badge> getAllBadges() throws SQLException, ClassNotFoundException {

        return badgeDAO.getAll();
    }
}

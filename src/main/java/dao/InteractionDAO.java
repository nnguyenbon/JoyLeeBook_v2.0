package dao;

import model.staff.InteractionStats;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionDAO {
    private Connection conn;

    public InteractionDAO(Connection conn) {
        this.conn = conn;
    }

    public InteractionStats getTotalInteractions() throws SQLException {
        InteractionStats stats = new InteractionStats();

        // Count likes
        String likesSQL = "SELECT COUNT(*) FROM likes";
        try (PreparedStatement ps = conn.prepareStatement(likesSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setLikes(rs.getInt(1));
            }
        }

        // Count comments
        String commentsSQL = "SELECT COUNT(*) FROM comments WHERE is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(commentsSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setComments(rs.getInt(1));
            }
        }

        // Count ratings (votes)
        String votesSQL = "SELECT COUNT(*) FROM ratings";
        try (PreparedStatement ps = conn.prepareStatement(votesSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setVotes(rs.getInt(1));
            }
        }



        // Count saved series
        String savesSQL = "SELECT COUNT(*) FROM saved_series";
        try (PreparedStatement ps = conn.prepareStatement(savesSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setSaves(rs.getInt(1));
            }
        }

        // Calculate total
        stats.setTotalInteractions(
                stats.getLikes() + stats.getComments() + stats.getVotes() + stats.getSaves()
        );

        return stats;
    }
}
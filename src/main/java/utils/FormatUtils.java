package utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    public static String calculateTimeAgo(LocalDateTime updatedAt) {
        Duration duration = Duration.between(updatedAt, LocalDateTime.now());
        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "just now";
        }
    }

    public static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String formatString(String status) {
        return  Character.toUpperCase(status.charAt(0)) + status.substring(1);
    }

    public static String getAction (String createdAt, String updatedAt){
        return createdAt.equals(updatedAt) ? "Add Chapter" : "Edit Chapter";
    }
}

package utils;


public class ValidationInput {
    public static boolean isPositiveInteger(String input){
        if (input != null && !input.isEmpty()) {
            return Integer.parseInt(input) >= 0;
        } return false;
    }

    public static boolean isEmptyString(String input){
        if (input != null && !input.isEmpty()) {
            return !input.trim().isEmpty();
        } return false;
    }

    public static boolean isValidCharacters(String name) {
        String regex = "^[A-Za-z0-9 ]+$";
        return name.matches(regex);
    }

    public static boolean isValidLength(String input, int limit){
        if (input != null && !input.isEmpty()) {
            return input.length() <= limit;
        }
        return true;
    }

    public static boolean isValidEmailFormat(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    public static boolean hasValidDomain(String email) {
        String[] allowedDomains = {".com", ".vn", ".edu.vn", ".org", ".net"};
        for (String domain : allowedDomains) {
            if (email.toLowerCase().endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLowercase(String password) {
        return password.matches(".*[a-z].*");
    }

    public static boolean hasUppercase(String password) {
        return password.matches(".*[A-Z].*");
    }

    public static boolean hasNumber(String password) {
        return password.matches(".*[0-9].*");
    }

    public static boolean hasSpecialCharacter(String password) {
        return password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>/?`~|].*");
    }
}

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
}

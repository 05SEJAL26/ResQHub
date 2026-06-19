public class InputValidator {

    public static boolean isValidQuantity(String input) {
        try {
            int val = Integer.parseInt(input.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidName(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static String normalize(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase();
    }

    public static boolean resourceMatchesSearch(String resourceKey, String searchTerm) {
        return normalize(resourceKey).contains(normalize(searchTerm));
    }
}


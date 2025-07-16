package toDoPac;

public enum Priority {
    HIGH, MEDIUM, LOW;

    public static Priority fromString(String s) {
        return switch (s.toLowerCase()) {
            case "high" -> HIGH;
            case "medium" -> MEDIUM;
            case "low" -> LOW;
            default -> MEDIUM;
        };
    }
}

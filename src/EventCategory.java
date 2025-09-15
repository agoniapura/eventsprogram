import java.util.Arrays;
import java.util.stream.Collectors;

public enum EventCategory {
    PARTY, SPORT, SHOW, FAIR, CONFERENCE, CULTURE, OTHER;

    public static EventCategory fromString(String s) {
        if (s == null || s.isEmpty()) return OTHER;
        try { return EventCategory.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return OTHER; }
    }

    public static String listValues() {
        return Arrays.stream(EventCategory.values()).map(Enum::name).collect(Collectors.joining(", "));
    }
}

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Event {
    private final String id;
    private final String name;
    private final String address;
    private final EventCategory category;
    private final LocalDateTime dateTime;
    private final String description;
    private final Set<String> participants = new HashSet<>();

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public Event(String id, String name, String address, EventCategory category, LocalDateTime dateTime, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public EventCategory getCategory() { return category; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDescription() { return description; }
    public Set<String> getParticipants() { return Collections.unmodifiableSet(participants); }

    public boolean addParticipant(String userId) { return participants.add(userId); }
    public boolean removeParticipant(String userId) { return participants.remove(userId); }

    public boolean isOccurringNow(LocalDateTime now) {
        LocalDateTime start = this.dateTime;
        LocalDateTime end = this.dateTime.plusHours(3);
        return (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end);
    }

    public String summary() {
        return String.format("[%s] %s - %s (%s) - %s - participants=%d", id, name, dateTime.format(dtf), category, address, participants.size());
    }

    public String summaryWithStatus(String userId) {
        String base = summary();
        String status = participants.contains(userId) ? " [JOINED]" : "";
        if (isOccurringNow(LocalDateTime.now())) base += " [ONGOING]";
        return base + status;
    }

    public String toDataLine() {
        String part = participants.stream().collect(Collectors.joining(","));
        return String.join("|", id, escapePipe(name), escapePipe(address), category.name(), dateTime.format(dtf), escapePipe(description), part);
    }

    private static String escapePipe(String s) { return s == null ? "" : s.replace("|", "/|"); }
    private static String unescapePipe(String s) { return s == null ? "" : s.replace("/|", "|"); }

    public static Event fromDataLine(String line) {
        String[] parts = line.split("\|", -1);
        String id = parts[0];
        String name = unescapePipe(parts[1]);
        String address = unescapePipe(parts[2]);
        EventCategory cat = EventCategory.fromString(parts[3]);
        LocalDateTime dt = LocalDateTime.parse(parts[4], dtf);
        String desc = unescapePipe(parts[5]);
        Event e = new Event(id, name, address, cat, dt, desc);
        if (!parts[6].trim().isEmpty()) {
            for (String pid : parts[6].split(",", -1)) {
                String t = pid.trim();
                if (!t.isEmpty()) e.addParticipant(t);
            }
        }
        return e;
    }
}

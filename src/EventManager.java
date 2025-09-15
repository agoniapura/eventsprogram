import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventManager {
    private final List<Event> events = new ArrayList<>();
    private final String dataFilePath;

    public EventManager(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public void addEvent(Event e) { events.add(e); }

    public List<Event> getAllEventsSorted() {
        return events.stream().sorted(Comparator.comparing(Event::getDateTime)).collect(Collectors.toList());
    }

    public boolean registerParticipation(String eventId, User user) {
        Event e = findById(eventId);
        if (e == null) return false;
        return e.addParticipant(user.getId());
    }

    public boolean cancelParticipation(String eventId, User user) {
        Event e = findById(eventId);
        if (e == null) return false;
        return e.removeParticipant(user.getId());
    }

    public List<Event> getEventsByParticipant(String userId) {
        return events.stream().filter(ev -> ev.getParticipants().contains(userId))
                .sorted(Comparator.comparing(Event::getDateTime)).collect(Collectors.toList());
    }

    public Event findById(String id) {
        return events.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream().filter(e -> e.getDateTime().isAfter(now))
                .sorted(Comparator.comparing(Event::getDateTime)).collect(Collectors.toList());
    }

    public List<Event> getCurrentEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream().filter(e -> e.isOccurringNow(now))
                .sorted(Comparator.comparing(Event::getDateTime)).collect(Collectors.toList());
    }

    public List<Event> getPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream().filter(e -> e.getDateTime().isBefore(now))
                .sorted(Comparator.comparing(Event::getDateTime).reversed()).collect(Collectors.toList());
    }

    public void loadFromFile() throws IOException {
        File f = new File(dataFilePath);
        if (!f.exists()) { f.createNewFile(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try { events.add(Event.fromDataLine(line)); }
                catch (Exception ex) { System.out.println("Error reading line: " + ex.getMessage()); }
            }
        }
    }

    public void saveToFile() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFilePath, false))) {
            for (Event e : events) {
                bw.write(e.toDataLine());
                bw.newLine();
            }
        }
    }
}

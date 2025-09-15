import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUI {
    private final EventManager manager;
    private final Scanner scanner;
    private final NotificationService notifier;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private User currentUser = null;

    public ConsoleUI(EventManager manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
        this.notifier = new NotificationService();
    }

    public void start() {
        System.out.println("=== City Event System ===");
        registerUser();

        boolean running = true;
        while (running) {
            showMainMenu();
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": createEvent(); break;
                case "2": listAllEvents(); break;
                case "3": joinEvent(); break;
                case "4": viewMyEvents(); break;
                case "5": cancelParticipation(); break;
                case "6": viewUpcomingCurrentPast(); break;
                case "7": saveAndExit(); running = false; break;
                default: System.out.println("Invalid option."); break;
            }
        }
    }

    private void registerUser() {
        System.out.println("Register to use the system.");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        String id = UUID.randomUUID().toString();
        currentUser = new User(id, name, email, phone);
        System.out.println("User registered with ID: " + id);
        notifier.notifyUser(currentUser, "Welcome, " + name + "!");
    }

    private void showMainMenu() {
        System.out.println("\nMenu:");
        System.out.println("1 - Create event");
        System.out.println("2 - List all events");
        System.out.println("3 - Join an event");
        System.out.println("4 - View my events");
        System.out.println("5 - Cancel participation");
        System.out.println("6 - View upcoming / ongoing / past events");
        System.out.println("7 - Save and exit");
        System.out.print("Choice: ");
    }

    private void createEvent() {
        try {
            System.out.print("Event name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Address: ");
            String address = scanner.nextLine().trim();
            System.out.println("Categories: " + EventCategory.listValues());
            System.out.print("Category: ");
            String catStr = scanner.nextLine().trim().toUpperCase();
            EventCategory category = EventCategory.fromString(catStr);
            System.out.print("Date and time (yyyy-MM-dd'T'HH:mm): ");
            String dtStr = scanner.nextLine().trim();
            LocalDateTime dateTime = LocalDateTime.parse(dtStr, dtf);
            System.out.print("Description: ");
            String desc = scanner.nextLine().trim();

            Event e = new Event(UUID.randomUUID().toString(), name, address, category, dateTime, desc);
            manager.addEvent(e);
            System.out.println("Event created with id: " + e.getId());
        } catch (Exception ex) {
            System.out.println("Error creating event: " + ex.getMessage());
        }
    }

    private void listAllEvents() {
        List<Event> events = manager.getAllEventsSorted();
        if (events.isEmpty()) {
            System.out.println("No events registered.");
            return;
        }
        System.out.println("\nRegistered events (sorted by time):");
        for (Event e : events) {
            System.out.println(e.summaryWithStatus(currentUser.getId()));
        }
    }

    private void joinEvent() {
        System.out.print("Enter the ID of the event you want to join: ");
        String id = scanner.nextLine().trim();
        try {
            boolean ok = manager.registerParticipation(id, currentUser);
            if (ok) {
                notifier.notifyUser(currentUser, "You joined the event " + id);
                System.out.println("Participation confirmed.");
            } else {
                System.out.println("Already joined or event not found.");
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void viewMyEvents() {
        List<Event> my = manager.getEventsByParticipant(currentUser.getId());
        if (my.isEmpty()) {
            System.out.println("You have not joined any event.");
            return;
        }
        System.out.println("Events you joined:");
        for (Event e : my) {
            System.out.println(e.summary());
        }
    }

    private void cancelParticipation() {
        System.out.print("Enter the ID of the event to cancel: ");
        String id = scanner.nextLine().trim();
        boolean ok = manager.cancelParticipation(id, currentUser);
        if (ok) {
            notifier.notifyUser(currentUser, "You canceled participation in event " + id);
            System.out.println("Participation canceled.");
        } else {
            System.out.println("Event not found or you were not participating.");
        }
    }

    private void viewUpcomingCurrentPast() {
        System.out.println("\nUpcoming events:");
        for (Event e : manager.getUpcomingEvents()) {
            System.out.println(e.summaryWithStatus(currentUser.getId()));
        }
        System.out.println("\nOngoing events:");
        for (Event e : manager.getCurrentEvents()) {
            System.out.println(e.summaryWithStatus(currentUser.getId()));
        }
        System.out.println("\nPast events:");
        for (Event e : manager.getPastEvents()) {
            System.out.println(e.summaryWithStatus(currentUser.getId()));
        }
    }

    private void saveAndExit() {
        try {
            manager.saveToFile();
            System.out.println("Data saved. Goodbye!");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
}

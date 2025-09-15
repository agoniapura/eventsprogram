import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        EventManager eventManager = new EventManager("events.data");
        try {
            eventManager.loadFromFile();
        } catch (IOException e) {
            System.out.println("Could not load events.data: " + e.getMessage());
        }

        ConsoleUI ui = new ConsoleUI(eventManager);
        ui.start();

        try {
            eventManager.saveToFile();
            System.out.println("Events saved to events.data.");
        } catch (IOException e) {
            System.out.println("Error saving events.data: " + e.getMessage());
        }
    }
}

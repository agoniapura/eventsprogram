public class NotificationService {
    public void notifyUser(User u, String message) {
        System.out.println("[NOTIF] To " + u.getName() + ": " + message);
    }
}

package securitymanagement;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class sms {
    private static final Scanner input = new Scanner(System.in);
    private static UserStore store;
    private static AuditLog audit;

    public static void main(String[] args) {
        try {
            store = new UserStore();
            audit = new AuditLog();
            System.out.println("====================================");
            System.out.println("   SECURITY MANAGEMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("First-run login: admin / admin123");
            System.out.println("Change this demo password after signing in.");
            loginLoop();
        } catch (IOException e) {
            System.out.println("Could not open project data files: " + e.getMessage());
        } finally {
            input.close();
        }
    }

    private static void loginLoop() throws IOException {
        while (true) {
            System.out.println(" 1. Login   0. Exit");
            int choice = readInt("Choose: ");
            if (choice == 0) return;
            if (choice != 1) { System.out.println("Choose 1 or 0."); continue; }

            String username = readLine("Username: ");
            String password = readLine("Password: ");
            User user = store.authenticate(username, password);
            if (user == null) {
                System.out.println("Login failed: invalid username or password.");
                audit.record(username, "FAILED LOGIN");
                continue;
            }
            audit.record(user.getUsername(), "SUCCESSFUL LOGIN");
            System.out.println("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
            if (user.getRole().equals("ADMIN")) adminMenu(user);
            else if (user.getRole().equals("OFFICER")) officerMenu(user);
            else userMenu(user);
        }
    }

    private static void adminMenu(User current) throws IOException {
        boolean active = true;
        while (active) {
            System.out.println("\\--- ADMIN DASHBOARD ---");
            System.out.println("1. List users");
            System.out.println("2. Add user");
            System.out.println("3. Delete user");
            System.out.println("4. View audit log");
            System.out.println("5. Run security check");
            System.out.println("6. Run monitoring threads");
            System.out.println("7. Deadlock prevention demo");
            System.out.println("0. Logout");
            switch (readInt("Choose: ")) {
                case 1 -> listUsers();
                case 2 -> addUser(current);
                case 3 -> deleteUser(current);
                case 4 -> viewAudit();
                case 5 -> {
                    new AdminSecurityModule().performSecurityCheck();
                    audit.record(current.getUsername(), "RAN SECURITY CHECK");
                }
                case 6 -> runMonitors(current);
                case 7 -> {
                    try { DeadlockDemo.runDemo(); }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Demo interrupted.");
                    }
                    audit.record(current.getUsername(), "RAN DEADLOCK PREVENTION DEMO");
                }
                case 0 -> active = false;
                default -> System.out.println("Invalid option.");
            }
        }
        audit.record(current.getUsername(), "LOGOUT");
    }

    private static void officerMenu(User current) throws IOException {
        System.out.println("\\--- OFFICER DASHBOARD ---");
        System.out.println("1. View registered users\\n2. Run security check\\n0. Logout");
        while (true) {
            int c = readInt("Choose: ");
            if (c == 0) break;
            if (c == 1) listUsers();
            else if (c == 2) new AdminSecurityModule().performSecurityCheck();
            else System.out.println("Invalid option.");
        }
        audit.record(current.getUsername(), "LOGOUT");
    }

    private static void userMenu(User current) throws IOException {
        System.out.println("\\--- USER DASHBOARD ---");
        System.out.println("Signed in as: " + current.getUsername());
        System.out.println("Your role: " + current.getRole());
        System.out.println("0. Logout");
        while (readInt("Choose: ") != 0) System.out.println("Choose 0 to logout.");
        audit.record(current.getUsername(), "LOGOUT");
    }

    private static void listUsers() {
        List<User> users = store.allUsers();
        System.out.println("\\--- REGISTERED ACCOUNTS (" + users.size() + ") ---");
        System.out.printf("%-22s %-12s%n", "USERNAME", "ROLE");
        System.out.println("----------------------------------");
        for (User u : users) System.out.printf("%-22s %-12s%n", u.getUsername(), u.getRole());
    }

    private static void addUser(User current) throws IOException {
        String username = readLine("New username: ");
        String password = readLine("New password (minimum 8 characters): ");
        String role = readLine("Role (ADMIN/OFFICER/USER): ").toUpperCase();
        try {
            boolean added = store.addUser(username, password, role);
            if (added) {
                System.out.println("Account created successfully.");
                audit.record(current.getUsername(), "CREATED " + role + " ACCOUNT: " + username);
            } else System.out.println("That username already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create account: " + e.getMessage());
        }
    }

    private static void deleteUser(User current) throws IOException {
        String username = readLine("Username to delete: ");
        if (username.equalsIgnoreCase(current.getUsername())) {
            System.out.println("You cannot delete the account currently in use.");
            return;
        }
        boolean deleted = store.deleteUser(username);
        if (deleted) {
            System.out.println("Account deleted.");
            audit.record(current.getUsername(), "DELETED ACCOUNT: " + username);
        } else System.out.println("Account not found, or it is the last administrator account.");
    }

    private static void viewAudit() throws IOException {
        System.out.println("--- AUDIT LOG ---");
        for (String line : audit.readAll()) System.out.println(line);
    }

    private static void runMonitors(User current) {
        System.out.println("--- BACKGROUND MONITORING ---");
        Thread security = new SecurityMonitor();
        Thread login = new LoginMonitor();
        security.start();
        login.start();
        try {
            security.join();
            login.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Monitoring interrupted.");
        }
        audit.record(current.getUsername(), "RAN MONITORING THREADS");
    }

    private static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("Enter a valid whole number."); }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }
}

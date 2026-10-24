package securitymanagement;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class UserStore {
    private final Path file = Paths.get("data", "users.db");
    private final Map<String, User> users = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public UserStore() throws IOException {
        Files.createDirectories(file.getParent());
        if (Files.exists(file)) {
            for (String line : Files.readAllLines(file)) {
                if (!line.isBlank()) {
                    try {
                        User u = User.fromStorageLine(line);
                        users.put(u.getUsername(), u);
                    } catch (RuntimeException ex) {
                        System.err.println("Skipping an invalid user record in users.db.");
                    }
                }
            }
        }
        if (users.isEmpty()) {
            addUser("admin", "admin123", "ADMIN");
        }
    }

    public synchronized User authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.authenticate(password) ? user : null;
    }

    public synchronized boolean addUser(String username, String password, String role) throws IOException {
        username = username.trim();
        if (username.isEmpty() || username.contains("|") || username.length() > 40) {
            throw new IllegalArgumentException("Username must be 1–40 characters and cannot contain |.");
        }
        if (password.length() < 8) throw new IllegalArgumentException("Password must contain at least 8 characters.");
        if (!role.equals("ADMIN") && !role.equals("OFFICER") && !role.equals("USER")) {
            throw new IllegalArgumentException("Role must be ADMIN, OFFICER, or USER.");
        }
        if (users.containsKey(username)) return false;
        String salt = PasswordUtil.newSalt();
        users.put(username, new User(username, role, salt, PasswordUtil.hash(password, salt)));
        save();
        return true;
    }

    public synchronized boolean deleteUser(String username) throws IOException {
        User u = users.get(username);
        if (u == null || u.getRole().equals("ADMIN") && countAdmins() <= 1) return false;
        users.remove(username);
        save();
        return true;
    }

    public synchronized int countAdmins() {
        return (int) users.values().stream().filter(u -> u.getRole().equals("ADMIN")).count();
    }

    public synchronized List<User> allUsers() { return new ArrayList<>(users.values()); }

    private void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (User u : users.values()) lines.add(u.storageLine());
        Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

package securitymanagement;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditLog {
    private final Path file = Paths.get("data", "audit.log");
    public AuditLog() throws IOException { Files.createDirectories(file.getParent()); }

    public synchronized void record(String actor, String action) {
        String safeActor = actor.replaceAll("[\\\\r\\\\n]", " ");
        String safeAction = action.replaceAll("[\\\\r\\\\n]", " ");
        String entry = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + " | " + safeActor + " | " + safeAction + System.lineSeparator();
        try {
            Files.writeString(file, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Warning: audit log could not be saved.");
        }
    }

    public List<String> readAll() throws IOException {
        return Files.exists(file) ? Files.readAllLines(file) : List.of("No audit events recorded yet.");
    }
}

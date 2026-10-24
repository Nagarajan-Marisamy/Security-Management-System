package securitymanagement;

public class User {
    private final String username;
    private final String role;
    private final String salt;
    private final String passwordHash;

    public User(String username, String role, String salt, String passwordHash) {
        this.username = username;
        this.role = role;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }

    public boolean authenticate(String password) {
        return PasswordUtil.verify(password, salt, passwordHash);
    }

    public String storageLine() {
        return encode(username) + "|" + encode(role) + "|" + salt + "|" + passwordHash;
    }

    public static User fromStorageLine(String line) {
        String[] p = line.split("\\\\|", -1);
        if (p.length != 4) throw new IllegalArgumentException("Invalid user record");
        return new User(decode(p[0]), decode(p[1]), p[2], p[3]);
    }

    private static String encode(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    private static String decode(String value) {
        return new String(java.util.Base64.getDecoder().decode(value), java.nio.charset.StandardCharsets.UTF_8);
    }
}

package securitymanagement;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private PasswordUtil() {}

    public static String newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String password, String saltText) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltText);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Could not process password securely.", e);
        }
    }

    public static boolean verify(String password, String salt, String expectedHash) {
        return java.security.MessageDigest.isEqual(
                hash(password, salt).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                expectedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}

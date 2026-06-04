package logic.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author akyer
 */
public class PasswordHasher {
    private static final int WORK_FACTOR = 12;
    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        boolean result;
        try {
            result = BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        return result;
    }

}

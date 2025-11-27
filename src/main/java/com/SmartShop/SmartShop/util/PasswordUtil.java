package com.SmartShop.SmartShop.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Le hash en base de données n'est pas au format BCrypt");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
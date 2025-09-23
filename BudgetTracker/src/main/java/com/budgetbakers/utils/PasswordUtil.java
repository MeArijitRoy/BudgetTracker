package com.budgetbakers.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
	
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPasswordFromDB) {
        return BCrypt.checkpw(plainTextPassword, hashedPasswordFromDB);
    }
}

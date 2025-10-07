package com.budgetbakers.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A utility class for handling password hashing and verification using the BCrypt algorithm.
 * This class provides static methods to securely hash plain-text passwords and
 * to check if a plain-text password matches a previously generated hash.
 */
public class PasswordUtil {
	
    /**
     * Hashes a plain-text password using the BCrypt algorithm.
     * A salt is automatically generated and included in the resulting hash string.
     *
     * @param plainTextPassword The plain-text password to hash.
     * @return A String containing the BCrypt hash of the password.
     */
    public static String hashPassword(String plainTextPassword) {
        // BCrypt.gensalt(12) generates a salt with a work factor of 12, which is a secure standard.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainTextPassword The plain-text password provided by the user during login.
     * @param hashedPasswordFromDB The hashed password retrieved from the database for that user.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPasswordFromDB) {
        // The BCrypt.checkpw method automatically extracts the salt from the stored hash
        // and performs a secure comparison.
        return BCrypt.checkpw(plainTextPassword, hashedPasswordFromDB);
    }
}


package com.budgetbakers.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.budgetbakers.entities.User;
import com.budgetbakers.utils.DbConnector;
import com.budgetbakers.utils.PasswordUtil;

/**
 * Service class for handling all business logic related to user management.
 * This includes user authentication, creation, password verification, and management.
 */
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);

	/**
	 * Finds a user in the database by their email address.
	 * @param email The email address to search for.
	 * @return A {@link User} object if a user with the given email is found, otherwise null.
	 */
	public User findUserByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setEmail(rs.getString("email"));
					user.setPassword(rs.getString("password"));
					user.setTempPassword(rs.getString("temp_password"));
					user.setTemp(rs.getBoolean("is_temp"));
					user.setAuthProvider(rs.getString("auth_provider"));
					return user;
				}
			}
		} catch (SQLException e) {
			logger.error("Database error while finding user by email: {}", email, e);
		}
		return null;
	}

	/**
	 * Creates a new user with local authentication. This involves generating a temporary
	 * password and sending it to the user's email.
	 * @param email The email address of the new user.
	 * @return The generated temporary password, or null if creation fails.
	 */
	public String createNewUser(String email) {
		String tempPassword = UUID.randomUUID().toString().substring(0, 8);
		String sql = "INSERT INTO users (email, temp_password, is_temp, auth_provider) VALUES (?, ?, TRUE, 'LOCAL')";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			stmt.setString(2, tempPassword);
			stmt.executeUpdate();
			
			logger.info("New user created successfully. Sending temporary password to {}", email);
			EmailService emailService = new EmailService();
			emailService.sendTemporaryPassword(email, tempPassword);
			return tempPassword;
		} catch (SQLException e) {
			logger.error("Database error while creating new user for email: {}", email, e);
		}
		return null;
	}

	/**
	 * Verifies if the provided temporary password matches the one stored for the user.
	 * @param email The user's email address.
	 * @param tempPassword The temporary password to verify.
	 * @return true if the password is correct and the user is in a temporary state, false otherwise.
	 */
	public boolean verifyTempPassword(String email, String tempPassword) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && user.isTemp() && tempPassword.equals(user.getTempPassword());
		return isValid;
	}

	/**
	 * Sets a new permanent password for a user, hashing it before storage.
	 * This action also clears the temporary password and marks the user as no longer temporary.
	 * @param email The user's email address.
	 * @param newPassword The new plain-text password to be set.
	 */
	public void setPermanentPassword(String email, String newPassword) {
		String sql = "UPDATE users SET password = ?, temp_password = NULL, is_temp = FALSE WHERE email = ?";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, PasswordUtil.hashPassword(newPassword));
			stmt.setString(2, email);
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Database error while setting permanent password for {}:", email, e);
		}
	}

	/**
	 * Verifies a user's permanent password against the stored hash.
	 * This only works for users with 'LOCAL' authentication.
	 * @param email The user's email address.
	 * @param password The plain-text password to verify.
	 * @return true if the password is correct, false otherwise.
	 */
	public boolean verifyPassword(String email, String password) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && !user.isTemp() && "LOCAL".equals(user.getAuthProvider())
				&& PasswordUtil.checkPassword(password, user.getPassword());
		return isValid;
	}

	/**
	 * Finds a user by their Google email. If the user does not exist, a new user
	 * record is created with 'GOOGLE' as the authentication provider.
	 * @param email The email address obtained from Google Sign-In.
	 * @return The existing or newly created {@link User} object, or null on failure.
	 */
	public User findOrCreateUserFromGoogle(String email) {
		User existingUser = findUserByEmail(email);

		if (existingUser != null) {
			// Logic to link a LOCAL account to Google would go here if needed.
			return existingUser;
		}

		String sql = "INSERT INTO users (email, auth_provider, is_temp) VALUES (?, 'GOOGLE', FALSE)";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, email);
			int affectedRows = stmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						User newUser = new User();
						newUser.setId(generatedKeys.getInt(1));
						newUser.setEmail(email);
						newUser.setAuthProvider("GOOGLE");
						logger.info("Successfully created new Google user with email {}", email);
						return newUser;
					}
				}
			}
		} catch (SQLException e) {
			logger.error("Database error while creating new Google user for {}:", email, e);
		}
		return null;
	}
}


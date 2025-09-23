package com.budgetbakers.services;

import com.budgetbakers.entities.User;
import com.budgetbakers.utils.DbConnector;
import com.budgetbakers.utils.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);

	public User findUserByEmail(String email) {
		logger.info("Attempting to find user by email: {}", email);
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					logger.info("User found: {}", email);
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setEmail(rs.getString("email"));
					user.setPassword(rs.getString("password"));
					user.setTempPassword(rs.getString("temp_password"));
					user.setTemp(rs.getBoolean("is_temp"));
					user.setAuthProvider(rs.getString("auth_provider"));
					logger.info("Retuning user object for user with email: {}", email);
					return user;
				} else {
					logger.info("No user found for email: {}", email);
				}
			}
		} catch (SQLException e) {
			logger.error("Database error while finding user by email: {}", email, e);
		}
		return null;
	}

	public String createNewUser(String email) {
		logger.info("Attempting to create new local user for email: {}", email);
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

	public boolean verifyTempPassword(String email, String tempPassword) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && user.isTemp() && tempPassword.equals(user.getTempPassword());
		logger.info("Temporary password verification for {}: {}", email, isValid ? "Success" : "Failure");
		return isValid;
	}

	public void setPermanentPassword(String email, String newPassword) {
		logger.info("Setting permanent password for user: {}", email);
		String sql = "UPDATE users SET password = ?, temp_password = NULL, is_temp = FALSE WHERE email = ?";
		try (Connection conn = DbConnector.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, PasswordUtil.hashPassword(newPassword));
			stmt.setString(2, email);
			stmt.executeUpdate();
			logger.info("Permanent password set successfully for {}", email);
		} catch (SQLException e) {
			logger.error("Database error while setting permanent password for {}:", email, e);
		}
	}

	public boolean verifyPassword(String email, String password) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && !user.isTemp() && "LOCAL".equals(user.getAuthProvider())
				&& PasswordUtil.checkPassword(password, user.getPassword());
		logger.info("Permanent password verification for {}: {}", email, isValid ? "Success" : "Failure");
		return isValid;
	}

	public User findOrCreateUserFromGoogle(String email) {
		logger.info("Attempting to find or create user from Google for email: {}", email);
		User existingUser = findUserByEmail(email);

		if (existingUser != null) {
			logger.info("Existing user found for Google sign-in: {}", email);
			return existingUser;
		}

		logger.info("No existing user found. Creating new Google user for: {}", email);
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
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

	public boolean verifyTempPassword(String email, String tempPassword) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && user.isTemp() && tempPassword.equals(user.getTempPassword());
		return isValid;
	}

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

	public boolean verifyPassword(String email, String password) {
		User user = findUserByEmail(email);
		boolean isValid = user != null && !user.isTemp() && "LOCAL".equals(user.getAuthProvider())
				&& PasswordUtil.checkPassword(password, user.getPassword());
		return isValid;
	}

	public User findOrCreateUserFromGoogle(String email) {
		User existingUser = findUserByEmail(email);

		if (existingUser != null) {
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
package com.budgetbakers.entities;

import java.sql.Timestamp;

/**
 * Represents a single user of the application.
 * This class models the `users` table in the database and stores essential
 * information for authentication and user identification.
 */
public class User {

    /** The unique identifier for the user. */
    private int id;
    /** The user's unique email address, which is used as their primary login identifier. */
    private String email;
    /** The user's hashed permanent password for local authentication. */
    private String password;
    /** A one-time temporary password for new users during the initial setup process. */
    private String tempPassword;
    /** A flag indicating whether the user is currently in a temporary password state. */
    private boolean isTemp;
    /** The timestamp of when the user account was created. */
    private Timestamp createdAt;
    /** The authentication method used by the user (e.g., "LOCAL", "GOOGLE"). */
    private String authProvider;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Gets the unique ID of the user.
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the user.
     * @param id The user ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's email address.
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email The email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's hashed permanent password.
     * @return The hashed password string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's hashed permanent password.
     * @param password The hashed password string.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's temporary password.
     * @return The temporary password string.
     */
    public String getTempPassword() {
        return tempPassword;
    }

    /**
     * Sets the user's temporary password.
     * @param tempPassword The temporary password string.
     */
    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    /**
     * Checks if the user is in a temporary password state.
     * @return true if the user has a temporary password, false otherwise.
     */
    public boolean isTemp() {
        return isTemp;
    }

    /**
     * Sets the user's temporary password state.
     * @param isTemp true if the user has a temporary password.
     */
    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

    /**
     * Gets the creation timestamp of the user account.
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the user account.
     * @param createdAt The creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

	/**
	 * Gets the authentication provider for the user.
	 * @return The auth provider string (e.g., "LOCAL", "GOOGLE").
	 */
	public String getAuthProvider() {
		return authProvider;
	}

	/**
	 * Sets the authentication provider for the user.
	 * @param authProvider The auth provider string.
	 */
	public void setAuthProvider(String authProvider) {
		this.authProvider = authProvider;
	}
    
}

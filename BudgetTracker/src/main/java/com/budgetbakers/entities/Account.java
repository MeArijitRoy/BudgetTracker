package com.budgetbakers.entities;

import java.sql.Timestamp;
import java.util.List;

/**
 * Represents a single financial account belonging to a user.
 * This class models the `accounts` table in the database and also includes
 * transient fields for holding calculated summary data for display in the UI.
 */
public class Account {

    // --- Fields mapping to the 'accounts' database table ---

    /** The unique identifier for the account. */
    private int id;
    /** The ID of the user who owns this account. */
    private int userId;
    /** The user-defined name of the account (e.g., "Cash", "Bank Savings"). */
    private String name;
    /** The type of the account (e.g., "Cash", "Bank Account", "Credit Card"). */
    private String accountType;
    /** The starting balance of the account when it was created. */
    private double initialBalance;
    /** The currency code for the account (e.g., "INR", "USD"). */
    private String currency;
    /** A hex color code for UI display purposes. */
    private String color;
    /** A flag to determine if this account's balance should be included in global statistics. */
    private boolean excludeFromStats;
    /** The timestamp of when the account was created. */
    private Timestamp createdAt;
    
    // --- Transient fields for holding calculated data (not stored in DB) ---
    
    /** The calculated total income for this account over a specific period. */
    private double totalIncome;
    /** The calculated total expense for this account over a specific period. */
    private double totalExpense;
    /** The calculated current balance (initialBalance + totalIncome - totalExpense). */
    private double currentBalance;
    /** A list of top spending categories for this account, used for dashboard charts. */
    private List<CategorySpending> topSpendingCategories;

    /**
     * Default constructor.
     */
    public Account() {
    }

    // --- Getters and Setters ---

    /**
     * Gets the list of top spending categories for the account's dashboard widget.
     * @return A list of {@link CategorySpending} objects.
     */
    public List<CategorySpending> getTopSpendingCategories() {
		return topSpendingCategories;
	}

    /**
     * Sets the list of top spending categories for the account.
     * @param topSpendingCategories A list of {@link CategorySpending} objects.
     */
	public void setTopSpendingCategories(List<CategorySpending> topSpendingCategories) {
		this.topSpendingCategories = topSpendingCategories;
	}

	/**
	 * Gets the calculated total income for this account.
	 * @return The total income as a double.
	 */
	public double getTotalIncome() {
		return totalIncome;
	}

	/**
	 * Sets the calculated total income for this account.
	 * @param totalIncome The total income.
	 */
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}

	/**
	 * Gets the calculated total expense for this account.
	 * @return The total expense as a double.
	 */
	public double getTotalExpense() {
		return totalExpense;
	}

	/**
	 * Sets the calculated total expense for this account.
	 * @param totalExpense The total expense.
	 */
	public void setTotalExpense(double totalExpense) {
		this.totalExpense = totalExpense;
	}

	/**
	 * Gets the calculated current balance of this account.
	 * @return The current balance as a double.
	 */
	public double getCurrentBalance() {
		return currentBalance;
	}

	/**
	 * Sets the calculated current balance of this account.
	 * @param currentBalance The current balance.
	 */
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

    /**
     * Gets the unique ID of the account.
     * @return The account ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the account.
     * @param id The account ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who owns this account.
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who owns this account.
     * @param userId The user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the account.
     * @return The account name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the account.
     * @param name The account name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the account.
     * @return The account type (e.g., "Cash").
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Sets the type of the account.
     * @param accountType The account type.
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets the initial balance of the account.
     * @return The initial balance.
     */
    public double getInitialBalance() {
        return initialBalance;
    }

    /**
     * Sets the initial balance of the account.
     * @param initialBalance The initial balance.
     */
    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    /**
     * Gets the currency code of the account.
     * @return The currency code (e.g., "INR").
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency code of the account.
     * @param currency The currency code.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the display color for the account.
     * @return The hex color code.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the display color for the account.
     * @param color The hex color code.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Checks if the account is excluded from global statistics.
     * @return true if excluded, false otherwise.
     */
    public boolean isExcludeFromStats() {
        return excludeFromStats;
    }

    /**
     * Sets whether the account should be excluded from global statistics.
     * @param excludeFromStats true to exclude, false to include.
     */
    public void setExcludeFromStats(boolean excludeFromStats) {
        this.excludeFromStats = excludeFromStats;
    }

    /**
     * Gets the creation timestamp of the account.
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the account.
     * @param createdAt The creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}


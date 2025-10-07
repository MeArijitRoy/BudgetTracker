package com.budgetbakers.entities;

import java.sql.Timestamp;
import java.util.List;

/**
 * Represents a single financial transaction record.
 * This is the core entity of the application, modeling the `transactions` table in the database.
 * It holds details about an expense, income, or transfer and links to other entities
 * like Account, Category, and Label.
 */
public class Transaction {

    /** The unique identifier for the transaction. */
    private int id;
    /** The ID of the user who created this transaction. */
    private int userId;
    /** The type of the transaction (e.g., "Expense", "Income", "Transfer"). */
    private String transactionType;
    /** The monetary value of the transaction. */
    private double amount; 
    /** The specific date and time the transaction occurred. */
    private Timestamp transactionDate;
    /** A user-provided note or description for the transaction. */
    private String note;
    
    // --- Relational objects ---
    
    /** The primary account associated with the transaction (source for expense/transfer, destination for income). */
    private Account account;
    /** The destination account for a 'Transfer' type transaction. Null otherwise. */
    private Account toAccount; 
    /** The category assigned to this transaction. Can be null. */
    private Category category;
    /** A list of labels assigned to this transaction. Can be null or empty. */
    private List<Label> labels;

    // --- Getters and Setters ---
    
    /**
     * Gets the unique ID of the transaction.
     * @return The transaction ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the transaction.
     * @param id The transaction ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who owns this transaction.
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who owns this transaction.
     * @param userId The user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the type of the transaction.
     * @return The transaction type (e.g., "Expense", "Income").
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the type of the transaction.
     * @param transactionType The transaction type.
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Gets the amount of the transaction.
     * @return The transaction amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     * @param amount The transaction amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the date and time of the transaction.
     * @return The transaction timestamp.
     */
    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the date and time of the transaction.
     * @param transactionDate The transaction timestamp.
     */
    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the user-provided note for the transaction.
     * @return The note.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the user-provided note for the transaction.
     * @param note The note.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the primary account associated with the transaction.
     * @return The {@link Account} object.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the primary account for the transaction.
     * @param account The {@link Account} object.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the destination account for a transfer.
     * @return The destination {@link Account} object, or null if not a transfer.
     */
    public Account getToAccount() {
        return toAccount;
    }

    /**
     * Sets the destination account for a transfer.
     * @param toAccount The destination {@link Account} object.
     */
    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    /**
     * Gets the category assigned to the transaction.
     * @return The {@link Category} object, or null if uncategorized.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category for the transaction.
     * @param category The {@link Category} object.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the list of labels assigned to the transaction.
     * @return A list of {@link Label} objects.
     */
    public List<Label> getLabels() {
        return labels;
    }

    /**
     * Sets the list of labels for the transaction.
     * @param labels A list of {@link Label} objects.
     */
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}


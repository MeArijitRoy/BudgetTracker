package com.budgetbakers.entities;

/**
 * Represents a single, user-defined label or tag that can be applied to transactions.
 * This class models the `labels` table in the database and is used to provide
 * a flexible, secondary way of categorizing transactions.
 */
public class Label {

    /** The unique identifier for the label. */
    private int id;
    /** The ID of the user who owns this label. */
    private int userId;
    /** The user-defined name of the label (e.g., "Vacation 2025", "Business Trip"). */
    private String name;

    /**
     * Default constructor.
     */
    public Label() {
    }

    /**
     * Gets the unique ID of the label.
     * @return The label ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the label.
     * @param id The label ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who owns this label.
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who owns this label.
     * @param userId The user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the label.
     * @return The label name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the label.
     * @param name The label name.
     */
    public void setName(String name) {
        this.name = name;
    }
}


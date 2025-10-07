package com.budgetbakers.entities;

/**
 * Represents a single transaction category or sub-category.
 * This class models the `categories` table in the database and supports a
 * hierarchical structure through the `parentId` field.
 */
public class Category {

    /** The unique identifier for the category. */
    private int id;
    /** The ID of the user who owns this category. */
    private int userId;
    /** The ID of the parent category, if this is a sub-category. Null for top-level categories. */
    private Integer parentId;
    /** The user-defined name of the category (e.g., "Food & Drinks", "Groceries"). */
    private String name;

    /**
     * Default constructor.
     */
    public Category() {
    }

    /**
     * Gets the unique ID of the category.
     * @return The category ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the category.
     * @param id The category ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who owns this category.
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who owns this category.
     * @param userId The user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the parent category.
     * @return The parent category ID, or null if this is a top-level category.
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Sets the ID of the parent category.
     * @param parentId The parent category ID. Should be null for top-level categories.
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the name of the category.
     * @return The category name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     * @param name The category name.
     */
    public void setName(String name) {
        this.name = name;
    }
}


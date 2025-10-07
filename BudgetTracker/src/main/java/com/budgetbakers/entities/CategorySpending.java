package com.budgetbakers.entities;

/**
 * A Data Transfer Object (DTO) used to hold aggregated spending data for a single category.
 * This class is not mapped to a database table directly but is used to transfer
 * the results of a GROUP BY query from the service layer to the frontend for charting.
 */
public class CategorySpending {

    /** The name of the category. */
    private String categoryName;
    /** The total amount spent in this category for a given period. */
    private double totalAmount;

    /**
     * Gets the name of the category.
     * @return The category name.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the name of the category.
     * @param categoryName The category name.
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Gets the total amount spent in this category.
     * @return The total amount as a double.
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount spent in this category.
     * @param totalAmount The total amount.
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}


package com.budgetbakers.entities;

/**
 * A Data Transfer Object (DTO) used to hold aggregated cash flow data for a specific time period.
 * This class is typically used to transfer the results of a GROUP BY query from the
 * service layer to the frontend to draw a cash flow bar chart.
 */
public class MonthlyCashFlow {

    /** The time period for this data point (e.g., "2025-09" or "2025-09-23"). */
    private String month; 
    /** The total income for this period. */
    private double totalIncome;
    /** The total expense for this period. */
    private double totalExpense;

    /**
     * Gets the time period label for this cash flow data.
     * @return The time period as a String (e.g., "2025-09").
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the time period label for this cash flow data.
     * @param month The time period label.
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the total income for this period.
     * @return The total income as a double.
     */
    public double getTotalIncome() {
        return totalIncome;
    }

    /**
     * Sets the total income for this period.
     * @param totalIncome The total income.
     */
    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     * Gets the total expense for this period.
     * @return The total expense as a double.
     */
    public double getTotalExpense() {
        return totalExpense;
    }

    /**
     * Sets the total expense for this period.
     * @param totalExpense The total expense.
     */
    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }
}


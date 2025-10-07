package com.budgetbakers.entities;

import java.util.Date;

/**
 * A Data Transfer Object (DTO) representing a single data point for a balance trend line chart.
 * This class holds a specific date and the calculated total balance on that date.
 */
public class DailyBalance {

    /** The specific date for this data point. */
    private Date date;
    /** The calculated total balance on the given date. */
    private double balance;

    /**
     * Gets the date for this balance data point.
     * @return The date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date for this balance data point.
     * @param date The date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the calculated balance on this date.
     * @return The balance as a double.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the calculated balance on this date.
     * @param balance The balance.
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
}


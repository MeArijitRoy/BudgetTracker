package com.budgetbakers.entities;

import java.sql.Timestamp;

public class Account {

    private int id;
    private int userId;
    private String name;
    private String accountType;
    private double initialBalance;
    private String currency;
    private String color;
    private boolean excludeFromStats;
    private Timestamp createdAt;

    // Default constructor
    public Account() {
    }

    // Getters and Setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isExcludeFromStats() {
        return excludeFromStats;
    }

    public void setExcludeFromStats(boolean excludeFromStats) {
        this.excludeFromStats = excludeFromStats;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}


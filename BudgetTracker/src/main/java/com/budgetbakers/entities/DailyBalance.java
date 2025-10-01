package com.budgetbakers.entities;

import java.util.Date;

public class DailyBalance {

    private Date date;
    private double balance;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

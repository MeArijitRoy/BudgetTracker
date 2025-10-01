package com.budgetbakers.services;

import com.budgetbakers.entities.Account;
import com.budgetbakers.utils.DbConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);

    public List<Account> getAccountSummariesForUser(int userId) {
        List<Account> accounts = getAccountsForUser(userId);
        Map<Integer, Account> accountMap = new HashMap<>();
        for (Account acc : accounts) {
            accountMap.put(acc.getId(), acc);
        }

        String sql = "SELECT account_id, transaction_type, SUM(amount) as total " +
                     "FROM transactions WHERE user_id = ? AND transaction_type IN ('Income', 'Expense') " +
                     "GROUP BY account_id, transaction_type";

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    String type = rs.getString("transaction_type");
                    double total = rs.getDouble("total");

                    Account account = accountMap.get(accountId);
                    if (account != null) {
                        if ("Income".equals(type)) {
                            account.setTotalIncome(total);
                        } else if ("Expense".equals(type)) {
                            account.setTotalExpense(total);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating transaction totals for user {}", userId, e);
        }

        // Calculate the final current balance for each account
        for (Account acc : accounts) {
            double currentBalance = acc.getInitialBalance() + acc.getTotalIncome() - acc.getTotalExpense();
            acc.setCurrentBalance(currentBalance);
        }

        return accounts;
    }

    public void addAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, name, account_type, initial_balance, currency, color) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, account.getUserId());
            stmt.setString(2, account.getName());
            stmt.setString(3, account.getAccountType());
            stmt.setDouble(4, account.getInitialBalance());
            stmt.setString(5, account.getCurrency());
            stmt.setString(6, account.getColor());
            stmt.executeUpdate();
            logger.info("New account '{}' added successfully for user {}", account.getName(), account.getUserId());
        } catch (SQLException e) {
            logger.error("Error adding account for user {}", account.getUserId(), e);
        }
    }


    private List<Account> getAccountsForUser(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY id ASC";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account();
                    account.setId(rs.getInt("id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setName(rs.getString("name"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setInitialBalance(rs.getDouble("initial_balance"));
                    account.setCurrency(rs.getString("currency"));
                    account.setColor(rs.getString("color"));
                    account.setExcludeFromStats(rs.getBoolean("exclude_from_stats"));
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching accounts for user {}", userId, e);
        }
        return accounts;
    }
}

package com.budgetbakers.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.Category;
import com.budgetbakers.entities.Transaction;
import com.budgetbakers.utils.DbConnector;

public class RecordService {

    private static final Logger logger = LogManager.getLogger(RecordService.class);

    public List<Account> getAccountsForUser(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY name ASC";
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

    public List<Category> getCategoriesForUser(int userId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE user_id = ? ORDER BY name ASC";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setUserId(rs.getInt("user_id"));
                    category.setParentId(rs.getObject("parent_id", Integer.class));
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching categories for user {}", userId, e);
        }
        return categories;
    }

    public List<Transaction> getTransactionsForUser(int userId, Map<String, String> filters) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, a.name as account_name, c.name as category_name " +
            "FROM transactions t " +
            "JOIN accounts a ON t.account_id = a.id " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = ? "
        );
        

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (filters != null) {
            String dateFilter = filters.get("date");
            if (dateFilter != null && !dateFilter.isEmpty()) {
                sql.append("AND DATE(t.transaction_date) = ? ");
                params.add(dateFilter);
            }
            String typeFilter = filters.get("type");
            if (typeFilter != null && !typeFilter.isEmpty()) {
                sql.append("AND t.transaction_type = ? ");
                params.add(typeFilter);
            }
            String categoryFilter = filters.get("category");
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                sql.append("AND t.category_id = ? ");
                params.add(Integer.parseInt(categoryFilter));
            }
            String accountFilter = filters.get("account");
            if (accountFilter != null && !accountFilter.isEmpty()) {
                sql.append("AND t.account_id = ? ");
                params.add(Integer.parseInt(accountFilter));
            }
        }

        sql.append("ORDER BY t.transaction_date DESC");

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            logger.info("Final SQL: {} | Params: {}", sql.toString(), params);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction();
                    tx.setId(rs.getInt("id"));
                    tx.setUserId(rs.getInt("user_id"));
                    tx.setTransactionType(rs.getString("transaction_type"));
                    tx.setAmount(rs.getDouble("amount"));
                    tx.setTransactionDate(rs.getTimestamp("transaction_date"));
                    tx.setNote(rs.getString("note"));

                    Account account = new Account();
                    account.setId(rs.getInt("account_id"));
                    account.setName(rs.getString("account_name"));
                    tx.setAccount(account);

                    if (rs.getObject("category_id") != null) {
                        Category category = new Category();
                        category.setId(rs.getInt("category_id"));
                        category.setName(rs.getString("category_name"));
                        tx.setCategory(category);
                    }
                    transactions.add(tx);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching transactions for user {}", userId, e);
        }
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, account_id, category_id, transaction_type, amount, transaction_date, note, to_account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getUserId());
            stmt.setInt(2, transaction.getAccount().getId());

            if (transaction.getCategory() != null && transaction.getCategory().getId() != 0) {
                stmt.setInt(3, transaction.getCategory().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, transaction.getTransactionType());
            stmt.setDouble(5, transaction.getAmount());
            stmt.setTimestamp(6, new Timestamp(transaction.getTransactionDate().getTime()));
            stmt.setString(7, transaction.getNote());

            if (transaction.getToAccount() != null) {
                stmt.setInt(8, transaction.getToAccount().getId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            logger.info("New transaction added successfully for user {}", transaction.getUserId());
        } catch (SQLException e) {
            logger.error("Error adding transaction for user {}", transaction.getUserId(), e);
        }
    }
}


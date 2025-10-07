package com.budgetbakers.services;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.CategorySpending;
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
    
    public Map<String, Double> getDashboardKPIs(int userId) {
        Map<String, Double> kpis = new HashMap<>();
        double totalBalance = 0.0;
        double monthlyIncome = 0.0;
        double monthlyExpense = 0.0;

        List<Account> accountSummaries = getAccountSummariesForUser(userId);
        for (Account acc : accountSummaries) {
            totalBalance += acc.getCurrentBalance();
        }

        String sql = "SELECT transaction_type, SUM(amount) as total " +
                     "FROM transactions WHERE user_id = ? " +
                     "AND MONTH(transaction_date) = MONTH(CURRENT_DATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURRENT_DATE()) " +
                     "GROUP BY transaction_type";

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("transaction_type");
                    if ("Income".equals(type)) {
                        monthlyIncome = rs.getDouble("total");
                    } else if ("Expense".equals(type)) {
                        monthlyExpense = rs.getDouble("total");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating monthly KPIs for user {}", userId, e);
        }

        kpis.put("totalBalance", totalBalance);
        kpis.put("monthlySpending", monthlyExpense);
        kpis.put("monthlyCashFlow", monthlyIncome - monthlyExpense);
        
        return kpis;
    }

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

        // Calculate balances and fetch top spending categories for each account
        for (Account acc : accounts) {
            double currentBalance = acc.getInitialBalance() + acc.getTotalIncome() - acc.getTotalExpense();
            acc.setCurrentBalance(currentBalance);
            
            // NEW: Fetch and set the top spending categories for the chart
            acc.setTopSpendingCategories(getTopSpendingCategoriesForAccount(acc.getId()));
        }

        return accounts;
    }

    public void addAccount(Account account) {
        boolean isFirstAccount = false;
        try (Connection conn = DbConnector.getInstance().getConnection()) {
            // Check if this is the user's first account
            String checkSql = "SELECT COUNT(*) FROM accounts WHERE user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, account.getUserId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        isFirstAccount = true;
                    }
                }
            }

            // Insert the new account
            String insertSql = "INSERT INTO accounts (user_id, name, account_type, initial_balance, currency, color) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, account.getUserId());
                stmt.setString(2, account.getName());
                stmt.setString(3, account.getAccountType());
                stmt.setDouble(4, account.getInitialBalance());
                stmt.setString(5, account.getCurrency());
                stmt.setString(6, account.getColor());
                stmt.executeUpdate();
                logger.info("New account '{}' added successfully for user {}", account.getName(), account.getUserId());
            }

            // If it was the first account, create the default categories
            if (isFirstAccount) {
                logger.info("First account for user {}. Creating default categories.", account.getUserId());
                createDefaultCategories(conn, account.getUserId());
            }

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
    
    /**
     * NEW HELPER METHOD
     * Fetches the top 5 spending categories for a specific account for the current month.
     * @param accountId The ID of the account.
     * @return A list of CategorySpending objects.
     */
    private List<CategorySpending> getTopSpendingCategoriesForAccount(int accountId) {
        List<CategorySpending> spendingList = new ArrayList<>();
        String sql = "SELECT c.name as category_name, SUM(t.amount) as total_amount " +
                     "FROM transactions t " +
                     "JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.account_id = ? " +
                     "  AND t.transaction_type = 'Expense' " +
                     "  AND MONTH(t.transaction_date) = MONTH(CURRENT_DATE()) " +
                     "  AND YEAR(t.transaction_date) = YEAR(CURRENT_DATE()) " +
                     "GROUP BY c.name " +
                     "ORDER BY total_amount DESC " +
                     "LIMIT 5";

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CategorySpending spending = new CategorySpending();
                    spending.setCategoryName(rs.getString("category_name"));
                    spending.setTotalAmount(rs.getDouble("total_amount"));
                    spendingList.add(spending);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching top spending categories for account {}", accountId, e);
        }
        return spendingList;
    }
    public void deleteAccount(int accountId, int userId) {
        String sql = "DELETE FROM accounts WHERE id = ? AND user_id = ?";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully deleted account ID {} for user ID {}", accountId, userId);
            } else {
                logger.warn("No account was deleted. Account ID {} might not exist or not belong to user ID {}", accountId, userId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting account ID {} for user ID {}", accountId, userId, e);
        }
    }
    private void createDefaultCategories(Connection conn, int userId) throws SQLException {
        String parentSql = "INSERT INTO categories (user_id, name, parent_id) VALUES (?, ?, NULL)";
        String childSql = "INSERT INTO categories (user_id, name, parent_id) VALUES (?, ?, ?)";

        // This structure uses a Map to hold the parent categories and their sub-categories
        Map<String, List<String>> categories = new HashMap<>();
        categories.put("Income", List.of("Salary", "Freelance", "Gifts Received"));
        categories.put("Food & Drinks", List.of("Groceries", "Restaurant", "Bar, cafe"));
        categories.put("Shopping", List.of("Clothes", "Electronics", "Health and beauty"));
        categories.put("Housing", List.of("Rent", "Mortgage", "Utilities"));
        categories.put("Transportation", List.of("Fuel", "Public Transport", "Taxi"));
        categories.put("Life & Entertainment", List.of("Holiday, trips, hotels", "Hobbies", "Education"));

        // Iterate through the map to insert categories
        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            String parentName = entry.getKey();
            List<String> children = entry.getValue();

            // Insert the parent category and get its generated ID
            long parentId = 0;
            try (PreparedStatement parentStmt = conn.prepareStatement(parentSql, Statement.RETURN_GENERATED_KEYS)) {
                parentStmt.setInt(1, userId);
                parentStmt.setString(2, parentName);
                parentStmt.executeUpdate();
                try (ResultSet generatedKeys = parentStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        parentId = generatedKeys.getLong(1);
                    }
                }
            }

            // Insert all child categories using the parent ID
            if (parentId > 0 && children != null) {
                try (PreparedStatement childStmt = conn.prepareStatement(childSql)) {
                    for (String childName : children) {
                        childStmt.setInt(1, userId);
                        childStmt.setString(2, childName);
                        childStmt.setLong(3, parentId);
                        childStmt.addBatch(); // Add to a batch for efficient insertion
                    }
                    childStmt.executeBatch();
                }
            }
        }
        logger.info("Default categories created for user {}", userId);
    }
}


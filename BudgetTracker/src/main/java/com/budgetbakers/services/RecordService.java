package com.budgetbakers.services;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.Category;
import com.budgetbakers.entities.CategorySpending;
import com.budgetbakers.entities.DailyBalance;
import com.budgetbakers.entities.MonthlyCashFlow;
import com.budgetbakers.entities.Transaction;
import com.budgetbakers.utils.DbConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    /**
     * NEW METHOD FOR ANALYSIS PAGE
     * Fetches a unique list of currencies the user has across all their accounts.
     */
    public List<String> getDistinctCurrenciesForUser(int userId) {
        List<String> currencies = new ArrayList<>();
        String sql = "SELECT DISTINCT currency FROM accounts WHERE user_id = ?";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    currencies.add(rs.getString("currency"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching distinct currencies for user {}", userId, e);
        }
        return currencies;
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
    
    public List<MonthlyCashFlow> getCashFlowTrendForAnalysis(int userId, String dateRange, List<Integer> accountIds, String currency) {
        List<MonthlyCashFlow> cashFlowList = new ArrayList<>();
        
        String groupByClause;
        String dateFilterClause;
        String dateFormat;

        switch (dateRange) {
            case "last30days":
                groupByClause = "GROUP BY period ORDER BY period ASC";
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 30 DAY";
                dateFormat = "%Y-%m-%d";
                break;
            case "last6months":
                groupByClause = "GROUP BY period ORDER BY period ASC";
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 6 MONTH";
                dateFormat = "%Y-%m";
                break;
            case "last12months":
            default:
                groupByClause = "GROUP BY period ORDER BY period ASC";
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 12 MONTH";
                dateFormat = "%Y-%m";
                break;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    DATE_FORMAT(t.transaction_date, '" + dateFormat + "') AS period, " +
            "    SUM(CASE WHEN t.transaction_type = 'Income' THEN t.amount ELSE 0 END) AS total_income, " +
            "    SUM(CASE WHEN t.transaction_type = 'Expense' THEN t.amount ELSE 0 END) AS total_expense " +
            "FROM transactions t " +
            "JOIN accounts a ON t.account_id = a.id " +
            "WHERE t.user_id = ? AND a.currency = ? AND " + dateFilterClause + " "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(currency);
        
        if (accountIds != null && !accountIds.isEmpty()) {
            sql.append("AND t.account_id IN (");
            sql.append(accountIds.stream().map(id -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(accountIds);
        }
        
        sql.append(groupByClause);

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MonthlyCashFlow mcf = new MonthlyCashFlow();
                    mcf.setMonth(rs.getString("period"));
                    mcf.setTotalIncome(rs.getDouble("total_income"));
                    mcf.setTotalExpense(rs.getDouble("total_expense"));
                    cashFlowList.add(mcf);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching cash flow trend data for user {}", userId, e);
        }
        
        return cashFlowList;
    }

    public List<CategorySpending> getSpendingByCategoryForAnalysis(int userId, String dateRange, List<Integer> accountIds, String currency) {
        List<CategorySpending> spendingList = new ArrayList<>();
        String dateFilterClause;

        switch (dateRange) {
            case "last30days":
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 30 DAY";
                break;
            case "last6months":
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 6 MONTH";
                break;
            case "last12months":
            default:
                dateFilterClause = "t.transaction_date >= CURDATE() - INTERVAL 12 MONTH";
                break;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT c.name as category_name, SUM(t.amount) as total_amount " +
            "FROM transactions t " +
            "JOIN categories c ON t.category_id = c.id " +
            "JOIN accounts a ON t.account_id = a.id " +
            "WHERE t.user_id = ? AND a.currency = ? AND t.transaction_type = 'Expense' AND " + dateFilterClause + " "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(currency);
        
        if (accountIds != null && !accountIds.isEmpty()) {
            sql.append("AND t.account_id IN (");
            sql.append(accountIds.stream().map(id -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(accountIds);
        }
        
        sql.append("GROUP BY c.name ORDER BY total_amount DESC");

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CategorySpending cs = new CategorySpending();
                    cs.setCategoryName(rs.getString("category_name"));
                    cs.setTotalAmount(rs.getDouble("total_amount"));
                    spendingList.add(cs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching spending by category data for user {}", userId, e);
        }
        
        return spendingList;
    }
    
    public List<DailyBalance> getBalanceTrendForAnalysis(int userId, String dateRange, List<Integer> accountIds, String currency) {
        List<DailyBalance> balanceTrend = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (dateRange) {
            case "last30days":
                startDate = endDate.minusDays(29);
                break;
            case "last6months":
                startDate = endDate.minusMonths(6).withDayOfMonth(1);
                break;
            case "last12months":
            default:
                startDate = endDate.minusMonths(12).withDayOfMonth(1);
                break;
        }

        try (Connection conn = DbConnector.getInstance().getConnection()) {
            double startingBalance = calculateStartingBalance(conn, userId, startDate, accountIds, currency);
            Map<LocalDate, Double> dailyChanges = getDailyNetChanges(conn, userId, startDate, endDate, accountIds, currency);

            double currentBalance = startingBalance;
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                currentBalance += dailyChanges.getOrDefault(date, 0.0);
                DailyBalance dailyBalance = new DailyBalance();
                dailyBalance.setDate(Date.valueOf(date));
                dailyBalance.setBalance(currentBalance);
                balanceTrend.add(dailyBalance);
            }

        } catch (SQLException e) {
            logger.error("Error fetching balance trend data for user {}", userId, e);
        }
        
        return balanceTrend;
    }

    private double calculateStartingBalance(Connection conn, int userId, LocalDate startDate, List<Integer> accountIds, String currency) throws SQLException {
        double totalInitialBalance = 0;
        double pastNetTransactions = 0;

        StringBuilder initialBalanceSql = new StringBuilder("SELECT SUM(initial_balance) FROM accounts WHERE user_id = ? AND currency = ? ");
        if (accountIds != null && !accountIds.isEmpty()) {
            initialBalanceSql.append("AND id IN (").append(accountIds.stream().map(id -> "?").collect(Collectors.joining(","))).append(") ");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(initialBalanceSql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            stmt.setString(paramIndex++, currency);
            if (accountIds != null && !accountIds.isEmpty()) {
                for (Integer id : accountIds) {
                    stmt.setInt(paramIndex++, id);
                }
            }
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) totalInitialBalance = rs.getDouble(1);
            }
        }

        StringBuilder pastTransactionsSql = new StringBuilder(
            "SELECT SUM(CASE WHEN transaction_type = 'Income' THEN amount WHEN transaction_type = 'Expense' THEN -amount ELSE 0 END) " +
            "FROM transactions WHERE user_id = ? AND account_id IN (SELECT id FROM accounts WHERE user_id = ? AND currency = ?) AND transaction_date < ? "
        );
         if (accountIds != null && !accountIds.isEmpty()) {
            pastTransactionsSql.append("AND account_id IN (").append(accountIds.stream().map(id -> "?").collect(Collectors.joining(","))).append(") ");
        }

        try (PreparedStatement stmt = conn.prepareStatement(pastTransactionsSql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            stmt.setInt(paramIndex++, userId);
            stmt.setString(paramIndex++, currency);
            stmt.setDate(paramIndex++, Date.valueOf(startDate));
             if (accountIds != null && !accountIds.isEmpty()) {
                for (Integer id : accountIds) {
                    stmt.setInt(paramIndex++, id);
                }
            }
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) pastNetTransactions = rs.getDouble(1);
            }
        }

        return totalInitialBalance + pastNetTransactions;
    }
    
    private Map<LocalDate, Double> getDailyNetChanges(Connection conn, int userId, LocalDate startDate, LocalDate endDate, List<Integer> accountIds, String currency) throws SQLException {
        Map<LocalDate, Double> dailyChanges = new HashMap<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DATE(transaction_date) as day, SUM(CASE WHEN transaction_type = 'Income' THEN amount WHEN transaction_type = 'Expense' THEN -amount ELSE 0 END) as net_change " +
            "FROM transactions WHERE user_id = ? AND account_id IN (SELECT id FROM accounts WHERE user_id = ? AND currency = ?) " +
            "AND transaction_date >= ? AND transaction_date <= ? "
        );

        if (accountIds != null && !accountIds.isEmpty()) {
            sql.append("AND account_id IN (").append(accountIds.stream().map(id -> "?").collect(Collectors.joining(","))).append(") ");
        }
        sql.append("GROUP BY day ORDER BY day ASC");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            stmt.setInt(paramIndex++, userId);
            stmt.setString(paramIndex++, currency);
            stmt.setDate(paramIndex++, Date.valueOf(startDate));
            stmt.setDate(paramIndex++, Date.valueOf(endDate));
            if (accountIds != null && !accountIds.isEmpty()) {
                for (Integer id : accountIds) {
                    stmt.setInt(paramIndex++, id);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    LocalDate date = rs.getDate("day").toLocalDate();
                    double netChange = rs.getDouble("net_change");
                    dailyChanges.put(date, netChange);
                }
            }
        }
        return dailyChanges;
    }
    public List<Transaction> getTransactionsForAnalysis(int userId, String dateRange, List<Integer> accountIds, String currency) {
        List<Transaction> transactions = new ArrayList<>();
        String dateFilterClause = getDateFilterClause(dateRange);

        StringBuilder sql = new StringBuilder(
            "SELECT t.*, a.name as account_name, c.name as category_name " +
            "FROM transactions t " +
            "JOIN accounts a ON t.account_id = a.id " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = ? AND a.currency = ? AND " + dateFilterClause + " "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(currency);

        if (accountIds != null && !accountIds.isEmpty()) {
            sql.append("AND t.account_id IN (");
            sql.append(accountIds.stream().map(id -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(accountIds);
        }

        sql.append("ORDER BY t.transaction_date DESC");

        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

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
            logger.error("Error fetching transactions for analysis for user {}", userId, e);
        }
        return transactions;
    }
    private String getDateFilterClause(String dateRange) {
        switch (dateRange) {
            case "last30days":
                return "t.transaction_date >= CURDATE() - INTERVAL 30 DAY";
            case "last3months":
                return "t.transaction_date >= CURDATE() - INTERVAL 3 MONTH";
            case "last6months":
                return "t.transaction_date >= CURDATE() - INTERVAL 6 MONTH";
            case "last12months":
            default:
                return "t.transaction_date >= CURDATE() - INTERVAL 12 MONTH";
        }
    }
    public void deleteTransaction(int transactionId, int userId) {
        // The WHERE clause includes user_id to ensure users can only delete their own transactions.
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (Connection conn = DbConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully deleted transaction ID {} for user ID {}", transactionId, userId);
            } else {
                logger.warn("No transaction was deleted. Transaction ID {} might not exist or not belong to user ID {}", transactionId, userId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting transaction ID {} for user ID {}", transactionId, userId, e);
        }
    }
}


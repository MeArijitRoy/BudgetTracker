package com.budgetbakers.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.Category;
import com.budgetbakers.entities.Transaction;
import com.budgetbakers.entities.User;
import com.budgetbakers.services.RecordService;

@WebServlet("/AddRecordServlet")
public class AddRecordServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AddRecordServlet.class);
    private final RecordService recordService = new RecordService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            logger.warn("Unauthorized attempt to add a record. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Create a new Transaction object from the form data
            Transaction newTransaction = new Transaction();
            newTransaction.setUserId(user.getId());
            newTransaction.setTransactionType(request.getParameter("type"));
            
            // Changed from BigDecimal to Double.parseDouble
            newTransaction.setAmount(Double.parseDouble(request.getParameter("amount")));
            
            newTransaction.setNote(request.getParameter("note"));

            // Handle date conversion
            String dateStr = request.getParameter("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = sdf.parse(dateStr);
            newTransaction.setTransactionDate(new Timestamp(parsedDate.getTime()));

            // Set the associated Account object
            Account account = new Account();
            account.setId(Integer.parseInt(request.getParameter("account")));
            newTransaction.setAccount(account);

            // Set the associated Category object (if one was selected)
            String categoryIdStr = request.getParameter("category");
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                Category category = new Category();
                category.setId(Integer.parseInt(categoryIdStr));
                newTransaction.setCategory(category);
            }

            // Note: Logic to parse and save Labels would be added here.

            // Call the service to add the transaction to the database
            recordService.addTransaction(newTransaction);
            logger.info("New record added successfully for user {}", user.getId());

            // Redirect back to the records page to show the updated list
            response.sendRedirect("RecordsServlet");

        } catch (Exception e) {
            logger.error("Error occurred while adding a new record for user {}", user.getId(), e);
            // Set an error message and forward back to the form
            request.setAttribute("modalError", "Failed to add record. Please check your input.");
            // We need to fetch the data for the dropdowns again before forwarding
            request.setAttribute("accounts", recordService.getAccountsForUser(user.getId()));
            request.setAttribute("categories", recordService.getCategoriesForUser(user.getId()));
            request.getRequestDispatcher("/views/records.jsp").forward(request, response);
        }
    }
}


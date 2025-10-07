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

/**
 * Servlet controller for handling the creation of a new transaction record.
 * This servlet processes the form submission from the 'Add Record' pop-up modal.
 */
@WebServlet("/AddRecordServlet")
public class AddRecordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AddRecordServlet.class);
    private final RecordService recordService = new RecordService();

    /**
     * Handles HTTP POST requests to create a new transaction. It retrieves form data,
     * creates a new {@link Transaction} object, and calls the {@link RecordService} to save it.
     * On success, it redirects to the main records page. On failure, it forwards back
     * to the form with an error message and the necessary data to re-populate dropdowns.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException if an input or output error is detected when the servlet handles the POST request
     */
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
            Transaction newTransaction = new Transaction();
            newTransaction.setUserId(user.getId());
            newTransaction.setTransactionType(request.getParameter("type"));
            newTransaction.setAmount(Double.parseDouble(request.getParameter("amount")));
            newTransaction.setNote(request.getParameter("note"));

            String dateStr = request.getParameter("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = sdf.parse(dateStr);
            newTransaction.setTransactionDate(new Timestamp(parsedDate.getTime()));

            Account account = new Account();
            account.setId(Integer.parseInt(request.getParameter("account")));
            newTransaction.setAccount(account);

            String categoryIdStr = request.getParameter("category");
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                Category category = new Category();
                category.setId(Integer.parseInt(categoryIdStr));
                newTransaction.setCategory(category);
            }

            recordService.addTransaction(newTransaction);
            logger.info("New record added successfully for user {}", user.getId());

            response.sendRedirect("RecordsServlet");

        } catch (Exception e) {
            logger.error("Error occurred while adding a new record for user {}", user.getId(), e);
            request.setAttribute("modalError", "Failed to add record. Please check your input.");
            // Re-fetch data for dropdowns before forwarding back to the page
            request.setAttribute("accounts", recordService.getAccountsForUser(user.getId()));
            request.setAttribute("categories", recordService.getCategoriesForUser(user.getId()));
            request.getRequestDispatcher("/views/records.jsp").forward(request, response);
        }
    }
}


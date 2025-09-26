package com.budgetbakers.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@WebServlet("/RecordsServlet")
public class RecordsServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(RecordsServlet.class);
    private final RecordService recordService = new RecordService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // The filter should prevent this, but it's a good safeguard.
            logger.warn("Unauthorized access to RecordsServlet. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = user.getId();

            // Collect filter parameters from the request
            Map<String, String> filters = new HashMap<>();
            filters.put("date", request.getParameter("filterDate"));
            filters.put("type", request.getParameter("filterType"));
            filters.put("category", request.getParameter("filterCategory"));

            // filters.put("labels", request.getParameter("filterLabels"));
            logger.debug("filterAccount raw param = {}", request.getParameter("filterAccount"));

            filters.put("account", request.getParameter("filterAccount"));

            // Fetch all necessary data from the service layer
            List<Transaction> transactions = recordService.getTransactionsForUser(userId, filters);
            List<Account> accounts = recordService.getAccountsForUser(userId);
            List<Category> categories = recordService.getCategoriesForUser(userId);

            // Set the data as request attributes to be used by the JSP
            request.setAttribute("transactions", transactions);
            request.setAttribute("accounts", accounts);
            request.setAttribute("categories", categories);

            logger.info("Successfully fetched {} transactions for user {}", transactions.size(), userId);

            // Forward to the JSP to display the data
            request.getRequestDispatcher("/views/records.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while fetching records for user.", e);
            // Redirect to a generic error page
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}

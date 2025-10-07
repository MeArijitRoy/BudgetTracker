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

/**
 * Servlet controller for handling requests related to the main Records page.
 * This servlet is responsible for fetching and displaying a list of the user's
 * transactions, and for handling filter submissions to refine that list.
 */
@WebServlet("/RecordsServlet")
public class RecordsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(RecordsServlet.class);
    private final RecordService recordService = new RecordService();

    /**
     * Handles HTTP GET requests to display the records page. It retrieves the list of
     * transactions for the logged-in user, applying any filters specified in the request
     * parameters. It also fetches the user's accounts and categories to populate the
     * filter dropdowns, then forwards all data to the `records.jsp` for rendering.
     *
     * @param request  the {@link HttpServletRequest} object that may contain filter parameters
     * like 'filterDate', 'filterType', 'filterCategory', and 'filterAccount'.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if the request for the GET could not be handled.
     * @throws IOException if an input or output error is detected when the servlet handles the GET request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
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
            filters.put("account", request.getParameter("filterAccount"));
            
            logger.debug("Fetching records for user {} with filters: {}", userId, filters);

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
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


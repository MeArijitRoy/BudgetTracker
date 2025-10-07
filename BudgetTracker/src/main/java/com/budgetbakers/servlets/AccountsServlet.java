package com.budgetbakers.servlets;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.User;
import com.budgetbakers.services.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet controller for handling requests related to the main Accounts page.
 * This servlet is responsible for fetching and displaying a summary of all
 * of the user's financial accounts.
 */
@WebServlet("/AccountsServlet")
public class AccountsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AccountsServlet.class);
    private final AccountService accountService = new AccountService();

    /**
     * Handles HTTP GET requests to display the accounts page. It retrieves a summary
     * for each of the logged-in user's accounts, including calculated balances and totals,
     * and forwards this data to the `accounts.jsp` for rendering.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            logger.warn("Unauthorized access to AccountsServlet. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = user.getId();
            logger.info("Fetching account summaries for user ID: {}", userId);
            List<Account> accountSummaries = accountService.getAccountSummariesForUser(userId);
            request.setAttribute("accountSummaries", accountSummaries);
            logger.info("Successfully fetched {} account summaries for user {}.", accountSummaries.size(), userId);
            request.getRequestDispatcher("/views/accounts.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while fetching account summaries for user.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


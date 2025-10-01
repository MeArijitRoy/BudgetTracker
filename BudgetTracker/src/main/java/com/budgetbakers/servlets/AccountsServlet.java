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

@WebServlet("/AccountsServlet")
public class AccountsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AccountsServlet.class);
    private final AccountService accountService = new AccountService();

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

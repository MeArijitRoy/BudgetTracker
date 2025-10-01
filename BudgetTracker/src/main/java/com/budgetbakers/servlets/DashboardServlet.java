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
import java.util.Map;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DashboardServlet.class);
    private final AccountService accountService = new AccountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            logger.warn("Unauthorized access to DashboardServlet. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = user.getId();
            logger.info("Fetching dashboard data for user ID: {}", userId);

            Map<String, Double> kpis = accountService.getDashboardKPIs(userId);
            List<Account> accountSummaries = accountService.getAccountSummariesForUser(userId);

            // Determine a primary currency for the main KPIs
            String primaryCurrency = "INR"; // Default currency
            if (accountSummaries != null && !accountSummaries.isEmpty()) {
                primaryCurrency = accountSummaries.get(0).getCurrency();
            }

            request.setAttribute("kpis", kpis);
            request.setAttribute("accountSummaries", accountSummaries);
            request.setAttribute("primaryCurrency", primaryCurrency); // Pass primary currency to JSP

            logger.info("Successfully fetched dashboard data for user {}.", userId);
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while fetching dashboard data for user.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


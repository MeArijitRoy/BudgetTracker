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

/**
 * Servlet controller for preparing and displaying the main user Dashboard.
 * This servlet fetches high-level summary data (KPIs) and per-account
 * summaries to provide an "at a glance" view of the user's financial health.
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DashboardServlet.class);
    private final AccountService accountService = new AccountService();

    /**
     * Handles HTTP GET requests to display the dashboard. It retrieves Key Performance Indicators (KPIs)
     * and a summary for each of the user's accounts from the {@link AccountService},
     * sets them as request attributes, and forwards to the `dashboard.jsp` for rendering.
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
            logger.warn("Unauthorized access to DashboardServlet. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = user.getId();
            logger.info("Fetching dashboard data for user ID: {}", userId);

            // Fetch the main KPIs and individual account summaries
            Map<String, Double> kpis = accountService.getDashboardKPIs(userId);
            List<Account> accountSummaries = accountService.getAccountSummariesForUser(userId);

            // Determine a primary currency to display for the main KPIs
            String primaryCurrency = "INR"; // Default currency
            if (accountSummaries != null && !accountSummaries.isEmpty()) {
                primaryCurrency = accountSummaries.get(0).getCurrency();
            }

            // Set the data as request attributes for the JSP
            request.setAttribute("kpis", kpis);
            request.setAttribute("accountSummaries", accountSummaries);
            request.setAttribute("primaryCurrency", primaryCurrency);

            logger.info("Successfully fetched dashboard data for user {}.", userId);
            
            // Forward to the JSP page for display
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while fetching dashboard data for user.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


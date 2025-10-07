package com.budgetbakers.servlets;

import com.budgetbakers.entities.Account;
import com.budgetbakers.entities.Category;
import com.budgetbakers.entities.User;
import com.budgetbakers.services.RecordService;
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
 * Servlet controller for preparing and displaying the main Analysis page.
 * This servlet's primary responsibility is to fetch the necessary data to populate
 * the filter dropdowns (accounts, categories, currencies) on the analysis page.
 */
@WebServlet("/AnalysisPageServlet")
public class AnalysisPageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AnalysisPageServlet.class);
    private final RecordService recordService = new RecordService();

    /**
     * Handles HTTP GET requests to display the analysis page. It fetches the user's
     * accounts, categories, and distinct currencies to populate the filter controls
     * and then forwards the request to the `analysis.jsp` for rendering.
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
            logger.warn("Unauthorized access to Analysis page. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = user.getId();
            logger.info("Preparing analysis page for user ID: {}", userId);

            // Fetch all the data needed to populate the filter dropdowns
            List<Account> accounts = recordService.getAccountsForUser(userId);
            List<Category> categories = recordService.getCategoriesForUser(userId);
            List<String> currencies = recordService.getDistinctCurrenciesForUser(userId);

            // Set the data as request attributes for the JSP
            request.setAttribute("accounts", accounts);
            request.setAttribute("categories", categories);
            request.setAttribute("currencies", currencies);

            // Forward to the analysis JSP to display the page
            request.getRequestDispatcher("/views/analysis.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while preparing the analysis page for user.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


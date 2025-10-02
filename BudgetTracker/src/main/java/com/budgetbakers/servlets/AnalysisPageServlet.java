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

@WebServlet("/AnalysisPageServlet")
public class AnalysisPageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AnalysisPageServlet.class);
    private final RecordService recordService = new RecordService();

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

            List<Account> accounts = recordService.getAccountsForUser(userId);
            List<Category> categories = recordService.getCategoriesForUser(userId);
            
            List<String> currencies = recordService.getDistinctCurrenciesForUser(userId);

            request.setAttribute("accounts", accounts);
            request.setAttribute("categories", categories);
            request.setAttribute("currencies", currencies);

            request.getRequestDispatcher("/views/analysis.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("An error occurred while preparing the analysis page for user.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}

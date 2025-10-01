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

@WebServlet("/AddAccountServlet")
public class AddAccountServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AddAccountServlet.class);
    private final AccountService accountService = new AccountService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            logger.warn("Unauthorized attempt to add an account. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String accountName = request.getParameter("accountName");
            String accountType = request.getParameter("accountType");
            double initialBalance = Double.parseDouble(request.getParameter("initialBalance"));
            String currency = request.getParameter("currency");
            String color = request.getParameter("color");

            Account newAccount = new Account();
            newAccount.setUserId(user.getId());
            newAccount.setName(accountName);
            newAccount.setAccountType(accountType);
            newAccount.setInitialBalance(initialBalance);
            newAccount.setCurrency(currency);
            newAccount.setColor(color);

            accountService.addAccount(newAccount);
            logger.info("New account added successfully for user {}", user.getId());
            response.sendRedirect("AccountsServlet");

        } catch (NumberFormatException e) {
            logger.error("Invalid number format for initial balance for user {}", user.getId(), e);
            request.setAttribute("formError", "Please enter a valid number for the initial balance.");
            request.getRequestDispatcher("/views/accounts.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error occurred while adding a new account for user {}", user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}

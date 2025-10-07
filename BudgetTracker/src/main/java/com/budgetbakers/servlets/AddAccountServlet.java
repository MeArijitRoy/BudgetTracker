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
 * Servlet controller for handling the creation of a new financial account.
 * This servlet processes the form submission from the 'Add New Account' form
 * on the accounts page.
 */
@WebServlet("/AddAccountServlet")
public class AddAccountServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AddAccountServlet.class);
    private final AccountService accountService = new AccountService();

    /**
     * Handles HTTP POST requests to create a new account. It retrieves form data,
     * creates a new {@link Account} object, and calls the {@link AccountService} to save it.
     * On success, it redirects to the main accounts page. On failure, it forwards back
     * to the form with an error message.
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
            // We need to re-fetch account summaries to correctly re-render the page with the error.
            List<Account> accountSummaries = accountService.getAccountSummariesForUser(user.getId());
            request.setAttribute("accountSummaries", accountSummaries);
            request.getRequestDispatcher("/views/accounts.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error occurred while adding a new account for user {}", user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


package com.budgetbakers.servlets;

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

/**
 * Servlet controller for handling the deletion of a financial account.
 * This servlet is triggered when a user confirms the deletion of an account
 * from the accounts page.
 */
@WebServlet("/DeleteAccountServlet")
public class DeleteAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(DeleteAccountServlet.class);
    private final AccountService accountService = new AccountService();

    /**
     * Handles HTTP GET requests to delete an account. It retrieves the account ID
     * from the request parameters and calls the {@link AccountService} to perform the deletion.
     * It includes a security check to ensure users can only delete their own accounts.
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
            logger.warn("Unauthorized attempt to delete an account. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. Get the ID of the account to delete from the request parameter.
            int accountId = Integer.parseInt(request.getParameter("id"));
            int userId = user.getId();

            logger.info("User {} is attempting to delete account with ID {}", userId, accountId);

            // 2. Call the service method to perform the deletion.
            accountService.deleteAccount(accountId, userId);

            // 3. Redirect back to the AccountsServlet to show the updated list.
            response.sendRedirect("AccountsServlet");

        } catch (NumberFormatException e) {
            logger.error("Invalid account ID format provided for deletion.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        } catch (Exception e) {
            logger.error("Error occurred while deleting account for user {}", user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}


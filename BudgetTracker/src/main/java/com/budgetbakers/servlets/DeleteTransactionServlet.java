package com.budgetbakers.servlets;

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

@WebServlet("/DeleteTransactionServlet")
public class DeleteTransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(DeleteTransactionServlet.class);
    private final RecordService recordService = new RecordService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            logger.warn("Unauthorized attempt to delete a transaction. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. Get the ID of the transaction to delete from the request parameter.
            int transactionId = Integer.parseInt(request.getParameter("id"));
            int userId = user.getId();

            logger.info("User {} is attempting to delete transaction with ID {}", userId, transactionId);

            // 2. Call the service method to perform the deletion.
            // Note: We will need to create this method in the RecordService.
            recordService.deleteTransaction(transactionId, userId);

            // 3. Redirect back to the RecordsServlet to show the updated list.
            response.sendRedirect("RecordsServlet");

        } catch (NumberFormatException e) {
            logger.error("Invalid transaction ID format provided for deletion.", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        } catch (Exception e) {
            logger.error("Error occurred while deleting transaction for user {}", user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}

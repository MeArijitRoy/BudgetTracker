package com.budgetbakers.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.budgetbakers.services.UserService;

/**
 * Servlet controller for setting a user's new permanent password.
 * This servlet handles the form submission from the 'Set New Password' page,
 * which is part of the initial login flow for new users.
 */
@WebServlet("/SetPasswordServlet")
public class SetPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SetPasswordServlet.class);
	private final UserService userService = new UserService();
	
    /**
     * Default constructor for the servlet.
     */
    public SetPasswordServlet() {
        super();
    }

    /**
     * Handles HTTP GET requests to set a new permanent password. It validates that the
     * new password and confirmation password match. If they match, it updates the user's
     * password in the database, invalidates the session, and forwards to the login page
     * with a success message. If they don't match, it returns to the form with an error.
     *
     * @param request  the {@link HttpServletRequest} object that contains 'newPassword' and 'confirmPassword' parameters.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Guard clause for users accessing the servlet directly without an email in session.
        if (email == null || email.isEmpty()) {
        	logger.warn("SetPasswordServlet accessed without an email in session.");
            session.setAttribute("message", "Your session may have expired. Please enter your email again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (newPassword != null && newPassword.equals(confirmPassword)) {
        	// Passwords match, set the new permanent password.
        	logger.info("Setting permanent password for user: {}", email);
            userService.setPermanentPassword(email, newPassword);
            session.invalidate(); // Invalidate session to force a fresh login.
            request.setAttribute("message", "Password successfully set! Please log in with your new password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
        	// Passwords do not match, return to the form with an error.
        	logger.warn("Password confirmation failed for user: {}", email);
            request.setAttribute("error", "Passwords do not match. Please try again.");
            request.getRequestDispatcher("views/setPassword.jsp").forward(request, response);
        }
	}

	/**
     * Delegates POST requests to the {@code doGet} method to handle form submissions
     * with the same logic as GET requests.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client has made of the servlet.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if the request for the POST could not be handled.
     * @throws IOException if an input or output error is detected when the servlet handles the POST request.
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

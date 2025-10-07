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
 * Servlet controller for verifying a new user's temporary password.
 * This servlet handles the form submission from the temporary password entry page.
 */
@WebServlet("/TempPasswordServlet")
public class TempPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TempPasswordServlet.class);
	private final UserService userService = new UserService();
	
    /**
     * Default constructor for the servlet.
     */
    public TempPasswordServlet() {
        super();
    }

    /**
     * Handles HTTP GET requests to verify a user's temporary password. It retrieves the
     * email from the session and the temporary password from the request. If the password
     * is valid, it forwards the user to the page for setting a new permanent password.
     * Otherwise, it returns to the temporary password page with an error message.
     *
     * @param request  the {@link HttpServletRequest} object that contains the 'tempPassword' as a parameter.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String tempPassword = request.getParameter("tempPassword");

        // Guard clause for users accessing the servlet directly without an email in session.
        if (email == null || email.isEmpty()) {
        	logger.warn("TempPasswordServlet accessed without an email in session.");
            session.setAttribute("message", "Your session may have expired. Please enter your email again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (userService.verifyTempPassword(email, tempPassword)) {
        	// Temporary password is correct, forward to the set new password page.
        	logger.info("Temporary password verified successfully for user: {}", email);
            request.getRequestDispatcher("views/setPassword.jsp").forward(request, response);
        } else {
        	// Temporary password was incorrect, return to the form with an error.
        	logger.warn("Invalid temporary password attempt for user: {}", email);
            request.setAttribute("error", "Invalid temporary password. Please try again.");
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
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

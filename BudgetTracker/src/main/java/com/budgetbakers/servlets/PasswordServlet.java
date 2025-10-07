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

import com.budgetbakers.entities.User;
import com.budgetbakers.services.UserService;

/**
 * Servlet controller for verifying a user's permanent password.
 * This servlet handles the form submission from the password entry page for
 * existing users who have already set a permanent password.
 */
@WebServlet("/PasswordServlet")
public class PasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PasswordServlet.class);
	private final UserService userService = new UserService();
	
    /**
     * Default constructor for the servlet.
     */
    public PasswordServlet() {
        super();
    }

    /**
     * Handles HTTP GET requests to verify a user's permanent password. It retrieves the
     * email from the session and the password from the request. If the credentials are valid,
     * it establishes a full user session and forwards to the dashboard. Otherwise, it returns
     * the user to the appropriate page with an error message.
     *
     * @param request  the {@link HttpServletRequest} object that contains the user's password as a parameter.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String password = request.getParameter("password");
        
        // Guard clause for users accessing the servlet directly without an email in session.
        if (email == null || email.isEmpty()) {
        	logger.warn("PasswordServlet accessed without an email in session.");
            session.setAttribute("message", "Your session may have expired. Please enter your email again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (userService.verifyPassword(email, password)) {
        	// Password is correct, create full user session and forward to dashboard.
        	logger.info("Permanent password verified successfully for user: {}", email);
            User user = userService.findUserByEmail(email);
            session.setAttribute("user", user); 
            request.getRequestDispatcher("DashboardServlet").forward(request, response);
        } else {
        	// Password was incorrect, return to password page with an error.
        	logger.warn("Invalid permanent password attempt for user: {}", email);
            request.setAttribute("error", "Invalid password. Please try again.");
            request.getRequestDispatcher("views/password.jsp").forward(request, response);
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

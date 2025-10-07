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
 * Servlet controller for handling the initial user login flow via email.
 * It determines if a user is new, an existing user with a temporary password,
 * or an existing user with a permanent password, and routes them accordingly.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LoginServlet.class);
	private final UserService userService = new UserService();
       
    /**
     * Default constructor for the servlet.
     */
    public LoginServlet() {
        super();
    }

    /**
     * Handles HTTP GET requests for the login process. It checks the provided email
     * against the database and directs the user to the appropriate next step:
     * - New user: Creates the user, sends a temporary password, and forwards to the temp password page.
     * - Existing user (temporary): Forwards to the temp password page.
     * - Existing user (Google account): Forwards back to the login page with a message.
     * - Existing user (permanent password): Forwards to the permanent password entry page.
     *
     * @param request  the {@link HttpServletRequest} object that contains the user's email as a parameter.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
        HttpSession session = request.getSession();
        session.setAttribute("email", email);

        User user = userService.findUserByEmail(email);

        if (user == null) {
            // New user flow
        	logger.info("New user detected with email: {}. Creating user.", email);
            String tempPassword = userService.createNewUser(email);
            session.setAttribute("tempPassword", tempPassword); 
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        } else if ("GOOGLE".equals(user.getAuthProvider())) {
            // User exists but signed up with Google
        	logger.warn("User with Google account ({}) attempted to log in via email form.", email);
            request.setAttribute("errorMessage", "This email is registered with Google. Please use the 'Sign in with Google' button.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else if (user.isTemp()) {
            // Existing user who has not set a permanent password yet
        	logger.info("Existing user with temporary password found: {}", email);
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        } else {
            // Existing user with a permanent password
        	logger.info("Existing user with permanent password found: {}", email);
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

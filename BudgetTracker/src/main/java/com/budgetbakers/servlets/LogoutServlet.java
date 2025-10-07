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

/**
 * Servlet controller for handling user logout.
 * This servlet invalidates the current user session and redirects the user
 * back to the main login page.
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LogoutServlet.class);
	
    /**
     * Default constructor for the servlet.
     */
    public LogoutServlet() {
        super();
    }

    /**
     * Handles HTTP GET requests to log the user out. It invalidates the current
     * HTTP session (if one exists) and then redirects the user to the login page.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client has made of the servlet.
     * @param response the {@link HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false); 
        
        if (session != null) {
            session.invalidate();
            logger.info("User logged out, session invalidated.");
        }
        // Redirect to the login page after invalidating the session.
        response.sendRedirect("login.jsp");
	}

	/**
     * Delegates POST requests to the {@code doGet} method to ensure that logout
     * functionality is consistent regardless of the HTTP method used.
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

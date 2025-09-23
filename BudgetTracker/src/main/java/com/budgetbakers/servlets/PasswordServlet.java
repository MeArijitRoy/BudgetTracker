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

@WebServlet("/PasswordServlet")
public class PasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();
	private static final Logger logger = LogManager.getLogger(PasswordServlet.class);
	
    public PasswordServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String password = request.getParameter("password");

        if (userService.verifyPassword(email, password)) {
            session.setAttribute("user", email); 
            logger.info("Email-linked login successful for user :{}.Now redirecting to home (home.jsp) .", email);
            request.getRequestDispatcher("views/home.jsp").forward(request, response);
        } else {
        	logger.info("Email-linked login not successful due to invalid password for user :{}.Now redirecting to password page (password.jsp) .", email);
            request.setAttribute("error", "Invalid password. Please try again.");
            request.getRequestDispatcher("views/password.jsp").forward(request, response);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

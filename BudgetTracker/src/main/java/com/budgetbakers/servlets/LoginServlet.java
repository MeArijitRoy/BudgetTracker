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


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LoginServlet.class);
	private final UserService userService = new UserService();
       
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
        HttpSession session = request.getSession();
        session.setAttribute("email", email);

        User user = userService.findUserByEmail(email);

        if (user == null) {
            String tempPassword = userService.createNewUser(email);
            session.setAttribute("tempPassword", tempPassword); 
            logger.info("New user created via email: {}. Redirecting to temporary password page (tempPassword.jsp).", email);
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        } else if (user.isTemp()) {
        	logger.info("Login initiated for user with a pending temporary password: {}. Redirecting to temporary password page (tempPassword.jsp).", email);
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        }else if ("GOOGLE".equals(user.getAuthProvider())) {
        	logger.info("Email login attempt by a Google-linked user: {}. Redirecting back to login page (login.jsp) with messag .", email);
            request.setAttribute("message", "This email is registered with Google. Please use the 'Sign in with Google' button.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
        	logger.info("User entered email for login: {}. Now redirecting to password page (password.jsp) .", email);
            request.getRequestDispatcher("views/password.jsp").forward(request, response);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

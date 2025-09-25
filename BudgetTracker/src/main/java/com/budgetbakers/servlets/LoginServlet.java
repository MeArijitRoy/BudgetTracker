package com.budgetbakers.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.budgetbakers.entities.User;
import com.budgetbakers.services.UserService;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        } else if (user.isTemp()) {
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        }else if ("GOOGLE".equals(user.getAuthProvider())) {
            request.setAttribute("message", "Please sign in with Google.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("views/password.jsp").forward(request, response);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

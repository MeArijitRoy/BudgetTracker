package com.budgetbakers.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.budgetbakers.services.UserService;

@WebServlet("/SetPasswordServlet")
public class SetPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();
	
    public SetPasswordServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword != null && newPassword.equals(confirmPassword)) {
            userService.setPermanentPassword(email, newPassword);
            session.invalidate();
            request.setAttribute("message", "Password successfully set! Please log in with your new password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }else if (email == null || email.isEmpty()) {
            session.setAttribute("message", "Please enter your email first.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Passwords do not match. Please try again.");
            request.getRequestDispatcher("views/setPassword.jsp").forward(request, response);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

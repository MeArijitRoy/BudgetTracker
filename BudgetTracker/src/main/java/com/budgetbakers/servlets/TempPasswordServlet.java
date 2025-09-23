package com.budgetbakers.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.budgetbakers.services.UserService;

@WebServlet("/TempPasswordServlet")
public class TempPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();
	
    public TempPasswordServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String tempPassword = request.getParameter("tempPassword");

        if (userService.verifyTempPassword(email, tempPassword)) {
            request.getRequestDispatcher("views/setPassword.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Invalid temporary password. Please try again.");
            request.getRequestDispatcher("views/tempPassword.jsp").forward(request, response);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

package com.budgetbakers.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        boolean isPublicResource = requestURI.startsWith(httpRequest.getContextPath() + "/login.jsp") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/LoginServlet") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/GoogleCallbackServlet") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/views/tempPassword.jsp") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/TempPasswordServlet") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/views/setPassword.jsp") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/PasswordServlet") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/views/password.jsp") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/SetPasswordServlet") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/css/") ||
                                   requestURI.startsWith(httpRequest.getContextPath() + "/js/");

        if (isLoggedIn || isPublicResource) {
            chain.doFilter(request, response);
        } else {
            logger.warn("Unauthorized access attempt to: {}", requestURI);
            httpRequest.getSession().setAttribute("message", "You must be logged in to access that page.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed.");
    }
}
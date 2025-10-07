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

/**
 * A servlet filter that intercepts all incoming requests to enforce application security.
 * It checks if a user session exists and is valid. If the user is not authenticated,
 * it redirects them to the login page, unless the requested resource is explicitly public.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    /**
     * Called by the web container to indicate to a filter that it is being placed into service.
     * Logs the initialization of the filter.
     *
     * @param filterConfig A filter configuration object used by the web container to pass information to a filter during initialization.
     * @throws ServletException if an exception occurs that interferes with the filter's normal operation.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized.");
    }

    /**
     * The core method of the filter, called by the container for each request.
     * This method checks if the user is logged in. If they are, or if they are accessing a public resource,
     * it allows the request to continue. Otherwise, it redirects them to the login page.
     *
     * @param request  The {@link ServletRequest} object containing the client's request.
     * @param response The {@link ServletResponse} object containing the filter's response.
     * @param chain    The {@link FilterChain} for invoking the next filter or the resource at the end of the chain.
     * @throws IOException if an I/O error occurs during processing.
     * @throws ServletException if a servlet error occurs during processing.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        // A whitelist of resources that do not require authentication.
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
            // User is logged in or accessing a public page, so allow the request to proceed.
            chain.doFilter(request, response);
        } else {
            // User is not logged in and is trying to access a protected resource. Redirect to login.
            logger.warn("Unauthorized access attempt to: {}", requestURI);
            httpRequest.getSession().setAttribute("message", "You must be logged in to access that page.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service.
     * Logs the destruction of the filter.
     */
    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed.");
    }
}

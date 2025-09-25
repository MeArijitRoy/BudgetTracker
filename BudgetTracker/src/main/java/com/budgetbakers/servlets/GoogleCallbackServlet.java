package com.budgetbakers.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


@WebServlet("/GoogleCallbackServlet") 
public class GoogleCallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(GoogleCallbackServlet.class);
	private String GOOGLE_CLIENT_ID;
    private String GOOGLE_CLIENT_SECRET;
    private String GOOGLE_REDIRECT_URI;

    @Override
    public void init() throws ServletException {

        try (InputStream input = getServletContext().getResourceAsStream("/WEB-INF/classes/google-config.properties")) {
            Properties props = new Properties();
            if (input == null) {
            	logger.error("Cannot find google-config.properties");
                throw new ServletException("Cannot find google-config.properties");
            }
            props.load(input);
            GOOGLE_CLIENT_ID = props.getProperty("google.client.id");
            GOOGLE_CLIENT_SECRET = props.getProperty("google.client.secret");
            GOOGLE_REDIRECT_URI = props.getProperty("google.redirect.uri");
        } catch (IOException e) {
        	logger.error("Failed to load Google configuration from properties file", e);
            throw new ServletException("Error loading Google configuration", e);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String error = request.getParameter("error");

        if (error != null) {
        	logger.error("Google login failed. Access was denied.");
            response.sendRedirect("login.jsp?error=Google login failed. Access was denied.");
            return;
        }

        if (code == null) {
        	logger.error("Google login failed. Authorization code was missing.");
            response.sendRedirect("login.jsp?error=Google login failed. Authorization code was missing.");
            return;
        }

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    GOOGLE_CLIENT_ID,
                    GOOGLE_CLIENT_SECRET,
                    code,
                    GOOGLE_REDIRECT_URI)
                    .execute();

            String idTokenString = tokenResponse.getIdToken();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(java.util.Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                UserService userService = new UserService();
                User user = userService.findOrCreateUserFromGoogle(email);

                if (user != null) {
                	if ("LOCAL".equals(user.getAuthProvider())) {
                		HttpSession session = request.getSession();
                        session.setAttribute("email", user.getEmail());
                        if (user.isTemp()) {
                            response.sendRedirect("views/tempPassword.jsp");
                        } else {
                            response.sendRedirect("views/password.jsp");
                        }
                    } else {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        response.sendRedirect("views/home.jsp");
                    }
                } else {
                	logger.error("Could not process your Google login.");
                    response.sendRedirect("login.jsp?error=Could not process your Google login. Please try again.");
                }

            } else {
            	logger.error("Invalid ID token from Google.");
                response.sendRedirect("login.jsp?error=Invalid ID token from Google.");
            }
        } catch (TokenResponseException e) {
        	 logger.error("An error (TokenResponseException) occurred during Google authentication.");
             response.sendRedirect("login.jsp?error=An error occurred during Google authentication. Please try again.");
        } catch (Exception e) {
            logger.error("Error during Google authentication.", e);
            throw new ServletException("Error during Google authentication.", e);
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
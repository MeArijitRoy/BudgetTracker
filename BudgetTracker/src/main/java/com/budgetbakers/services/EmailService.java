package com.budgetbakers.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

	private static final Logger logger = LogManager.getLogger(EmailService.class);
    private Properties emailProps = new Properties();

    public EmailService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                logger.error("FATAL: Could not find 'email.properties' in the classpath. EmailService will not be able to send emails.");
                return;
            }
            emailProps.load(input);
        } catch (IOException ex) {
            logger.error("Failed to load email.properties due to an IOException. EmailService may not function correctly.", ex);
        }
    }

    public void sendTemporaryPassword(String toEmail, String tempPassword) {

        final String fromEmail = emailProps.getProperty("mail.smtp.username");
        final String password = emailProps.getProperty("mail.smtp.password");

        Properties props = new Properties();
        props.put("mail.smtp.host", emailProps.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", emailProps.getProperty("mail.smtp.port"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your Temporary Password");
            message.setText("Welcome to the application!\n\n"
                          + "Your temporary password is: " + tempPassword + "\n\n"
                          + "Please use this to log in and set your new password.");

            Transport.send(message);

            logger.info("Temporary password email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send temporary password email to: {}", toEmail, e);
        }
    }
}
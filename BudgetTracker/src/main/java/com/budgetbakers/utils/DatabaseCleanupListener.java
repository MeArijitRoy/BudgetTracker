package com.budgetbakers.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

/**
 * A ServletContextListener that handles the graceful shutdown of the MySQL
 * JDBC driver's abandoned connection cleanup thread. This listener is automatically
 * registered via the @WebListener annotation and prevents memory leaks and error
 * messages in the server logs when the web application is stopped or reloaded.
 */
@WebListener
public class DatabaseCleanupListener implements ServletContextListener {
	private static final Logger logger = LogManager.getLogger(DatabaseCleanupListener.class);
	
    /**
     * Called by the container when the web application is first initialized.
     * No action is required by this listener during application startup.
     *
     * @param sce The ServletContextEvent containing the ServletContext that is being initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No action needed on startup.
    }

    /**
     * Called by the container when the web application is about to be shut down.
     * This method invokes the shutdown hook for the MySQL abandoned connection cleanup thread
     * to ensure it terminates properly.
     *
     * @param sce The ServletContextEvent containing the ServletContext that is being destroyed.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            logger.info("Successfully shut down MySQL AbandonedConnectionCleanupThread.");
        } catch (Exception e) {
        	logger.error("Failed to shut down AbandonedConnectionCleanupThread during context destruction.", e);
        }
    }
}


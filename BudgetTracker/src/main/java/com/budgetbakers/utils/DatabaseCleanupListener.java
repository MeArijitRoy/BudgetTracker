package com.budgetbakers.utils;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class DatabaseCleanupListener implements ServletContextListener {
	private static final Logger logger = LogManager.getLogger(DatabaseCleanupListener.class);
	
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
        	logger.info("Attempting to shut down the AbandonedConnectionCleanupThread...");
            AbandonedConnectionCleanupThread.checkedShutdown();
            logger.info("Successfully shut down the AbandonedConnectionCleanupThread.");
        } catch (Exception e) {
        	logger.error("Failed to shut down AbandonedConnectionCleanupThread during context destruction.", e);
        }
    }
}

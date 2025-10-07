package com.budgetbakers.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Singleton class responsible for managing the database connection.
 * It ensures that only one instance of the connector is created and provides
 * a centralized point for obtaining a connection to the database.
 */
public class DbConnector {
	private static final Logger logger = LogManager.getLogger(DbConnector.class);
    public static DbConnector dbConnector = null;
    private static Connection connection = null;
    private static Properties props = null;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * It loads the database configuration from the 'db.properties' file.
     */
    private DbConnector() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (input == null) {
            	logger.error("Unable to find db.properties");
                return;
            }
            props = new Properties();
            props.load(input);
        } catch (Exception e) {
        	 logger.error("Error while loading db.properties.", e);
        }
    }

    /**
     * Provides the singleton instance of the DbConnector.
     * If an instance does not exist, it creates one.
     * @return The single instance of the {@link DbConnector}.
     */
    public static DbConnector getInstance() {
        if (dbConnector == null) {
            dbConnector = new DbConnector();
        }
        return dbConnector;
    }

    /**
     * Gets a connection to the database.
     * If the current connection is null or closed, it establishes a new one
     * using the loaded properties. Otherwise, it returns the existing connection.
     * @return A {@link Connection} object for the database, or null if an error occurs.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                if (props == null) {
                	logger.error("Failed to create database connection: Database configuration properties could not be loaded.");
                    return null;
                }

                Class.forName(props.getProperty("db.driver"));

                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, username, password);
                
                logger.info("Connection created successfully.");
            }
        } catch (Exception e) {
        	logger.error("Error while connecting to db.", e);
        }
        return connection;
    }
}

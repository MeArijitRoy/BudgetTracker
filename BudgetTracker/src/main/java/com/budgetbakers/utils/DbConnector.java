package com.budgetbakers.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbConnector {
	private static final Logger logger = LogManager.getLogger(DbConnector.class);
    public static DbConnector dbConnector = null;
    private static Connection connection = null;
    private static Properties props = null;

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

    public static DbConnector getInstance() {
        if (dbConnector == null) {
            dbConnector = new DbConnector();
        }
        return dbConnector;
    }

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


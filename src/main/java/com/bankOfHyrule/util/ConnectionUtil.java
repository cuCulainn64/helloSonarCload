package com.bankOfHyrule.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;


/**
 * Used to establish a connection to the database using you system environment variables
 * */
public class ConnectionUtil {

	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);

	/**
	 * Used to establish a connection to the database using you system environment variables
	 * */
	public static Connection getConnection() {


		Connection conn = null;

		try {
			
			logger.debug("Making a database connection");
			
			conn = DriverManager.getConnection(
					System.getenv("db_url"), 
					System.getenv("db_username"), 
					System.getenv("db_password")
				);
			logger.debug("Connection has been successfully established.");
		} catch (SQLException e) {
			logger.warn("Unable to obtain connection to database", e);
		}

		return conn;
	}
}

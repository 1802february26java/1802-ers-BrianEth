package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ConnectionUtil {

	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);

	public static Connection getConnection() throws SQLException{
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e)
		{
			logger.error("class not found for driver");
		}
		String url = "jdbc:oracle:thin:@revaturebankdb.cxcscohikus1.us-east-2.rds.amazonaws.com:1521:ORCL";
		String username = "REIMBURSEMENT_DB";
		String password = "p4ssw0rd";
		return DriverManager.getConnection(url, username, password);
	}
}

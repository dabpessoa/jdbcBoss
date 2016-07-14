package me.dabpessoa.jdbcBoss.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnection {

	public static final String USER_DRIVER_PROPERTY_NAME = "user";
	public static final String PASSWORD_DRIVER_PROPERTY_NAME = "password";
	public static final String URL_DRIVER_PROPERTY_NAME = "url";
	
	public Connection createConnection(String url, String user, String password) throws ClassNotFoundException, SQLException {
		return createConnection(url, user, password, null);
	}
	
	public Connection createConnection(String url, String user, String password, String dataBaseDriverClassName) throws ClassNotFoundException, SQLException {
		return createConnection(createProperties(url, user, password), dataBaseDriverClassName);
	}
	
	public Connection createConnection(Properties properties, String dataBaseDriverClassName) throws ClassNotFoundException, SQLException {
		Class.forName(dataBaseDriverClassName);
		Connection connection = DriverManager
						.getConnection(properties.getProperty(URL_DRIVER_PROPERTY_NAME), 
										properties.getProperty(USER_DRIVER_PROPERTY_NAME),
										properties.getProperty(PASSWORD_DRIVER_PROPERTY_NAME));
		
		return connection;
	}
	
	private Properties createProperties(String url, String user, String password) {
		Properties properties = new Properties();
		properties.put(URL_DRIVER_PROPERTY_NAME, url);
		properties.put(USER_DRIVER_PROPERTY_NAME, user);
		properties.put(PASSWORD_DRIVER_PROPERTY_NAME, password);
		return properties;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		JDBCConnection t = new JDBCConnection();
		Connection c = t.createConnection("jdbc:postgresql://localhost:5432/atma", "postgres", "postgres", "org.postgresql.Driver");
		System.out.println(c);
	}
	
}

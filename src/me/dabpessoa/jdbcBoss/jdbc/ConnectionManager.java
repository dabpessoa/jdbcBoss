package me.dabpessoa.jdbcBoss.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

	private JDBCConnection jdbcConnection;
	private ConnectionProperties connectionProperties;
	
	public ConnectionManager(ConnectionProperties connectionProperties) {
		this.connectionProperties = connectionProperties;
		jdbcConnection = new JDBCConnection();
	}
	
	public <T> T execute(ConnectionCallback<T> callback) {
		Connection connection = null;
		try {
			
			connection = jdbcConnection.createConnection(connectionProperties.getUrl(), connectionProperties.getUser(), connectionProperties.getPassword(), connectionProperties.getJdbcDriverClassFullName());
			return callback.execute(connection);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void closeConnection(Connection connection) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
	
}

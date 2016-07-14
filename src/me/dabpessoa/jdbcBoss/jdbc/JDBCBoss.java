package me.dabpessoa.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCBoss implements JDBCQuerable {

	private ConnectionManager manager;
	
	public JDBCBoss(ConnectionProperties connectionProperties) {
		manager = new ConnectionManager(connectionProperties);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper) {
		return (List<T>) queryAnyObject(sql, mapper, true);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper) {
		return (T) queryAnyObject(sql, mapper, false);
	}
	
	private <T> Object queryAnyObject(String sql, ResultSetObjectMapper<T> mapper, boolean isQueryList) {
		return manager.execute(new ConnectionExecuteCallback<Object>() {
			@Override
			public Object execute(Connection connection) throws SQLException {
				Statement stm = connection.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				if (!isQueryList) {
					rs.next();
					return mapper.map(rs, 1);
				} else {
					List<T> list = new ArrayList<T>();
					int rowCount = 0;
					while (rs.next()) {
						list.add(mapper.map(rs, ++rowCount));
					}
					return list;
				}
			}
		});
	}	
	
	@Override
	public int insert(String sql) {
		return executeAction(sql);
	}
	
	@Override
	public int update(String sql) {
		return executeAction(sql);
	}
	
	@Override
	public int delete(String sql) {
		return executeAction(sql);
	}
	
	private int executeAction(String sql) {
		return manager.execute(new ConnectionExecuteCallback<Integer>() {
			@Override
			public Integer execute(Connection connection) throws SQLException {
				PreparedStatement pstm = connection.prepareStatement(sql);
				return pstm.executeUpdate();
			}
		});
	}
	
}

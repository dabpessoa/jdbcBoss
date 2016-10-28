package me.dabpessoa.jdbcBoss.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionCallback<T> {

	T execute(Connection connection) throws SQLException;
	
}

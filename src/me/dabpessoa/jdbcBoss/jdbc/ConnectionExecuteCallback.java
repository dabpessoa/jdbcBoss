package me.dabpessoa.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionExecuteCallback<T> {

	T execute(Connection connection) throws SQLException;
	
}

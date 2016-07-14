package me.dabpessoa.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetObjectMapper<T> {

	T map(ResultSet rs, int rowNum) throws SQLException;
	
}

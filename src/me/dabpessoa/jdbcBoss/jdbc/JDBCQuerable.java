package me.dabpessoa.jdbcBoss.jdbc;

import java.util.List;

public interface JDBCQuerable {

	<T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper);
	<T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper);
	int insert(String sql);
	int update(String sql);
	int delete(String sql);
	
}

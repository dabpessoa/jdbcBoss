package me.dabpessoa.jdbcBoss.jdbc;

import java.util.List;
import java.util.Map;

public interface JDBCQuerable {

	<T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper);
	<T> List<T> queryList(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap);
	<T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper);
	<T> T querySingleResult(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap);
	int insert(String sql);
	int update(String sql);
	int delete(String sql);
	
}

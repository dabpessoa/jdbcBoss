package me.dabpessoa.jdbcBoss.jdbc;

import java.util.List;
import java.util.Map;

public interface JDBCQuerable {

	<T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper);
	
	/**
	 * 
	 * @param sql Comando padrão SQL a ser enviado para o banco de dados.
	 * @param clazz Classe que representa as colunas do banco de dados que se deseja consultar.
	 * @param wantedFieldsMap Mapa dos nomes dos atributos das entidades e seus respectivos nomes no banco de dados.
	 * @return
	 */
	<T> List<T> queryList(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap);
	
	<T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper);
	
	/**
	 * 
	 * @param sql Comando padrão SQL a ser enviado para o banco de dados.
	 * @param clazz Classe que representa as colunas do banco de dados que se deseja consultar.
	 * @param wantedFieldsMap Mapa dos nomes dos atributos das entidades e seus respectivos nomes no banco de dados.
	 * @return
	 */
	<T> T querySingleResult(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap);
	
	int execute(String sql);
	
}

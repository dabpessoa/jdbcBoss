package me.dabpessoa.jdbcBoss.jdbc;

import java.util.List;
import java.util.Map;

/**
 * @author diego.pessoa [dabpessoa@gmail.com]
 */
public interface JDBCQuerable {

	<T> List<T> queryList(Class<T> clazz, String tableName, Map<String, Object> selectFieldsMap, Map<String, Object> whereFieldsMap);
	
	<T> List<T> queryList(Class<T> clazz, String tableName, Map<String, Object> selectFieldsMap);
	
	long count(String tableName);
	
	long count(String tableName, Map<String, Object> whereFieldsMap);
	
	<T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper);
	
	/**
	 * 
	 * @param sql Comando padrão SQL a ser enviado para o banco de dados.
	 * @param clazz Classe que representa as colunas do banco de dados que se deseja consultar.
	 * @param wantedFieldsMap Mapa dos nomes dos atributos das entidades e seus respectivos nomes no banco de dados.
	 * @return
	 */
	<T> List<T> queryList(String sql, Class<T> clazz, Map<String, Object> selectFieldsMap);
	
	<T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper);
	
	/**
	 * 
	 * @param sql Comando padrão SQL a ser enviado para o banco de dados.
	 * @param clazz Classe que representa as colunas do banco de dados que se deseja consultar.
	 * @param wantedFieldsMap Mapa dos nomes dos atributos das entidades e seus respectivos nomes no banco de dados.
	 * @return
	 */
	<T> T querySingleResult(String sql, Class<T> clazz, Map<String, Object> selectFieldsMap);
	
	
	<T> T querySingleObject(String sql);
	
	/**
	 * 
	 * Consulta uma lista de uma única coluna de uma determinada tabela especificada na consulta. Caso a consulta seja
	 * um "select * ...", então uma lista de da primeira coluna da tabela será retornada.
	 * 
	 * @param sql comando SQL à ser enviado para o banco de dados.
	 * @return List<T> Lista de uma determinada coluna do banco de dados.
	 */
	<T> List<T> queryObjectList(String sql);
	
	int execute(String sql);
	
}

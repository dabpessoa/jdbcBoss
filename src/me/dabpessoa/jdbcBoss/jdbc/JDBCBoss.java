package me.dabpessoa.jdbcBoss.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.dabpessoa.jdbcBoss.temp.GenericRowMapperFactory;
import me.dabpessoa.jdbcBoss.util.ReflectionUtils;

public class JDBCBoss implements JDBCQuerable {

	private ConnectionManager manager;
	
	public JDBCBoss(ConnectionProperties connectionProperties) {
		manager = new ConnectionManager(connectionProperties);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> queryList(String sql, ResultSetObjectMapper<T> mapper) {
		return (List<T>) queryAnyObject(sql, mapper, true);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> queryList(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap) {
		ResultSetObjectMapper<T> mapper = createGenericResultSetObjectMapper(clazz, wantedFieldsMap);
		return (List<T>) queryAnyObject(sql, mapper, true);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T querySingleResult(String sql, ResultSetObjectMapper<T> mapper) {
		return (T) queryAnyObject(sql, mapper, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T querySingleResult(String sql, Class<T> clazz, Map<String, String> wantedFieldsMap) {
		ResultSetObjectMapper<T> mapper = createGenericResultSetObjectMapper(clazz, wantedFieldsMap);
		return (T) queryAnyObject(sql, mapper, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T querySingleObject(String sql) {
		return (T) queryAnyObject(sql, null, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> queryObjectList(String sql) {
		return (List<T>) queryAnyObject(sql, null, true);
	}
	
	/**
	 * INSERT, UPDATE and DELETE
	 * @param sql
	 * @return
	 */
	@Override
	public int execute(String sql) {
		return manager.execute(new ConnectionExecuteCallback<Integer>() {
			@Override
			public Integer execute(Connection connection) throws SQLException {
				PreparedStatement pstm = connection.prepareStatement(sql);
				return pstm.executeUpdate();
			}
		});
	}
	
	private <T> Object queryAnyObject(String sql, ResultSetObjectMapper<T> mapper, boolean isQueryList) {
		return manager.execute(new ConnectionExecuteCallback<Object>() {
			@Override
			@SuppressWarnings("unchecked")
			public Object execute(Connection connection) throws SQLException {
				Statement stm = connection.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				if (!isQueryList) {
					rs.next();
					if (mapper != null) return mapper.map(rs, 1);
					else {
						return rs.getObject(1);
					}
				} else {
					List<T> list = new ArrayList<T>();
					int rowCount = 0;
					while (rs.next()) {
						if (mapper != null) list.add(mapper.map(rs, ++rowCount));
						else {							
							list.add((T) rs.getObject(1));
						}
					}
					return list;
				}
			}
		});
	}
	
	/*
	 * wantedFieldsMap => Mapa dos nomes dos atributos das entidades e seus respectivos nomes no banco de dados.
	 */
	private <T> ResultSetObjectMapper<T> createGenericResultSetObjectMapper(Class<T> clazz, Map<String, String> wantedFieldsMap) {
		
		ResultSetObjectMapper<T> mapper = new ResultSetObjectMapper<T>() {
			@Override
			public T map(ResultSet rs, int rowNum) throws SQLException {
				
				T entity = null;
				try {
					entity = clazz.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				for (Entry<String, String> entry : wantedFieldsMap.entrySet()) {
					String entityFieldName = entry.getKey(); // Entity field name
					String databaseColumnName = entry.getValue(); // Database column name
				
					Field field = ReflectionUtils.findFieldByName(clazz, entityFieldName);
					
					Object fieldValue;
					try {
						fieldValue = rs.getObject(databaseColumnName);
					} catch (SQLException e) {
						throw new SQLException("Erro ao setar valor no RowMapper. ("+GenericRowMapperFactory.class.getSimpleName()+"). Field Name = "+field.getName()+", Field Type = "+field.getType(), e);
					}
					
					try {
						// Aplica transformações básicas.
						fieldValue = basicFieldTypeTransform(fieldValue, field);
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
					
					try {
						ReflectionUtils.setFieldValue(entity, field, fieldValue);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					
				}
				
				return entity;
				
			}
		};
		
		return mapper;
		
	}
	
	private Object basicFieldTypeTransform(Object value, Field field) throws Exception {

		Type fieldType = field.getType();
		String fieldName = field.getName();
		
		if (value != null) {
		
			try {
				
				String myValue = value.toString();
				myValue = myValue.trim();
				
				if (Integer.class.equals(fieldType)) {
					value = new Double(myValue).intValue();
				} else if (String.class.equals(fieldType)) {
					value = myValue;
				} else if (BigDecimal.class.equals(fieldType)) {
					value = new BigDecimal(myValue);
				} else if (Long.class.equals(fieldType)) {
					value = Long.parseLong(myValue);
				} else if (Byte.class.equals(fieldType)) {
					value = Byte.parseByte(myValue);
				} else if (Short.class.equals(fieldType)) {
					value = Short.parseShort(myValue); 
				} else if (Double.class.equals(fieldType)) {
					value = Double.parseDouble(myValue);
				} else if (Float.class.equals(fieldType)) {
					value = Float.parseFloat(myValue);
				} else if (BigInteger.class.equals(fieldType)) {
					value = new BigInteger(myValue);
				}
				
			} catch (NumberFormatException e) {
				throw new Exception("Não foi possível setar o valor: "+value+", no campo: "+fieldName+", do tipo: "+fieldType+", da classe: "+field.getDeclaringClass()+". Erro de conversão de tipo.", e);
			}
			
		}
		
		return value;

	}
	
}

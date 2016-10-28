package me.dabpessoa.jdbcBoss.util;

import java.util.Map;

public class QueryGenerator {

	public static final int EXECUTE_INSERT = 0;
	public static final int EXECUTE_UPDATE = 1;
	public static final int EXECUTE_DELETE = 2;
	public static final int EXECUTE_QUERY = 3;
	public static final int EXECUTE_COUNT = 4;
	
	public static <T> String generateQuery(String tableName, Map<String, Object> fieldsMap, Map<String, Object> whereFieldsMap, Integer executeType) {
		
		StringBuffer sql = null;
		
		switch (executeType) {
			case EXECUTE_COUNT: {
				
				sql = new StringBuffer("select count(1) from "+tableName+" ");
				
				if (whereFieldsMap != null) {
					String[] wherekeys = whereFieldsMap.keySet().toArray(new String[whereFieldsMap.keySet().size()]);
					sql.append(" where ");
					for(int i = 0 ; i < wherekeys.length ; i++) {
						if (i+1 != wherekeys.length) sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i])+" and ");
						else sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i]));
					}
				}
				
				break;
			}
		
			case EXECUTE_QUERY: {
				
				// TODO FIXME implementar
				
				break;
			}
		
			case EXECUTE_INSERT: {
				
				if (fieldsMap == null) throw new RuntimeException("Campos para o insert est�o nulos");
				String[] keys = fieldsMap.keySet().toArray(new String[fieldsMap.keySet().size()]);
				
				sql = new StringBuffer();
				sql.append("insert into "+tableName+" (");
				for(int i = 0 ; i < keys.length ; i++) {
					if (i+1 != keys.length) sql.append(keys[i]+", ");
					else sql.append(keys[i]);
				}
				
				sql.append(") values (");
				
				for(int i = 0 ; i < keys.length ; i++) {
					if (i+1 != keys.length) sql.append(fieldsMap.get(keys[i])+", ");
					else sql.append(fieldsMap.get(keys[i]));
				}
				
				sql.append(")");
				
				break;
			}
			
			case EXECUTE_UPDATE: {
				
				if (fieldsMap == null) throw new RuntimeException("Campos para o update est�o nulos");
				String[] keys = fieldsMap.keySet().toArray(new String[fieldsMap.keySet().size()]);
				
				sql = new StringBuffer();
				sql.append("update "+tableName+" set ");
				for(int i = 0 ; i < keys.length ; i++) {
					if (i+1 != keys.length) sql.append(keys[i]+"="+fieldsMap.get(keys[i])+", ");
					else sql.append(keys[i]+"="+fieldsMap.get(keys[i]));
				}
				
				if (whereFieldsMap != null) {
					String[] wherekeys = whereFieldsMap.keySet().toArray(new String[whereFieldsMap.keySet().size()]);
					sql.append(" where ");
					for(int i = 0 ; i < wherekeys.length ; i++) {
						if (i+1 != wherekeys.length) sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i])+" and ");
						else sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i]));
					}
				}
				
				break;
			}
			
			case EXECUTE_DELETE: {
				
				sql = new StringBuffer();
				sql.append("delete from "+tableName+" ");
				if (whereFieldsMap != null) {
					String[] wherekeys = whereFieldsMap.keySet().toArray(new String[whereFieldsMap.keySet().size()]);
					sql.append(" where ");
					for(int i = 0 ; i < wherekeys.length ; i++) {
						if (i+1 != wherekeys.length) sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i])+" and ");
						else sql.append(wherekeys[i]+"="+whereFieldsMap.get(wherekeys[i]));
					}
				}
				
				break;
				
			}

			default: {
				sql = new StringBuffer("Opção inválida de SQL, os valores aceitos são: 'INSERT, UPDATE E DELETE'");
			}
			
		}
				
		return sql != null ? sql.toString() : null;
		
	}
	
}

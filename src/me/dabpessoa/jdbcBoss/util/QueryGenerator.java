package me.dabpessoa.jdbcBoss.util;

public class QueryGenerator {

	private static final int EXECUTE_INSERT = 0;
	private static final int EXECUTE_UPDATE = 1;
	private static final int EXECUTE_DELETE = 2;
	
	public <T> String generateQuery(Class<T> clazz, String[] campos, Object[] values, String updateWhereClause, Integer executeType, String version) {
		
		StringBuffer sql = null;
		String tableName = ReflectionUtils.findSchemaTableName(clazz);
		
		// Se somente um dos vetores é nulo ou se os dois são diferentes de nulo e t�m quantidades diferentes de registros.
		if ( ((campos == null && values != null) || (values == null && campos != null)) ||
			 ((campos != null && values != null) && (campos.length != values.length)) ) {
			throw new RuntimeException("Quantidade de campos diferente da quantidade de valores.");
		}
		
		switch (executeType) {
			case EXECUTE_INSERT: {
				
				if (campos == null || values == null) throw new RuntimeException("Campos ou valores nulos para a operação de INSERT.");
			
				sql = new StringBuffer();
				sql.append("insert into "+tableName+" (");
				for(int i = 0 ; i < campos.length ; i++) {
					if (i+1 != campos.length) sql.append(campos[i]+", ");
					else sql.append(campos[i]);
				}
				
//				// Recuperar o nome do campo da versão do registro.
//				String columnVersionName = null;
//				try {
//					columnVersionName = ReflectionUtilsfindVersionFieldAnnotationName(clazz);
//					if (columnVersionName == null) columnVersionName = findVersionFieldName(clazz);
//				} catch (UniqueAnnotationException e) {
//					throw new RuntimeException(e);
//				}
				
//				boolean campoVersionAdicionado = false;
//				if (columnVersionName != null && !columnVersionName.isEmpty()) {
//					// Verificar se o campo de versão já está no insert caso contrário colocá-lo.
//					if (sql.indexOf(columnVersionName) == -1) {
//						sql.append(", "+columnVersionName);
//						campoVersionAdicionado = true;
//					}
//				}
				
				sql.append(") values (");
				
				for(int i = 0 ; i < values.length ; i++) {
					if (i+1 != values.length) sql.append(values[i]+", ");
					else sql.append(values[i]);
				}
				
//				if (campoVersionAdicionado) {
//					sql.append(", 0");
//				}
				
				sql.append(")");
				
				break;
			}
			
			case EXECUTE_UPDATE: {
				
				if (campos == null || values == null) throw new RuntimeException("Campos ou valores nulos para a operação de UPDATE.");
				
//				String columnVersionName = null;
//				try {
//					columnVersionName = findVersionFieldAnnotationName(clazz);
//					if (columnVersionName == null) columnVersionName = findVersionFieldName(clazz);
//				} catch (UniqueAnnotationException e) {
//					throw new RuntimeException(e);
//				}
//				if (columnVersionName != null && !columnVersionName.isEmpty() && version == null && executeType == EXECUTE_UPDATE) {
//					// No caso de ter sido mapeado uma coluna de vers�o e esta estar nula deve ser levantada uma exce��o.
//					throw new JdbcEmptyVersionException("Não é permitido um valor de versão nulo no momento do update.");
//				}
				
				sql = new StringBuffer();
				sql.append("update "+tableName+" set ");
				for(int i = 0 ; i < campos.length ; i++) {
					if (i+1 != campos.length) sql.append(campos[i]+"="+values[i]+", ");
					else sql.append(campos[i]+"="+values[i]);
				}
				
//				if (columnVersionName != null && !columnVersionName.isEmpty()) {
//					sql.append(", "+columnVersionName+"="+new BigDecimal(version).add(new BigDecimal(1)));
//				}
//				
//				if (updateWhereClause != null && !updateWhereClause.isEmpty()) {
//					sql.append(" "+updateWhereClause);
//					if (columnVersionName != null && !columnVersionName.isEmpty()) {
//						sql.append(" and "+columnVersionName+"="+version);
//					}
//				}
				
				break;
			}
			
			case EXECUTE_DELETE: {
				
				sql = new StringBuffer();
				sql.append("delete from "+tableName+" ");
				if (campos != null && values != null) {
					sql.append(" where ");
					for(int i = 0 ; i < campos.length ; i++) {
						if (i+1 != campos.length) sql.append(campos[i]+"="+values[i]+" and ");
						else sql.append(campos[i]+"="+values[i]);
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

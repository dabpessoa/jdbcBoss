//package temp;
//
//import java.lang.reflect.Field;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Logger;
//
//import javax.persistence.Id;
//import javax.persistence.Version;
//import javax.sql.DataSource;
//
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
///**
// * 
// * Classe responsável por auxiliar e gerenciar as consultas em SQL através da utilização
// * do JDBC puro. 
// * 
// * Ainda utiliza-se aqui uma outra classe auxiliar pertencente ao framework do Spring, essa da qual
// * facilita as consultas ao banco via SQL puro e JDBC, e trata a abertura e fechamento de transações e também os roolbacks. 
// * 
// * @author diego.pessoa [dabpessoa@gmail.com]
// * @since 17.06.2013
// *
// */
//
//@Repository
//public class JdbcManager {
//	// Atributo responsável por fazer os logs dos comandos SQL.
//	public static Logger LOGGER = Logger.getLogger(JdbcManager.class.getName());
//	
//	/*
//	 *  atributos estáticos para controle de ações.
//	 */
//	private static final int EXECUTE_INSERT = 0;
//	private static final int EXECUTE_UPDATE = 1;
//	private static final int EXECUTE_DELETE = 2;
//	
//	private JdbcTemplate jdbc;
//
//	public JdbcManager() {}
//	
//	/**
//	 * 
//	 * Retorna o número de linhas da tabela referente
//	 * à entidade solicitada. Deve estar presente nesta entidade
//	 * a anotação que informa o nome da tabela: "javax.persistence.Table".
//	 * 
//	 * @param sql
//	 * @return
//	 */
//	public <T> int count(Class<T> clazz) {
//		String tableName = ReflectionUtils.findSchemaTableName(clazz);
//		String sql = "select count(*) from "+tableName;
//		LOGGER.info(sql);
//		return jdbc.queryForInt(sql);
//	}
//
//	/**
//	 * 
//	 * Retorna um objeto primitivo a partir do SQL passado por parâmetro.
//	 * Aqui o sql deve possuir um select para apenas uma coluna da entidade
//	 * em quest�o. 
//	 * 
//	 * @param sql
//	 * @param clazz
//	 * @param params
//	 * @return
//	 */
//	public <T> T querySingleColumn(String sql, Class<T> clazz, Object... params) {
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(params));
//		LOGGER.info(sql);
//		return jdbc.queryForObject(sql, params, clazz);
//	}
//	
//	/**
//	 * 
//	 * Retorna um objeto a partir do SQL e do RowMapper passados por parâmetro.
//	 * Neste método é possóvel recuperar mais de uma coluna da entidade em questão.
//	 * 
//	 * @param sql
//	 * @param rowMapper
//	 * @param params
//	 * @return
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 */
//	public <T> T query(String sql, RowMapper<T> rowMapper, Object... params) throws InstantiationException, IllegalAccessException {
//		sql = verifySelectOptimisticLock(rowMapper, sql);
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(params));
//		LOGGER.info(sql);
//		try {
//			return jdbc.queryForObject(sql, params, rowMapper);
//		} catch (EmptyResultDataAccessException e) {
//			return null;
//		}
//	}
//	
//	/**
//	 * 
//	 * Retorna uma lista de objetos de acordo com o RowMapper passado como parâmetro.
//	 * 
//	 * @param sql
//	 * @param rowMapper
//	 * @param params
//	 * @return
//	 */
//	public <T> List<T> list(String sql, RowMapper<T> rowMapper, Object... params) {
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(params));
//		sql = verifySelectOptimisticLock(rowMapper, sql);
//		LOGGER.info(sql);
//		return jdbc.query(sql, params, rowMapper);
//	}
//	
//	public int insert(Object entity, String fields[], Object values[]) {
//		Object version = null;
//		try {
//			version = findVersionValue(entity);
//		} catch (UniqueAnnotationException e) {
//			throw new RuntimeException(e);
//		}
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(fields)+" -> "+Arrays.toString(values));
//		return execute(entity.getClass(), fields, values, EXECUTE_INSERT, null, version != null ? version.toString() : null);
//	}
//	
//	public int insert(Object entity, Map<String, Object> valuesMap) {
//		
//		String fields[] = new String[valuesMap.size()];
//		Object values[] = new Object[valuesMap.size()];
//		
//		int index = 0;
//		for(final Map.Entry<String, Object> entry : valuesMap.entrySet()) {
//			fields[index] = entry.getKey();
//			values[index] = entry.getValue();
//			index++;
//		}
//		
//		return insert(entity, fields, values);
//	}
//	
//	public int delete(Object entity, String whereFields[], Object whereValues[]) {
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(whereFields)+" -> "+Arrays.toString(whereValues));
//		return execute(entity.getClass(), whereFields, whereValues, EXECUTE_DELETE, null, null);
//	}
//	
//	public int update(Object entity, String fields[], String values[], String whereFields[], String whereValues[]) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
//		Object version = null;
//		try {
//			if (verifyVersionAnnotation(entity.getClass())) verificaVersionamentoLock(entity);
//			version = findVersionValue(entity);
//		} catch (UniqueAnnotationException e) {
//			throw new RuntimeException(e);
//		}
//		LOGGER.info("SQL PARAMETERS: "+Arrays.toString(fields)+" -> "+Arrays.toString(values));
//		String updateWhereClause = generateWhereClause(whereFields, whereValues);
//		return this.execute(entity.getClass(), fields, values, EXECUTE_UPDATE, updateWhereClause != null? updateWhereClause.toString() : null, version != null ? version.toString() : null);
//	}
//	
//	/**
//	 * Batch INSERT / UPDATE
//	 * 
//	 * @param sqls
//	 * @return
//	 */
//	public int[] executeBatch(String... sqls) {
//		if (sqls != null && sqls.length != 0)  
//			return jdbc.batchUpdate(sqls);
//		return null;
//	}
//	
//	public int[] batchUpdate(List<Object> entities, List<String[]> camposList, List<String[]> valuesList, List<String[]> whereCamposList, List<String[]> whereValuesList,  int batchSize) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
//		
//		int batch = batchSize;
//		List<Integer> retornos = new ArrayList<Integer>();
//		if (entities.size() == camposList.size() && entities.size() == valuesList.size() && entities.size() == whereCamposList.size() && entities.size() == whereValuesList.size()) {
//			
//			List<String> sqls = new ArrayList<String>();
//			for (int i = 0 ; i < entities.size() ; i++) {
//				
//				Object entity = entities.get(i);
//				String[] campos = camposList.get(i);
//				String[] values = valuesList.get(i);
//				String[] whereCampos = whereCamposList.get(i);
//				String[] whereValues = whereValuesList.get(i);
//				
//				Object version = null;
//				try {
//					if (verifyVersionAnnotation(entity.getClass())) verificaVersionamentoLock(entity);
//					version = findVersionValue(entity);
//				} catch (UniqueAnnotationException e) {
//					throw new RuntimeException(e);
//				}
//				LOGGER.info("SQL PARAMETERS: "+Arrays.toString(campos)+" -> "+Arrays.toString(values));
//				
//				String updateWhereClause = generateWhereClause(whereCampos, whereValues);
//				String sql = generateQuery(entity.getClass(), campos, values, updateWhereClause, EXECUTE_UPDATE, version == null ? null : version.toString());
//				sqls.add(sql);
//				
//				if (i >= batchSize) {
//					int[] results = executeBatch(sqls.toArray(new String[sqls.size()]));
//					if (results != null) {
//						for (int r : results) {
//							retornos.add(r);
//						}
//					}
//					sqls.clear();
//					batchSize = batchSize + batch;
//				}
//				
//			}
//			
//			int results[] = executeBatch(sqls.toArray(new String[sqls.size()]));
//			if (results != null) {
//				for (int r : results) {
//					retornos.add(r);
//				}
//			}
//			
//			int[] rs = new int[retornos.size()];
//			for (int i = 0 ; i < rs.length ; i++) {
//				rs[i] = retornos.get(i);
//			}
//			return rs;
//			
//		} else {
//			throw new RuntimeException("Listas passadas para o batch update possuem tamanho diferentes.");
//		}
//		
//	}
//	
//	public int[] batchInsert(List<Object> entities, List<String[]> camposList, List<String[]> valuesList, int batchSize) {
//		
//		if (!(entities.size() == camposList.size() && entities.size() == valuesList.size())) throw new RuntimeException("Listas passadas para o batch insert possuem tamanho diferentes.");
//		
//		int batch = batchSize;
//		List<String> sqls = new ArrayList<String>();
//		List<Integer> retornos = new ArrayList<Integer>();
//		for (int i = 0 ; i < entities.size() ; i++) {
//		
//			Object entity = entities.get(i);
//			String[] campos = camposList.get(i);
//			Object[] values = valuesList.get(i);
//			
//			Object version = null;
//			try {
//				version = findVersionValue(entity);
//			} catch (UniqueAnnotationException e) {
//				throw new RuntimeException(e);
//			}
//			LOGGER.info("SQL PARAMETERS: "+Arrays.toString(campos)+" -> "+Arrays.toString(values));
//			
//			String sql = generateQuery(entity.getClass(), campos, values, null, EXECUTE_INSERT, version == null? null : version.toString());
//			sqls.add(sql);
//			
//			if (i >= batchSize) {
//				int[] results = executeBatch(sqls.toArray(new String[sqls.size()]));
//				if (results != null) {
//					for (int r : results) {
//						retornos.add(r);
//					}
//				}
//				sqls.clear();
//				batchSize = batchSize + batch;
//			}
//			
//		}
//		
//		int results[] = executeBatch(sqls.toArray(new String[sqls.size()]));
//		if (results != null) {
//			for (int r : results) {
//				retornos.add(r);
//			}
//		}
//		
//		int[] rs = new int[retornos.size()];
//		for (int i = 0 ; i < rs.length ; i++) {
//			rs[i] = retornos.get(i);
//		}
//		return rs;
//		
//	}
//	
//	public <T> void verificaVersionamentoLock(Object entity) throws NumberFormatException, IllegalArgumentException, IllegalAccessException, UniqueAnnotationException {
//		Integer id = Integer.parseInt(findIdValue(entity).toString());
//		String versionName = findVersionFieldAnnotationName(entity.getClass());
//		String tableName = ReflectionUtils.findSchemaTableName(entity.getClass());
//		String idTableColumnName = findIdFieldAnnotationName(entity.getClass());
//		String dataBaseversionValue = new String(this.querySingleColumn("select "+versionName+" from "+tableName+" where "+idTableColumnName+" = ?", Integer.class, id).toString());
//		String entityVersionValue = (String) findVersionValue(entity);
//		if (Long.parseLong(dataBaseversionValue) > Long.parseLong(entityVersionValue)) {
//			throw new JdbcDirtyDataException("Este registro j� foi alterado por outra transa��o.");
//		}
//	}
//	
//	public String generateWhereClause(String[] whereCampos, String[] whereValues) {
//		StringBuffer updateWhereClause = null;
//		// Se somente um dos vetores é nulo ou se os dois são diferentes de nulo e têm quantidades diferentes de registros.
//		if ( ((whereCampos == null && whereValues != null) || (whereValues == null && whereCampos != null)) ||
//			 ((whereCampos != null && whereValues != null) && (whereCampos.length != whereValues.length)) ) {
//			throw new RuntimeException("Quantidade de campos da cláusula where diferente da quantidade de valores.");
//		} else if ((whereCampos != null && whereValues != null) && (whereCampos.length != 0 && whereValues.length != 0)) {
//			updateWhereClause = new StringBuffer();
//			updateWhereClause.append(" where ");
//			for(int j = 0 ; j < whereCampos.length ; j++) {
//				if (j+1 != whereCampos.length) updateWhereClause.append(whereCampos[j]+"="+whereValues[j]+" and ");
//				else updateWhereClause.append(whereCampos[j]+"="+whereValues[j]);
//			}
//		}
//		return updateWhereClause != null ? updateWhereClause.toString() : null;
//	}
//	
//	/**
//	 * 
//	 * INSERT / UPDATE / DELETE
//	 * 
//	 * Executa ações de insert, update ou delete tratando o optimistic locking para controle de versões de registros
//	 * em uma tabela onde for necess�rio
//	 * 
//	 * @param campos
//	 * @param values
//	 * @param clazz
//	 * @param whereClause
//	 * 
//	 * @return int inteiro que representa o número de linhas afetadas pela ação.
//	 * @throws JdbcEmptyVersionException 
//	 * 
//	 */
//	private <T> int execute(Class<T> clazz, String campos[], Object values[], Integer executeType, String updateWhereClause, String version) {
//		String sql = generateQuery(clazz, campos, values, updateWhereClause, executeType, version);
//		LOGGER.info(sql);
//		// A execução retornará o número de linhas afetadas na tabela, caso contrário retornará 0 para os seguintes dois casos:
//		// 1) A execução realmente afetou 0 linhas, ou,
//		// 2) Houve uma execução de uma DDL.
//		return jdbc.update(sql);
//		
//	}
//	
//	public <T> String generateQuery(Class<T> clazz, String[] campos, Object[] values, String updateWhereClause, Integer executeType, String version) {
//		
//		StringBuffer sql = null;
//		String tableName = ReflectionUtils.findSchemaTableName(clazz);
//		
//		// Se somente um dos vetores é nulo ou se os dois são diferentes de nulo e t�m quantidades diferentes de registros.
//		if ( ((campos == null && values != null) || (values == null && campos != null)) ||
//			 ((campos != null && values != null) && (campos.length != values.length)) ) {
//			throw new RuntimeException("Quantidade de campos diferente da quantidade de valores.");
//		}
//		
//		switch (executeType) {
//			case EXECUTE_INSERT: {
//				
//				if (campos == null || values == null) throw new RuntimeException("Campos ou valores nulos para a operação de INSERT.");
//			
//				sql = new StringBuffer();
//				sql.append("insert into "+tableName+" (");
//				for(int i = 0 ; i < campos.length ; i++) {
//					if (i+1 != campos.length) sql.append(campos[i]+", ");
//					else sql.append(campos[i]);
//				}
//				
//				// Recuperar o nome do campo da versão do registro.
//				String columnVersionName = null;
//				try {
//					columnVersionName = findVersionFieldAnnotationName(clazz);
//					if (columnVersionName == null) columnVersionName = findVersionFieldName(clazz);
//				} catch (UniqueAnnotationException e) {
//					throw new RuntimeException(e);
//				}
//				
//				boolean campoVersionAdicionado = false;
//				if (columnVersionName != null && !columnVersionName.isEmpty()) {
//					// Verificar se o campo de versão já está no insert caso contrário colocá-lo.
//					if (sql.indexOf(columnVersionName) == -1) {
//						sql.append(", "+columnVersionName);
//						campoVersionAdicionado = true;
//					}
//				}
//				
//				sql.append(") values (");
//				
//				for(int i = 0 ; i < values.length ; i++) {
//					if (i+1 != values.length) sql.append(values[i]+", ");
//					else sql.append(values[i]);
//				}
//				
//				if (campoVersionAdicionado) {
//					sql.append(", 0");
//				}
//				
//				sql.append(")");
//				
//				break;
//			}
//			
//			case EXECUTE_UPDATE: {
//				
//				if (campos == null || values == null) throw new RuntimeException("Campos ou valores nulos para a operação de UPDATE.");
//				
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
//				
//				sql = new StringBuffer();
//				sql.append("update "+tableName+" set ");
//				for(int i = 0 ; i < campos.length ; i++) {
//					if (i+1 != campos.length) sql.append(campos[i]+"="+values[i]+", ");
//					else sql.append(campos[i]+"="+values[i]);
//				}
//				
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
//				
//				break;
//			}
//			
//			case EXECUTE_DELETE: {
//				
//				sql = new StringBuffer();
//				sql.append("delete from "+tableName+" ");
//				if (campos != null && values != null) {
//					sql.append(" where ");
//					for(int i = 0 ; i < campos.length ; i++) {
//						if (i+1 != campos.length) sql.append(campos[i]+"="+values[i]+" and ");
//						else sql.append(campos[i]+"="+values[i]);
//					}
//				}
//				
//				break;
//				
//			}
//
//			default: {
//				sql = new StringBuffer("Opção inválida de SQL, os valores aceitos são: 'INSERT, UPDATE E DELETE'");
//			}
//			
//		}
//				
//		return sql != null ? sql.toString() : null;
//		
//	}
//	
//	/**
//	 * 
//	 * SELECT
//	 * 
//	 * Garante o controle de concorrência através do optmistic locking, fazendo o select
//	 * do campo cuja a annotação "@javax.persistence.Version" esteja presente. Dessa forma
//	 * sempre será selecionado a versão do registro em questão.
//	 * 
//	 */
//	public <T> String verifySelectOptimisticLock(RowMapper<T> rowMapper, String sql) {
//		
//		String newSQL = sql;
//		String newSQLTemp = newSQL.toLowerCase();
//		
//		Class<T> clazz = RowMapperUtils.findEntityClass(rowMapper);
//		boolean verified = false;
//		try {
//			verified = verifyVersionAnnotation(clazz);
//		} catch (UniqueAnnotationException e) {
//			throw new RuntimeException(e);
//		}
//		
//		// A anotação @version está presente em algum campo da classe.
//		// Então temos que garantir que este seja selecionado.
//		if (verified) {
//			
//			String schemaTableName = ReflectionUtils.findSchemaTableName(clazz).toLowerCase();
//			String columnVersionName = null;
//			try {
//				columnVersionName = findVersionFieldAnnotationName(clazz);
//				if (columnVersionName == null) columnVersionName = findVersionFieldName(clazz);
//			} catch (UniqueAnnotationException e) {
//				throw new RuntimeException(e);
//			}
//			
//			int indexTable = newSQLTemp.indexOf(schemaTableName);
//			int indexFrom = newSQLTemp.indexOf("from");
//			int indexWhere = newSQLTemp.lastIndexOf("where");
//			if (indexWhere < 0) indexWhere = Integer.MAX_VALUE;
//			
//			// Se a tabela está no select
//			if (indexTable > indexFrom && indexTable < indexWhere) {
//				// Se a coluna versão NÃO está no select
//				if (newSQLTemp.indexOf(columnVersionName) == -1 && newSQLTemp.indexOf("*") == -1 && newSQLTemp.indexOf("*") < indexFrom) {
//					
//					String[] parts;
//					if (newSQL.indexOf("from") != -1) {
//						parts = newSQL.split("from "+schemaTableName);
//					} else {
//						parts = newSQL.split("FROM "+schemaTableName);
//					}
//					
//					if (parts != null) {
//						if (parts.length > 1) {
//							newSQL = parts[0] + ", " + columnVersionName + " from " + schemaTableName + parts[1];
//						} else {
//							newSQL = parts[0] + ", " + columnVersionName + " from " + schemaTableName;
//						}
//					}
//					
//				}
//			}
//			
//		}
//		
//		return newSQL;
//		
//	}
//	
//	 public <T> String findVersionFieldName(Class<T> clazz) throws UniqueAnnotationException {
//		Field field = findVersionField(clazz);
//		if (field != null) return field.getName();
//		else return null;
//	}
//    
//    public <T> Field findVersionField(Class<T> clazz) throws UniqueAnnotationException {
//		List<Field> fields = ReflectionUtils.findFieldsByAnnotation(clazz, Version.class);
//		if (fields != null && !fields.isEmpty()) return fields.get(0); // Só pode ter um campo com esta anotação
//		if (fields != null && fields.size() > 1) throw new UniqueAnnotationException("Mais de uma anotação '@Version' para a entidade: "+clazz.getName());
//		else return null;
//	}
//	
//	public Object findVersionValue(Object entity) throws UniqueAnnotationException {
//		List<Object> values = ReflectionUtils.findFieldsValueByAnnotation(entity, Version.class);
//		if (values == null || values.isEmpty()) return null;
//		if (values != null && values.size() > 1) throw new UniqueAnnotationException("Mais de uma anotação '@Version' para a entidade: "+entity.getClass().getName());
//		return values.get(0); // Só pode ter um campo com esta anotação
//	}
//	
//	public Object findIdValue(Object entity) throws UniqueAnnotationException {
//		List<Object> values = ReflectionUtils.findFieldsValueByAnnotation(entity, Id.class);
//		if (values == null || values.isEmpty()) return null;
//		if (values != null && values.size() > 1) throw new UniqueAnnotationException("Mais de uma anotação '@Id' para a entidade: "+entity.getClass().getName());
//		return values.get(0); // Só pode ter um campo com esta anotação
//	}
//	
//	public <T> Field findIdField(Class<T> clazz) throws UniqueAnnotationException {
//		List<Field> fields =  ReflectionUtils.findFieldsByAnnotation(clazz, Id.class);
//		if (fields != null && !fields.isEmpty()) return fields.get(0); // Só pode ter um campo com esta anotação
//		if (fields != null && fields.size() > 1) throw new UniqueAnnotationException("Mais de uma anotação '@Id' para a entidade: "+clazz.getName());
//		else return null;
//	}
//	
//	public <T> boolean verifyVersionAnnotation(Class<T> clazz) throws UniqueAnnotationException {
//		String versionField = findVersionFieldName(clazz);
//		if (versionField == null || versionField.trim().isEmpty()) return false;
//		return true;
//	}
//	
//	public <T> String findVersionFieldAnnotationName(Class<T> clazz) throws UniqueAnnotationException {
//		Object value = ReflectionUtils.findAnnotationAttributeValue(clazz, Version.class, findVersionField(clazz), "name");
//		return value == null ? null : value.toString();
//	}
//	
//	public <T> String findIdFieldAnnotationName(Class<T> clazz) throws UniqueAnnotationException {
//		Object value = ReflectionUtils.findAnnotationAttributeValue(clazz, Id.class, findIdField(clazz), "name");
//		return value == null ? null : value.toString();
//	}
//	
//	public JdbcTemplate getJdbc() {
//		return jdbc;
//	}
//	
//	public void setDataSource(DataSource dataSource) {
//		this.jdbc = new JdbcTemplate(dataSource);
//	}
//
//	/**
//	 * Método para realização dos testes
//	 * 
//	 */
//	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
// 		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//		Date data = new Date();
//		LOGGER.info(sdf.format(data));
//		
////		ApplicationContext app = new FileSystemXmlApplicationContext("config/applicationContext.xml");
////		JdbcManager bean = (JdbcManager) app.getBean("jdbcManager");
////		
////		LOGGER.info(bean);
//		
//	}
//
//}

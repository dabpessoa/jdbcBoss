package temp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

public class GenericRowMapperFactory {

	/**
	 * 
	 * Cria um RowMapper genérico
	 * 
	 * @param fieldMap mapa com o nome do field conforme se encontra no banco de dados e o próprio field do tipo "java.lang.reflect.Field"
	 * @return entidade do banco de dados preenchida devidamente
	 * 
	 * OBS: Só funciona se no select estiver selecionando todos os campos passados no fieldMap.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static<T> RowMapper<T> createRowMapper(final Map<String, Field> fieldMap) {

		if (CollectionUtils.isEmpty(fieldMap)) {
			throw new RuntimeException("Mapa de campos vazios."); 
		} Class<T> clazz = (Class<T>) fieldMap.values().iterator().next().getDeclaringClass();

		return new RowMapperImpl<T>(clazz) {

			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {

				T entity = null;
				try {
					for (Entry<String, Field> entry : fieldMap.entrySet()) {

						String fieldName = entry.getKey();
						Field field = entry.getValue();

						if (entity == null) entity = (T) field.getDeclaringClass().newInstance();
						
						Object fieldValue;
						try {
							fieldValue = rs.getObject(fieldName);
						} catch (SQLException e) {
							throw new SQLException("Erro ao setar valor no RowMapper. ("+GenericRowMapperFactory.class.getSimpleName()+"). Field Name = "+fieldName+", Field Type = "+field.getType(), e);
						}

						Object newValue = TransformerUtils.selectTransformField(fieldValue, field);
						
						if (newValue instanceof Character) {
							if ( ! ((fieldValue instanceof Character) && newValue.equals(fieldValue)) ) {
								fieldValue = newValue;
							}
						} else if (!(newValue != null && fieldValue != null && !newValue.toString().equalsIgnoreCase(fieldValue.toString()))) { // Se não houve nenhuma transformação ainda
							fieldValue = basicFieldTypeTransform(fieldValue, field); // Aplica Transformações básicas.
						} else fieldValue = newValue;
						
						ReflectionUtils.setFieldValue(entity, field, fieldValue);

					}
				} catch (InstantiationException e) {
					throw new RuntimeException("Erro na criação do rowMapper [InstantiationException]", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Erro na criação do rowMapper [IllegalAccessException]", e);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}

				return entity;

			}

		};

	}

	public static abstract class RowMapperImpl<T> implements RowMapper<T> {

		private Class<T> clazz;

		public RowMapperImpl(Class<T> clazz) {
			this.clazz = clazz;
		}

		public Class<T> getClazz() {
			return clazz;
		}

	}

	@SuppressWarnings("unchecked")
	public static final <T> Class<T> findEntityClass(RowMapper<T> rowMapper) {
		Method method = ReflectionUtils.findMethod(rowMapper.getClass(), "getClazz");
		return (Class<T>) ReflectionUtils.invokeMethod(method, rowMapper);
	}

	public static Object basicFieldTypeTransform(Object value, Field field) throws Exception {

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

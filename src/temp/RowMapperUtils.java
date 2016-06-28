package temp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RowMapperUtils {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> findEntityClass(RowMapper<T> rowMapper) {
		if (rowMapper.getClass().getName().contains(GenericRowMapperFactory.class.getSimpleName())) {
			return GenericRowMapperFactory.findEntityClass(rowMapper);
		} else if (rowMapper.getClass().getName().contains("RowMapper") || rowMapper.getClass().getName().contains("$")) {
			return (Class<T>) ((ParameterizedType)((Type[])rowMapper.getClass().getGenericInterfaces())[0]).getActualTypeArguments()[0];
		} else {
			return (Class<T>) ((ParameterizedType)((Type[])rowMapper.getClass().getSuperclass().getGenericInterfaces())[0]).getActualTypeArguments()[0];
		}
	}
	
}

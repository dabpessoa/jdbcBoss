package me.dabpessoa.jdbcBoss.temp;

import java.lang.reflect.Field;

public class TransformerUtils {

	@SafeVarargs
	public static Object transformField(Object value, Field field, Class<? extends Transformavel>... transformavelClasses) throws Exception {
		
		// Verificar se há algum tipo de transformação.
		Transformer transformerAnnotation = field.getAnnotation(Transformer.class);
		if (transformerAnnotation != null) {

			Class<? extends Transformavel>[] transformaveis = transformerAnnotation.value();

			if (transformaveis != null) {

				for (Class<? extends Transformavel> transformavel : transformaveis) {
					Transformavel transformer = null;
					try {
						transformer = transformavel.newInstance();
					} catch (InstantiationException e) {} 
					catch (IllegalAccessException e) {}

					if (transformavelClasses != null && transformavelClasses.length != 0) {
						for (Class<? extends Transformavel> transformavelClass : transformavelClasses) {
							if (transformer != null && transformavelClass.isInstance(transformer)) {
								value =  transformer.transform(value);
							}
						}
					}
				}

			} 
		} return value;
		
	}
	
	public static Object parserTransformField(Object value, Field field) throws Exception {
		return TransformerUtils.transformField(value, field, ParserTransformer.class);
	}
	
	public static Object selectTransformField(Object value, Field field) throws Exception {
		return TransformerUtils.transformField(value, field, SelectTransformer.class);
	}
	
	public static Object insertTransformField(Object value, Field field) throws Exception {
		return TransformerUtils.transformField(value, field, InsertTransformer.class);
	}

	public static boolean hasTransformerAnnotation(Field field) {
		return field.getAnnotation(Transformer.class) != null;
	}

}

package me.dabpessoa.jdbcBoss.util;

import java.lang.reflect.Field;

public class SQLFieldWrapper {
	
	public static <T> String wrap(Object value) {
		
		if (value == null) {
			return nullWrap();
		} else if (value instanceof String) {
			return stringWrap((String) value);
		} else if (value instanceof Number) {
			return numberWrap((Number) value);
		} else if (value instanceof Character) {
			return characterWrap((Character) value);
		} else {
			return value.toString();
		}
		
	}
	
	public static <T> String wrap(final T entity, final Field field) {
		final Object value = ReflectionUtils.findFieldValue(entity, field);
		return wrap(value);
	}
	
	public static String numberWrap(Number number) {
		return number+"";
	}
	
	public static String stringWrap(String value) {
		if (value.indexOf("'") != -1) {
			value = value.replace("'", "''");
		}
		return "'"+value+"'";
	}
	
	public static String characterWrap(Character value) {
		return "'"+value+"'";
	}
	
	public static String nullWrap() {
		return null+"";
	}
}
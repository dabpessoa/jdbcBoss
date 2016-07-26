//package br.gov.ce.seduc.transformer;
//
//import java.lang.reflect.Field;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
//import br.gov.ce.seduc.webService.bean.PersistenceType;
//import br.gov.ce.seduc.webService.dao.ReflectionUtils;
//
//public class SQLFieldWrapper {
//	
//	public static <T> String wrap(Object value, PersistenceType persistenceType) {
//		
//		if (value == null) {
//			return nullWrap();
//		} else if (value instanceof String) {
//			return stringWrap((String) value);
//		} else if (value instanceof Number) {
//			return numberWrap((Number) value);
//		} else if (value instanceof Date) {
//			return oracleTimestampWrap((Date) value);
//		} else if (value instanceof Character) {
//			return characterWrap((Character) value);
//		}
//		
//		return null;
//		
//	}
//	
//	public static <T>  String wrap(final T entity, final Field field) {
//		
//		final Object value = ReflectionUtils.findFieldValue(entity, field);
//		
//		if (value == null) {
//			return nullWrap();
//		} else if (value instanceof String) {
//			return stringWrap((String) value);
//		} else if (value instanceof Number) {
//			return numberWrap((Number) value);
//		} else if (value instanceof Date) {
//			
//			TemporalType temporalType = (TemporalType) ReflectionUtils.findAnnotationAttributeValue(entity.getClass(), Temporal.class, field, "value");
//			
//			if (!TemporalType.DATE.equals(temporalType)) {
//				return oracleTimestampWrap((Date) value);
//			} else {
//				return oracleDateWrap((Date) value);
//			}
//			
//		} else if (value instanceof Character) {
//			return characterWrap((Character) value);
//		}
//		
//		return null;
//	}
//	
//	public static String oracleDateWrap(Date date) {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		return String.format("to_date('%s', '%s')", sdf.format(date), "DD/MM/YYYY HH24:MI:SS");
//	}
//	
//	public static String oracleTimestampWrap(Date date) {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		return String.format("to_timestamp('%s', '%s')", sdf.format(date), "DD/MM/YYYY HH24:MI:SS");
//	}
//	
//	public static String numberWrap(Number number) {
//		return number+"";
//	}
//	
//	public static String stringWrap(String value) {
//		if (value.indexOf("'") != -1) {
//			value = value.replace("'", "''");
//		}
//		return "'"+value+"'";
//	}
//	
//	public static String characterWrap(Character value) {
//		return "'"+value+"'";
//	}
//	
//	public static String nullWrap() {
//		return null+"";
//	}
//}

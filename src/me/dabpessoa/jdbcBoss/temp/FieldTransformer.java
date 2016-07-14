package me.dabpessoa.jdbcBoss.temp;

public interface FieldTransformer extends InsertTransformer {
	
	@Override public String transform(Object value);
	
}

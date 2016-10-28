package me.dabpessoa.jdbcBoss.temp;

import java.math.BigDecimal;

public class BigToIntegerTransformer implements ParserTransformer {

	@Override
	public Object transform(Object value) {

		if (value != null) {
			
			return BigDecimal.valueOf(Double.parseDouble(value.toString())).intValue();
		} 
		
		return null;
	}

}

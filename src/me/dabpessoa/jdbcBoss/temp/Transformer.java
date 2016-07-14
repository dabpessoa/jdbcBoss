package me.dabpessoa.jdbcBoss.temp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Podem ser atribuidos uma lista de transformadores capazes de tratar
 * campos de uma entidade de tal forma que altere seus valores, seus nomes ou seus operadores utilizados
 * em um sql.
 * 
 * @author diego.pessoa
 *
 */

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transformer {

	Class<? extends Transformavel>[] value();
	
}

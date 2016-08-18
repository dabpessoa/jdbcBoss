package me.dabpessoa.jdbcBoss.run;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import me.dabpessoa.jdbcBoss.bean.Domain;
import me.dabpessoa.jdbcBoss.jdbc.ConnectionProperties;
import me.dabpessoa.jdbcBoss.jdbc.JDBCBoss;
import me.dabpessoa.jdbcBoss.jdbc.ResultSetObjectMapper;


public class Run {

	public static void main(String[] args) {
		
		ConnectionProperties connectionProperties = new ConnectionProperties();
		connectionProperties.setJdbcDriverClassFullName("org.postgresql.Driver");
		connectionProperties.setUrl("jdbc:postgresql://localhost:5432/atma");
		connectionProperties.setUser("postgres");
		connectionProperties.setPassword("postgres");
		
		JDBCBoss jdbcBoss = new JDBCBoss(connectionProperties);
		
//		int result = jdbcBoss.execute("insert into vaicomecar.domain values (default, 'description teste', 'label teste')");
//		System.out.println(result);
		
		List<Domain> domains = jdbcBoss.queryList("select * from vaicomecar.domain", new ResultSetObjectMapper<Domain>() {
			public Domain map(ResultSet rs, int rowNum) throws SQLException {
				Domain domain = new Domain();
				domain.setId(rs.getLong("id"));
				domain.setLabel(rs.getString("label"));
				domain.setDescription(rs.getString("description"));
				return domain;
			};
		});
		
		Domain domain = jdbcBoss.querySingleResult("select * from vaicomecar.domain m where m.id = 2", new ResultSetObjectMapper<Domain>() {
			@Override
			public Domain map(ResultSet rs, int rowNum) throws SQLException {
				Domain domain = new Domain();
				domain.setId(rs.getLong("id"));
				domain.setLabel(rs.getString("label"));
				domain.setDescription(rs.getString("description"));
				return domain;
			}
		});
		
		System.out.println("DOMAIN unique result: "+domain.getLabel());
		
		for (Domain d : domains) {
			System.out.println("DOMAIN: "+d.getLabel());
		}
		
	}
	
}

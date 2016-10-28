package me.dabpessoa.jdbcBoss.run;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import me.dabpessoa.jdbcBoss.bean.Domain;
import me.dabpessoa.jdbcBoss.jdbc.ConnectionProperties;
import me.dabpessoa.jdbcBoss.jdbc.JDBCBoss;
import me.dabpessoa.jdbcBoss.jdbc.ResultSetObjectMapper;
import me.dabpessoa.jdbcBoss.util.MapFactory;


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
		
		List<Domain> domains = jdbcBoss.queryList("select * from vaicomecar.domain", Domain.class, MapFactory.create("id", "id", "label", "label", "description", "description"));
		
		long count = jdbcBoss.count("vaicomecar.domain");
		System.out.println(count);
		
		long countWhere = jdbcBoss.count("vaicomecar.domain", MapFactory.create("id", 1));
		System.out.println(countWhere);
		
//		List<Domain> domains = jdbcBoss.queryList("select * from vaicomecar.domain", new ResultSetObjectMapper<Domain>() {
//			public Domain map(ResultSet rs, int rowNum) throws SQLException {
//				Domain domain = new Domain();
//				domain.setId(rs.getLong("id"));
//				domain.setLabel(rs.getString("label"));
//				domain.setDescription(rs.getString("description"));
//				return domain;
//			};
//		});
		
		System.out.println("(*) TESTE DOMAIN LIST");
		for (Domain d : domains) {
			System.out.println("Domain: "+d.getLabel());
		}
		
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
		
		System.out.println("(*) TESTE DOMAIN UNIQUE RESULT");
		System.out.println("Domain: "+domain.getLabel());
		
		String domainDescription = jdbcBoss.querySingleObject("select d.description from vaicomecar.domain d where d.id = 1");
		System.out.println("(*) TESTE SINGLE OBJECT");
		System.out.println("Description: "+domainDescription);
		
		List<String> domainDescriptions = jdbcBoss.queryObjectList("select d.description from vaicomecar.domain d");
		System.out.println("(*) TESTE MULTIPLE OBJECTS");
		for (String description : domainDescriptions) {
			System.out.println("Description: "+description);
		}
		
	}
	
}

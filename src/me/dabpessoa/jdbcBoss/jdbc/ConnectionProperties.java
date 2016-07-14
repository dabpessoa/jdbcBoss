package me.dabpessoa.jdbc;

public class ConnectionProperties {

	private String user;
	private String password;
	private String url;
	private String jdbcDriverClassFullName;
	
	public ConnectionProperties() {}

	public ConnectionProperties(String user, String password, String url, String jdbcDriverClassFullName) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.jdbcDriverClassFullName = jdbcDriverClassFullName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJdbcDriverClassFullName() {
		return jdbcDriverClassFullName;
	}

	public void setJdbcDriverClassFullName(String jdbcDriverClassFullName) {
		this.jdbcDriverClassFullName = jdbcDriverClassFullName;
	}
	
}

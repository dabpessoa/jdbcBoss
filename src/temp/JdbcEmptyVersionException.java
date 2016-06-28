package temp;

public class JdbcEmptyVersionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JdbcEmptyVersionException(String message) {
		super(message);
	}
	
}

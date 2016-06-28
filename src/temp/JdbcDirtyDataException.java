package temp;

public class JdbcDirtyDataException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JdbcDirtyDataException(String message) {
		super(message);
	}

}

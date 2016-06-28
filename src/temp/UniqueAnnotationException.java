package temp;

public class UniqueAnnotationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UniqueAnnotationException(Throwable throwable) {
		super(throwable);
	}
	
	public UniqueAnnotationException(String message) {
		super(message);
	}
	
	public UniqueAnnotationException(String message, Throwable throwable) {
		super(message, throwable);
	}

}

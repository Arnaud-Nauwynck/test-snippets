package fr.an.tests.reverseyarn;

public class YarnException extends Exception {

	private static final long serialVersionUID = 1L;

	public YarnException() {
		super();
	}

	public YarnException(String message) {
		super(message);
	}

	public YarnException(Throwable cause) {
		super(cause);
	}

	public YarnException(String message, Throwable cause) {
		super(message, cause);
	}

}

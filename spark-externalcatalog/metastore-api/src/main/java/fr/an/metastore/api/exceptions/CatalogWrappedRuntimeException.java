package fr.an.metastore.api.exceptions;

public class CatalogWrappedRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CatalogWrappedRuntimeException(String message, Exception ex) {
		super(message, ex);
	}
	
}
package fr.an.metastore.api.exceptions;

public class CatalogRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CatalogRuntimeException(String message) {
		super(message);
	}
	
}
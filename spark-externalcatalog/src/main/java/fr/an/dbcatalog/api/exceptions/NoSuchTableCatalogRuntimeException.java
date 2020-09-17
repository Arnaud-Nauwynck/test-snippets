package fr.an.dbcatalog.api.exceptions;

import lombok.Getter;

@Getter
public class NoSuchTableCatalogRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;

	public NoSuchTableCatalogRuntimeException(String dbName, String tableName) {
		super("Table '" + dbName + "." + tableName + "' not found");
		this.dbName = dbName;
		this.tableName = tableName;
	}
	
}

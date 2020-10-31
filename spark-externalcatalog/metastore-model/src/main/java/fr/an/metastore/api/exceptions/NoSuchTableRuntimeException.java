package fr.an.metastore.api.exceptions;

import lombok.Getter;

@Getter
public class NoSuchTableRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;

	public NoSuchTableRuntimeException(String dbName, String tableName) {
		super("Table '" + dbName + "." + tableName + "' not found");
		this.dbName = dbName;
		this.tableName = tableName;
	}
	
}

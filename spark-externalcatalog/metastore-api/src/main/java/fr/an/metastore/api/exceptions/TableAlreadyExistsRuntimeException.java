package fr.an.metastore.api.exceptions;

public class TableAlreadyExistsRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;
	
	public TableAlreadyExistsRuntimeException(String dbName, String tableName) {
		super("Table '" + dbName + "." + tableName + "' already exists");
		this.dbName = dbName;
		this.tableName = tableName;
	}
	
}

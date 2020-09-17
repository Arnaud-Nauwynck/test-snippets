package fr.an.dbcatalog.api.exceptions;

import fr.an.dbcatalog.api.PartitionSpec;

public class PartitionAlreadyExistsRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;
	public final PartitionSpec spec;
	
	public PartitionAlreadyExistsRuntimeException(String dbName, String tableName, PartitionSpec spec) {
		super("Partition already exists in table '" + dbName + "." + tableName + "' : '" + spec.mkString(",") + "'");
		this.dbName = dbName;
		this.tableName = tableName;
		this.spec = spec;
	}
}

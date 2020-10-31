package fr.an.metastore.api.exceptions;

import fr.an.metastore.api.immutable.ImmutablePartitionSpec;

public class PartitionAlreadyExistsRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;
	public final ImmutablePartitionSpec spec;
	
	public PartitionAlreadyExistsRuntimeException(String dbName, String tableName, ImmutablePartitionSpec spec) {
		super("Partition already exists in table '" + dbName + "." + tableName + "' : '" + spec + "'");
		this.dbName = dbName;
		this.tableName = tableName;
		this.spec = spec;
	}
}

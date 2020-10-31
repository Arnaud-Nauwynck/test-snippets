package fr.an.metastore.api.exceptions;

import fr.an.metastore.api.immutable.ImmutablePartitionSpec;

public class NoSuchPartitionRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;
	public final ImmutablePartitionSpec spec;
	
	public NoSuchPartitionRuntimeException(String dbName, String tableName, ImmutablePartitionSpec spec) {
		super("Partition not found in table '" + dbName + "." + tableName + "' : '" + spec + "'");
		this.dbName = dbName;
		this.tableName = tableName;
		this.spec = spec;
	}

}

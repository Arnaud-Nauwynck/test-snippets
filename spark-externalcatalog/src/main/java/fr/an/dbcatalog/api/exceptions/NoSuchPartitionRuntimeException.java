package fr.an.dbcatalog.api.exceptions;

import fr.an.dbcatalog.api.PartitionSpec;

public class NoSuchPartitionRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final String dbName;
	public final String tableName;
	public final PartitionSpec spec;
	
	public NoSuchPartitionRuntimeException(String dbName, String tableName, PartitionSpec spec) {
		super("Partition not found in table '" + dbName + "." + tableName + "' : '" + spec.mkString(",") + "'");
		this.dbName = dbName;
		this.tableName = tableName;
		this.spec = spec;
	}

}

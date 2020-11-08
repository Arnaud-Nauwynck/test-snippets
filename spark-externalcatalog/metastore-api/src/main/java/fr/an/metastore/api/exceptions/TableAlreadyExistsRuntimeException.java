package fr.an.metastore.api.exceptions;

import fr.an.metastore.api.immutable.CatalogTableId;

public class TableAlreadyExistsRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final CatalogTableId tableId;

	public TableAlreadyExistsRuntimeException(CatalogTableId tableId) {
		super("Table '" + tableId + "' already exists");
		this.tableId = tableId;
	}
	
}

package fr.an.metastore.api.exceptions;

import fr.an.metastore.api.immutable.CatalogTableId;
import lombok.Getter;

@Getter
public class NoSuchTableRuntimeException extends CatalogRuntimeException {

	private static final long serialVersionUID = 1L;

	public final CatalogTableId tableId;

	public NoSuchTableRuntimeException(CatalogTableId tableId) {
		super("Table '" + tableId + "' not found");
		this.tableId = tableId;
	}
	
}

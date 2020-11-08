package fr.an.metastore.api.immutable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CatalogTableId {
	public final String database;
	public final String table;
	
	@Override
	public String toString() {
		return ((database != null)? database + "." : "") + table;
	}
	
}
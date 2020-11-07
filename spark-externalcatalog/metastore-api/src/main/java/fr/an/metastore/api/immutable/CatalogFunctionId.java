package fr.an.metastore.api.immutable;

import lombok.Value;

@Value
public class CatalogFunctionId {
	public final String database;
	public final String funcName;
}
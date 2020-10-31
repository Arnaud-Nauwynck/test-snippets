package fr.an.metastore.api.immutable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ImmutableTableId {
	public final String database;
	public final String table;
}
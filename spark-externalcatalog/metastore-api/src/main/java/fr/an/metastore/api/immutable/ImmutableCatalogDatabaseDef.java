package fr.an.metastore.api.immutable;

import java.net.URI;

import com.google.common.collect.ImmutableMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor 
@Builder(toBuilder = true)
public class ImmutableCatalogDatabaseDef {

	public final String name;

	public final String description;

	public final URI locationUri;

	public final ImmutableMap<String, String> properties;

}

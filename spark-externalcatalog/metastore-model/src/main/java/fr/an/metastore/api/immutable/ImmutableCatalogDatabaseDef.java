package fr.an.metastore.api.immutable;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor @Builder
public class ImmutableCatalogDatabaseDef {

	public final String name;

	public final String description;

	public final URI locationUri;

	public final ImmutableMap<String, String> properties;

	public ImmutableCatalogDatabaseDefBuilder builderCopy() {
		return builder() //
				.name(name) //
				.description(description) //
				.locationUri(locationUri) //
				.properties(properties);
	}

	public ImmutableCatalogDatabaseDef copyWithDescription(String p) {
		return builderCopy().description(p).build();
	}

	public ImmutableCatalogDatabaseDef copyWithLocationUri(URI p) {
		return builderCopy().locationUri(p).build();
	}

	public ImmutableCatalogDatabaseDef copyWithProperties(Map<String, String> p) {
		return builderCopy().properties(ImmutableMap.copyOf(p)).build();
	}

}

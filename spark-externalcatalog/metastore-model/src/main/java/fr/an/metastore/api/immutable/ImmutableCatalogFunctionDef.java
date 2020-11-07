package fr.an.metastore.api.immutable;

import java.net.URI;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 *
 */
@Value
@Builder(toBuilder = true)
public class ImmutableCatalogFunctionDef {

	public final CatalogFunctionId identifier;
    
	public final String className;
    
	public final List<ImmutableCatalogFunctionResource> resources;

	@Value
	@AllArgsConstructor 
	// @Builder(toBuilder = true)
	public static class ImmutableCatalogFunctionResource {
		public final FunctionResourceTypeEnum resourceType;
		public final URI uri;
	}

}
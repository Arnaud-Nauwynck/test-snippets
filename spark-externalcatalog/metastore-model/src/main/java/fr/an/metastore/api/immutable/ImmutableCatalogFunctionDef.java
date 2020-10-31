package fr.an.metastore.api.immutable;

import java.net.URI;
import java.util.List;

import lombok.Value;

/**
 *
 */
@Value
public class ImmutableCatalogFunctionDef {

	// redundant .. public final CatalogFunctionId identifier;
    
	public final String className;
    
	public final List<ImmutableCatalogFunctionResource> resources;

	@Value
	public static class ImmutableCatalogFunctionResource {
		public final FunctionResourceTypeEnum resourceType;
		public final URI uri;
	}

}
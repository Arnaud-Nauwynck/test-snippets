package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;

public interface IFunctionModel {

	ImmutableCatalogFunctionDef getFuncDef();

}

package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;

/**
 * part of AbstractJavaDbCatalog, for functions DDL
 */
public abstract class FunctionsDDL<TDb, TFunc> {

	public abstract TFunc createFunction(TDb db, ImmutableCatalogFunctionDef funcDef);

	public abstract void dropFunction(TDb db, TFunc func);

	public abstract void alterFunction(TDb db, TFunc func, ImmutableCatalogFunctionDef funcDef);

	public abstract TFunc renameFunction(TDb db, TFunc oldFunc, String newName);

}

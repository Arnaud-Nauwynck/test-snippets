package fr.an.metastore.api.manager;

import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.impl.model.DatabaseModel;

/**
 * part of AbstractJavaDbCatalog, for functions DDL
 */
public abstract class FunctionsDDLManager<TDb extends DatabaseModel, TFunc> {

	public abstract TFunc createFunction(TDb db, String funcName, ImmutableCatalogFunctionDef funcDef);

	public abstract void dropFunction(TDb db, TFunc func);

	public abstract void alterFunction(TDb db, TFunc func, ImmutableCatalogFunctionDef funcDef);

	public abstract TFunc renameFunction(TDb db, TFunc func, String newName);

}

package fr.an.dbcatalog.api.manager;

import org.apache.spark.sql.catalyst.catalog.CatalogFunction;

import fr.an.dbcatalog.impl.model.DatabaseModel;

/**
 * part of AbstractJavaDbCatalog, for functions DDL
 */
public abstract class FunctionsDDLManager<TDb extends DatabaseModel, TFunc> {

	public abstract TFunc createFunction(TDb db, CatalogFunction funcDef);

	public abstract void dropFunction(TDb db, TFunc func);

	public abstract void alterFunction(TDb db, TFunc func, CatalogFunction funcDef);

	public abstract TFunc renameFunction(TDb db, TFunc func, String newName);

}

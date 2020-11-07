package fr.an.metastore.api.spi;


import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;

/**
 * part of AbstractJavaDbCatalog, for table DDL
 *
 */
public abstract class TablesDDL<TDb,TTable> {
	
	public abstract TTable createTable(TDb db, ImmutableCatalogTableDef tableDef, boolean ignoreIfExists);

	public abstract void dropTable(TDb db, TTable table, boolean ignoreIfNotExists, boolean purge);

	public abstract TTable renameTable(TDb db, TTable table, //
			String newName);

	public abstract void alterTable(TDb db, TTable table, //
			ImmutableCatalogTableDef tableDef);

	public abstract void alterTableDataSchema(TDb db, TTable table, //
			StructTypeDTO newDataSchema);

	public abstract void alterTableStats(TDb db, TTable table, //
			ImmutableCatalogTableStatistics stats);

}

package fr.an.metastore.api.manager;


import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.StructTypeDTO;

/**
 * part of AbstractJavaDbCatalog, for table DDL
 *
 */
public abstract class TablesDDLManager<TDb,TTable> {
	
	public abstract TTable createTable(TDb db, CatalogTableDTO tableDefinition, boolean ignoreIfExists);

	public abstract void dropTable(TDb db, TTable table, boolean ignoreIfNotExists, boolean purge);

	public abstract TTable renameTable(TDb db, TTable table, //
			String newName);

	public abstract void alterTable(TDb db, TTable table, //
			CatalogTableDTO tableDefinition);

	public abstract void alterTableDataSchema(TDb db, TTable table, //
			StructTypeDTO newDataSchema);

	public abstract void alterTableStats(TDb db, TTable table, //
			CatalogStatisticsDTO stats);

}

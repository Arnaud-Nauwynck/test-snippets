package fr.an.metastore.impl.manager;

import org.apache.hadoop.conf.Configuration;

import fr.an.metastore.api.manager.DataLoaderManager.DefaultDataLoaderManager;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.CatalogModel2DtoConverter;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.val;

public class DefaultCatalogFactory {

	public static LookupsAndDdlsAdapterCatalogFacade createDefaultJavaDbCatalog(Configuration hadoopConfig, CatalogModel dbCatalogModel) {
		val lookups = new ModelDelegateCatalogLookup(dbCatalogModel);
		val ddls = new InMemoryCatalogDDLManager(dbCatalogModel, hadoopConfig);
		
		CatalogModel2DtoConverter dtoConverter = new CatalogModel2DtoConverter();
		val dataLoaderManager = new DefaultDataLoaderManager<DatabaseModel,TableModel,TablePartitionModel>();
	
		val javaDbCatalog = new LookupsAndDdlsAdapterCatalogFacade(
				dtoConverter, //
				lookups.dbsLookup, ddls.dbsDdl, //
				lookups.dbTablesLookup, ddls.dbTablesDdl, //
				lookups.dbTablePartitionsLookup, ddls.dbTablePartitionsDdl, //
				lookups.dbFuncsLookup, ddls.dbFuncsDdl, //
				dataLoaderManager);
		return javaDbCatalog;
	}
}

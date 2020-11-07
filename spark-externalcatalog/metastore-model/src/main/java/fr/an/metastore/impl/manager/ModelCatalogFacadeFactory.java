package fr.an.metastore.impl.manager;

import org.apache.hadoop.conf.Configuration;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.spi.DataLoader.DoNothingDataLoader;
import fr.an.metastore.api.spi.DefaultDelegateCatalogFacade;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.val;

public class ModelCatalogFacadeFactory {

	public static CatalogFacade createDefaultCatalogFacade(
			Configuration hadoopConfig, 
			CatalogModel dbCatalogModel
			) {
		val lookups = new ModelCatalogLookups(dbCatalogModel);
		val ddls = new ModelCatalogDDLs(dbCatalogModel, hadoopConfig);
		
		val dataLoaderManager = new DoNothingDataLoader<DatabaseModel,TableModel,TablePartitionModel>();
	
		val javaDbCatalog = new DefaultDelegateCatalogFacade<DatabaseModel, TableModel, TablePartitionModel, FunctionModel>(
				lookups.dbsLookup, ddls.dbsDdl, //
				lookups.dbTablesLookup, ddls.dbTablesDdl, //
				lookups.dbTablePartitionsLookup, ddls.dbTablePartitionsDdl, //
				lookups.dbFuncsLookup, ddls.dbFuncsDdl, //
				dataLoaderManager);
		return javaDbCatalog;
	}
}

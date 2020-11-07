package fr.an.metastore.impl.manager;

import org.apache.hadoop.conf.Configuration;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.spi.DataLoader;
import fr.an.metastore.api.spi.DataLoader.DoNothingDataLoader;
import fr.an.metastore.api.spi.DefaultDelegateCatalogFacade;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.Data;
import lombok.val;

@Data
public class ModelCatalogFacadeFactory {

	public final CatalogModel dbCatalogModel = new CatalogModel();

	public final Configuration hadoopConfig = new Configuration();
	public final ModelCatalogLookups lookups = new ModelCatalogLookups(dbCatalogModel);
	public final ModelCatalogDDLs ddls = new ModelCatalogDDLs(dbCatalogModel, hadoopConfig);
	public final DataLoader<DatabaseModel,TableModel,TablePartitionModel> dataLoaderManager = new DoNothingDataLoader<DatabaseModel,TableModel,TablePartitionModel>();

	/** cf also EmbeddedCatalogFacade constructor */
	public CatalogFacade createCatalogFacade() {
		val javaDbCatalog = new DefaultDelegateCatalogFacade<DatabaseModel, TableModel, TablePartitionModel, FunctionModel>(
				lookups.dbsLookup, ddls.dbsDdl, //
				lookups.dbTablesLookup, ddls.dbTablesDdl, //
				lookups.dbTablePartitionsLookup, ddls.dbTablePartitionsDdl, //
				lookups.dbFuncsLookup, ddls.dbFuncsDdl, //
				dataLoaderManager);
		return javaDbCatalog;
	}
}

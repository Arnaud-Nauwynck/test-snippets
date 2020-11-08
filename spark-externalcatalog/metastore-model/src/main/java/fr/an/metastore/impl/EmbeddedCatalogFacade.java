package fr.an.metastore.impl;

import java.io.File;

import fr.an.metastore.api.spi.DefaultDelegateCatalogFacade;
import fr.an.metastore.impl.loader.ConfigImporter;
import fr.an.metastore.impl.manager.ModelCatalogFacadeFactory;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedCatalogFacade 
	extends DefaultDelegateCatalogFacade<DatabaseModel, TableModel, TablePartitionModel, FunctionModel> {

	public final CatalogModel catalogModel;
	
	ConfigImporter configImporter = new ConfigImporter();
	
	public EmbeddedCatalogFacade() {
		this(null);
	}
	
	public EmbeddedCatalogFacade(File configFile) {
		this(new ModelCatalogFacadeFactory(), configFile);
	}
	
	private EmbeddedCatalogFacade(ModelCatalogFacadeFactory b, File configFile) {
		super(// call super with empty in-memory CatalogModel from ModelCatalogFacadeFactory
				b.lookups.getDbsLookup(), b.ddls.getDbsDdl(), //
				b.lookups.getDbTablesLookup(), b.ddls.getDbTablesDdl(), //
				b.lookups.getDbTablePartitionsLookup(), b.ddls.getDbTablePartitionsDdl(), //
				b.lookups.getDbFuncsLookup(), b.ddls.getDbFuncsDdl(), //
				b.dataLoaderManager);
		this.catalogModel = b.dbCatalogModel;
		if (configFile != null) {
			loadConfigFile(configFile);
		}
	}

	public void loadConfigFile(File configFile) {
		// Fill CatalogModel from yaml config files
		if (configFile.exists()) {
			configImporter.importConfig(configFile, catalogModel);
		} else {
			log.error("Config file '" + configFile + "' not found! using empty model");
		}				
	}

}

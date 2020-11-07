package fr.an.metastore.impl;

import java.io.File;

import fr.an.metastore.api.spi.DefaultDelegateCatalogFacade;
import fr.an.metastore.impl.loader.ConfigImporter;
import fr.an.metastore.impl.manager.ModelCatalogFacadeFactory;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedCatalogFacade 
	extends DefaultDelegateCatalogFacade<DatabaseModel, TableModel, TablePartitionModel, FunctionModel> {
	
	public static final String DEFAULT_CONFIG_FILENAME = "embeded-catalog.yml";
	
	public EmbeddedCatalogFacade() {
		this(new ModelCatalogFacadeFactory());
	}
	
	private EmbeddedCatalogFacade(ModelCatalogFacadeFactory b) {
		super(// call super with empty in-memory CatalogModel from ModelCatalogFacadeFactory
				b.lookups.getDbsLookup(), b.ddls.getDbsDdl(), //
				b.lookups.getDbTablesLookup(), b.ddls.getDbTablesDdl(), //
				b.lookups.getDbTablePartitionsLookup(), b.ddls.getDbTablePartitionsDdl(), //
				b.lookups.getDbFuncsLookup(), b.ddls.getDbFuncsDdl(), //
				b.dataLoaderManager);
		
		// Fill CatalogModel from yaml config files
		ConfigImporter configImporter = new ConfigImporter();
		String configFileName = System.getProperty("fr.an.metastore.configFileName", DEFAULT_CONFIG_FILENAME);
		File configFile = new File(configFileName);
		if (configFile.exists()) {
			configImporter.importConfig(configFile, b.dbCatalogModel);
		} else {
			log.error("Config file '" + configFile + "' not found! using empty model");
		}
	}

}

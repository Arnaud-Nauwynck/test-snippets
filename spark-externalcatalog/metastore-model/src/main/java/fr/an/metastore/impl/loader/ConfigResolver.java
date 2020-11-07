package fr.an.metastore.impl.loader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogTablePropertiesConfig;
import lombok.val;

public class ConfigResolver {

	public void resolve(EmbeddedCatalogConfig catalogConf) {
		Set<String> importedFiles = new HashSet<>();
		// TOADD recursive read files

		// resolve config, recursive import declared "importFiles", and inheritable properties
		Map<String,CatalogTablePropertiesConfig> importableTableProperties = new HashMap<>();
		collectImportableTableProperties(importableTableProperties, catalogConf.getImportableTableProperties());
		if (catalogConf.getDatabases() != null) {
			for(val dbConf : catalogConf.getDatabases()) {
				collectImportableTableProperties(importableTableProperties, 
						dbConf.getImportableTableProperties());
			}
		}

		// apply importTablePropertiesRefs
		// TODO
	}

	private void collectImportableTableProperties(
			Map<String, CatalogTablePropertiesConfig> dest,
			List<CatalogTablePropertiesConfig> confs) {
		if (confs != null && !confs.isEmpty()) {
			for(val conf : confs) {
				dest.put(conf.getName(), conf);
			}
		}
	}

}

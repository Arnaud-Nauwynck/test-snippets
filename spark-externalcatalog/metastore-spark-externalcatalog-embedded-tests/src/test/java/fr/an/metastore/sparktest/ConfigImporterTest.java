package fr.an.metastore.sparktest;

import java.io.File;

import org.junit.Test;

import fr.an.metastore.impl.loader.ConfigImporter;
import fr.an.metastore.impl.model.CatalogModel;
import lombok.val;

public class ConfigImporterTest {

	@Test
	public void testConfigFile_embeddedCatalogYml() {
		File configFile = new File("embedded-catalog.yml");
		val catalogModel = new CatalogModel();
		val configImporter = new ConfigImporter();
		configImporter.importConfig(configFile, catalogModel);
	}
}

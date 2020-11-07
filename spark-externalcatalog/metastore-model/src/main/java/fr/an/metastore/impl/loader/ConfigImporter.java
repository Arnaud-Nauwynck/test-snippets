package fr.an.metastore.impl.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import fr.an.metastore.impl.model.CatalogModel;
import lombok.Getter;
import lombok.Setter;

public class ConfigImporter {

	@Getter @Setter
	ResolvedConfigToModelBuilder resolvedConfigToModelBuilder = new ResolvedConfigToModelBuilder();
	
	@Getter @Setter
	ConfigResolver configResolver = new ConfigResolver();
	
	public void importConfig(File configFile, CatalogModel catalogModel) {
		EmbeddedCatalogConfig catalogConf = loadYaml(configFile, EmbeddedCatalogConfig.class);
		
		configResolver.resolve(catalogConf);

		resolvedConfigToModelBuilder.registerResolvedConfToModel(catalogConf, catalogModel);
	}

	// Yaml utility method
	private static <T> T loadYaml(File file, Class<T> clss) {
		Yaml yaml = new Yaml();
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
	        Reader reader = new UnicodeReader(in); // remove BOM if any "<U+FEFF>" + use UTF8 or UTF16 or , ....
	        
	        return yaml.loadAs(reader, clss);
		} catch(Exception ex) {
			throw new RuntimeException("Failed to read yaml file '" + file + "'", ex);
		}
	}


}

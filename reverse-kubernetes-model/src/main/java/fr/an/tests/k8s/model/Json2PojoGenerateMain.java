package fr.an.tests.k8s.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsonschema2pojo.Jsonschema2Pojo;
import org.jsonschema2pojo.RuleLogger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Json2PojoGenerateMain {

	private File sourceBaseDir = new File("C:/arn/downloadTools/k8s/operators/kubernetes-client/kubernetes-model-generator");
	private File targetBaseDir = new File("generated");
	
	public static void main(String[] args) {
		try {
			Json2PojoGenerateMain app = new Json2PojoGenerateMain();
			app.doMain(args);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void doMain(String[] args) {
		RuleLogger ruleLogger = new SimpleSlf4jRuleLogger(log);
		
		if (! targetBaseDir.exists()) {
			targetBaseDir.mkdirs();
		}
		
		File[] modelDirs = sourceBaseDir.listFiles();
		for(File modelDir : modelDirs) {
			String modelName = modelDir.getName();
			
			File schemaFile = new File(modelDir, "src/main/resources/schema/kube-schema.json");
			if (schemaFile.exists()) {
				if (!modelName.startsWith("kubernetes-")) {
					log.info("ignored scanned model: " + modelName);
					continue;
				}

				log.info("scanned model: " + modelName);
				SimpleGenerationConfig config = new SimpleGenerationConfig();
			
				List<URL> sources = new ArrayList<>();
				try {
					sources.add(schemaFile.toURI().toURL());
				} catch (MalformedURLException e) {
				}
				config.setSources(sources);
				
				File targetDir = new File(targetBaseDir, modelName + "/src/main/java");
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
				config.setTargetDirectory(targetDir);
				
				try {
					Jsonschema2Pojo.generate(config, ruleLogger);
				} catch (IOException ex) {
					log.error("Failed", ex);
				}
			
			}
			
		}
	}
}

package fr.an.metastore.spark;

import java.io.File;

import org.apache.spark.sql.connector.catalog.CatalogPlugin;
import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.TableCatalog;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import fr.an.metastore.impl.EmbeddedCatalogFacade;
import fr.an.metastore.spark.modeladapter.SparkModelConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom spark ExternalCatalog 
 * to use with spark parameters
 * <PRE>
 * --conf spark.sql.catalog.spark_catalog=fr.an.metastore.spark.EmbeddedExternalCatalog
 * </PRE>
 *
 * This class will be lazily loaded by spark, on the first usage of for exmaple:
 * <code>spark.sql("show tables").show()</code>
 * 
 * cf corresponding spark code:
 * 
 * CatalogManager {
 * val SESSION_CATALOG_NAME: String = "spark_catalog"
 * }
 * 
 * SQLConf
 * <code>
 *   val V2_SESSION_CATALOG_IMPLEMENTATION =
 *   buildConf(s"spark.sql.catalog.$SESSION_CATALOG_NAME")
 *     .doc("A catalog implementation that will be used as the v2 interface to Spark's built-in " +
 *       s"v1 catalog: $SESSION_CATALOG_NAME. This catalog shares its identifier namespace with " +
 *       s"the $SESSION_CATALOG_NAME and must be consistent with it; for example, if a table can " +
 *       s"be loaded by the $SESSION_CATALOG_NAME, this catalog must also return the table " +
 *       s"metadata. To delegate operations to the $SESSION_CATALOG_NAME, implementations can " +
 *       "extend 'CatalogExtension'.")
 *     .stringConf
 *     .createOptional
 * </code>


 * 
 * NOTICE:
 * 1/ class need to implement org.apache.spark.sql.connector.catalog.CatalogPlugin
 * otherwise ..
 * <PRE>
 * ERROR catalog.CatalogManager: Fail to instantiate the custom v2 session catalog: fr.an.metastore.spark.EmbeddedExternalCatalog
 * </PRE>
 * 
 * 2/ 
 * <PRE>
 * org.apache.spark.sql.AnalysisException: Cannot use catalog embedded-catalog: not a TableCatalog;
 * at org.apache.spark.sql.connector.catalog.CatalogV2Implicits$CatalogHelper.asTableCatalog(CatalogV2Implicits.scala:76)
 * </PRE>
 * 
 */
@Slf4j
public class EmbeddedExternalCatalog extends JavaAdapterExternalCatalog 
	implements CatalogPlugin, TableCatalog, SupportsNamespaces {

	public static final String DEFAULT_CONFIG_FILENAME = "embedded-catalog.yml";
	public static final String CONFIG_FILE_PROPERTY = "fr.an.metastore.configFileName";
	
	private static EmbeddedCatalogFacade catalogFacadeInstance;

	static {
		log.info("<cinit> fr.an.metastore.spark.EmbeddedExternalCatalog");
		System.out.println("(stdout) <cinit> fr.an.metastore.spark.EmbeddedExternalCatalog");
		catalogFacadeInstance = new EmbeddedCatalogFacade();
	}
	
	/**
	 * called by introspection from spark config
	 * cf org.apache.spark.sql.connector.catalog.CatalogManager.loadV2SessionCatalog()
	 */
	public EmbeddedExternalCatalog() {
		super(catalogFacadeInstance, new SparkModelConverter());
		log.info("done EmbeddedExternalCatalog.<init>()");
		System.out.println("(stdout) done EmbeddedExternalCatalog.<init>()");
	}
	
	@Override
	public String name() {
		return "embedded-catalog";
	}

	@Override
	public void initialize(String name, CaseInsensitiveStringMap options) {
		log.info("fr.an.metastore.spark.EmbeddedExternalCatalog.initialize()");

		String configFileName = options.get(CONFIG_FILE_PROPERTY);
		if (configFileName == null) {
			configFileName = System.getProperty(CONFIG_FILE_PROPERTY);
		}
		if (configFileName == null) {
			configFileName = System.getenv(CONFIG_FILE_PROPERTY);
		}
		if (configFileName == null) {
			configFileName = DEFAULT_CONFIG_FILENAME;
		}
		
		File configFile = new File(configFileName);
		if (! configFile.exists()) {
			throw new RuntimeException("catalog config file not found '" + configFile + "'!"
					+ ".. use default " + DEFAULT_CONFIG_FILENAME 
					+ " or override with property " + CONFIG_FILE_PROPERTY);
		}
		try {
			catalogFacadeInstance.loadConfigFile(configFile);
		} catch(Exception ex) {
			log.error("Failed to load config file '" + configFile + "'", ex);
		}
	}


}

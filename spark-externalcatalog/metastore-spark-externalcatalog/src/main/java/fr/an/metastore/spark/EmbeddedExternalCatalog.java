package fr.an.metastore.spark;

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
 */
@Slf4j
public class EmbeddedExternalCatalog extends JavaAdapterExternalCatalog {

	static {
		log.info("<cinit> fr.an.metastore.spark.EmbeddedExternalCatalog");
		System.out.println("(stdout) <cinit> fr.an.metastore.spark.EmbeddedExternalCatalog");
	}
	
	/**
	 * called by introspection from spark config
	 */
	public EmbeddedExternalCatalog() {
		super(new EmbeddedCatalogFacade(), new SparkModelConverter());
		log.info("done EmbeddedExternalCatalog.<init>()");
		System.out.println("(stdout) done EmbeddedExternalCatalog.<init>()");
	}
	
	
}

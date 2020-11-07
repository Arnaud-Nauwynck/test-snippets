package fr.an.metastore.impl.loader;


import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.FunctionResourceTypeEnum;
import lombok.Data;

@Data
public class EmbeddedCatalogConfig {

	List<String> importFiles;
	
	List<DatabaseCatalogConfig> databases;

	List<CatalogTablePropertiesConfig> importableTableProperties;

	// --------------------------------------------------------------------------------------------

	/**
	 * see corresponding {@link ImmutableCatalogDatabaseDef}
	 */
	@Data
	public static class DatabaseCatalogConfig {
		String name; // databaseName
		String description;
		URI locationUri;
		Map<String, String> properties;

		List<CatalogTablePropertiesConfig> importableTableProperties;
		CatalogTablePropertiesConfig sharedTableProperties;
		// TOADD CatalogTableContextConfig tableContexts;
		
		List<CatalogTableConfig> tables;
		
		List<CatalogFunctionConfig> functions;
	
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * cf corresponding class {@link ImmutableCatalogTableDef} 
	 */
	@Data
	public static class CatalogTableConfig {
		String name; // tableName;
		
		List<String> importTablePropertiesRefs;
		
		CatalogTableTypeEnum tableType;
		CatalogStorageFormatConfig storage;
		CatalogTableSchemaConfig schema;
		String provider;
		List<String> partitionColumnNames;
		BucketSpecConfig bucketSpec;
		String owner;
		long createTime;
		// public long lastAccessTime;
		String createVersion;
		Map<String, String> properties;
		CatalogStatisticsConfig stats;
		String viewText;
		String comment;
		List<String> unsupportedFeatures;
		boolean tracksPartitionsInCatalog;
		boolean schemaPreservesCase = true;
		Map<String, String> ignoredProperties;
		String viewOriginalText;
	}

	/**
	 * importable CatalogTable properties fragment
	 */
	@Data
	public static class CatalogTablePropertiesConfig {
		String name; // for importing ref
		
		CatalogTableTypeEnum tableType;
		CatalogStorageFormatConfig storage;
		CatalogTableSchemaConfig schema;
		String provider;
		List<String> partitionColumnNames;
		BucketSpecConfig bucketSpec;
		String owner;
		long createTime;
		String createVersion;
		Map<String, String> properties;
		CatalogStatisticsConfig stats;
		String viewText;
		String comment;
		List<String> unsupportedFeatures;
		boolean tracksPartitionsInCatalog;
		boolean schemaPreservesCase = true;
		Map<String, String> ignoredProperties;
		String viewOriginalText;
	}

	
	/**
	 * cf corresponding class {@link ImmutableCatalogStorageFormat}
	 */
	@Data
	public static class CatalogStorageFormatConfig {
		URI locationUri;
	    String inputFormat;
	    String outputFormat;
	    String serde;
	    boolean compressed;
	    Map<String, String> properties;
	}

	@Data
	public static class CatalogTableSchemaConfig {
		// TODO
		String avroSchema;
		String avroSchemaFile;
		String sampleAvroDataFile;
		String sampleParquetDataFile;
		
	}
	
	/**
	 * cf corresponding class {@link ImmutableBucketSpec}
	 */
	@Data
	public static class BucketSpecConfig {
		int numBuckets;
		List<String> bucketColumnNames;
		List<String> sortColumnNames;
	}
	
	/**
	 * cf corresponding {@link ImmutableCatalogTableStatistics}
	 */
	@Data
	public static class CatalogStatisticsConfig {
		BigInteger sizeInBytes;
		BigInteger rowCount;
		List<CatalogColumnStatConfig> colStats;
	}

	/**
	 * cf corresponding class {@link ImmutableCatalogColumnStat}
	 */
	@Data
	public static class CatalogColumnStatConfig {
		BigInteger distinctCount;
		String min;
		String max;
		BigInteger nullCount;
		Long avgLen;
		Long maxLen;
		HistogramConfig histogram;
		int version; // = CatalogColumnStat.VERSION;
	}

	/**
	 * cf corresponding class {@link ImmutableHistogram}
	 */
	@Data
	public static class HistogramConfig {
		BigInteger sizeInBytes;
		BigInteger rowCount;
	    // TODO
		//	List<ColumnStatConfig> attributeStats;
	}

	
	// --------------------------------------------------------------------------------------------

	/**
	 * cf corresponding class {@link ImmutableCatalogFunctionDef}
	 */
	@Data
	public static class CatalogFunctionConfig {
		String name; // functionName
		String className;
		List<CatalogFunctionResourceConfig> resources;
	}

	/**
	 * cf corresponding {@link ImmutableCatalogFunctionResource}
	 */
	@Data
	public static class CatalogFunctionResourceConfig {
		FunctionResourceTypeEnum resourceType;
		URI uri;
	}

}

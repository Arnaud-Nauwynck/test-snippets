package fr.an.metastore.impl.loader;

import static fr.an.metastore.api.utils.MetastoreListUtils.map;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.FunctionResourceTypeEnum;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef.ImmutableCatalogFunctionResource;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableBucketSpec;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStorageFormat;
import fr.an.metastore.api.immutable.ImmutableStructType;
import fr.an.metastore.api.immutable.ImmutableStructType.ImmutableStructField;
import fr.an.metastore.api.utils.AvroImmutableStructTypeUtils;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.BucketSpecConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogFunctionConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogFunctionResourceConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogStorageFormatConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogTableConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.CatalogTableSchemaConfig;
import fr.an.metastore.impl.loader.EmbeddedCatalogConfig.DatabaseCatalogConfig;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import lombok.val;

public class ResolvedConfigToModelBuilder {

	/**
	 * recursive catalog -> db -> tables + functions, register corresponding model objects
	 */
	public void registerResolvedConfToModel(EmbeddedCatalogConfig catalogConf, CatalogModel catalogModel) {
		for(val dbConf : catalogConf.getDatabases()) {
			val dbModel = registerDatabaseModel(catalogModel, dbConf);
		
			if (dbConf.getTables() != null) {
				for(val tableConf : dbConf.getTables()) {
					registerTableModel(dbModel, tableConf);
				}
			}

			if (dbConf.getFunctions() != null) {
				for(val funcConf : dbConf.getFunctions()) {
					registerFunctionModel(dbModel, funcConf);
				}
			}

		}
	}

	protected DatabaseModel registerDatabaseModel(CatalogModel catalogModel,
			DatabaseCatalogConfig dbConf) {
		String name = ensureValidString(dbConf.getName(), "db name");
		String description = dbConf.description;
		URI locationUri = dbConf.locationUri;
		ImmutableMap<String, String> properties = toMapOrEmpty(dbConf.properties);

		ImmutableCatalogDatabaseDef dbDef = new ImmutableCatalogDatabaseDef(
				name, description, locationUri, properties);
		DatabaseModel dbModel = new DatabaseModel(catalogModel, dbDef);
		catalogModel.addDatabase(dbModel);
		return dbModel;
	}

	// register Table
	// --------------------------------------------------------------------------------------------
	
	protected TableModel registerTableModel(DatabaseModel dbModel, CatalogTableConfig tableConf) {
		String tableName = ensureValidString(tableConf.name, "table name");
		CatalogTableId identifier = new CatalogTableId(dbModel.getName(), tableName);
		CatalogTableTypeEnum tableType = ensureNotNull(tableConf.tableType, "table type");
		ImmutableCatalogStorageFormat storage = toStorageFormat(tableConf.storage);
		ImmutableStructType schema = toSchema(tableConf.schema);
		String provider = tableConf.provider;
		ImmutableList<String> partitionColumnNames = toListOrEmpty(tableConf.partitionColumnNames);
		ImmutableBucketSpec bucketSpec = toBucketSpec(tableConf.bucketSpec);
		String owner = tableConf.owner;
		long createTime = tableConf.createTime;
		// long lastAccessTime = tableConf.lastAccessTime;
		String createVersion = tableConf.createVersion;
		ImmutableMap<String, String> properties = toMapOrEmpty(tableConf.properties);
		// CatalogStatisticsDTO stats;
		String viewText = tableConf.viewText;
		String comment = tableConf.comment;
		ImmutableList<String> unsupportedFeatures = toListOrEmpty(tableConf.unsupportedFeatures);
		boolean tracksPartitionsInCatalog = tableConf.tracksPartitionsInCatalog; // = false;
		boolean schemaPreservesCase = tableConf.schemaPreservesCase; // = true;
		ImmutableMap<String, String> ignoredProperties = toMapOrEmpty(tableConf.ignoredProperties);
		String viewOriginalText = tableConf.viewOriginalText;

		ImmutableCatalogTableDef tableDef = ImmutableCatalogTableDef.builder()
				.identifier(identifier)
				.tableType(tableType)
				.storage(storage)
				.schema(schema)
				.provider(provider)
				.partitionColumnNames(partitionColumnNames)
				.bucketSpec(bucketSpec)
				.owner(owner)
				.createTime(createTime)
				//.lastAccessTime(lastAccessTime)
				.createVersion(createVersion)
				.properties(properties)
				// CatalogStatisticsDTO stats;
				.viewText(viewText)
				.comment(comment)
				.unsupportedFeatures(unsupportedFeatures)
				.tracksPartitionsInCatalog(tracksPartitionsInCatalog)
				.schemaPreservesCase(schemaPreservesCase)
				.ignoredProperties(ignoredProperties)
				.viewOriginalText(viewOriginalText)
				.build();
		TableModel tableModel = new TableModel(dbModel, tableDef);
		dbModel.addTable(tableModel);
		return tableModel;
	}

	private ImmutableCatalogStorageFormat toStorageFormat(CatalogStorageFormatConfig storage) {
		ensureNotNull(storage, "table storage");
		URI locationUri = ensureNotNull(storage.locationUri, "table storage locationUri");
	    String inputFormat = storage.inputFormat;
	    String outputFormat = storage.outputFormat;
	    String serde = storage.serde;
	    boolean compressed = storage.compressed;
	    ImmutableMap<String, String> properties = toMapOrEmpty(storage.properties);
	    return new ImmutableCatalogStorageFormat(locationUri, inputFormat, outputFormat, serde, compressed, properties);
	}

	private ImmutableStructType toSchema(CatalogTableSchemaConfig schemaConfig) {
		ensureNotNull(schemaConfig, "table schema");
		String avroSchemaContent = schemaConfig.avroSchema;
		String avroSchemaFile = schemaConfig.avroSchemaFile;
		String sampleAvroDataFile = schemaConfig.sampleAvroDataFile;
		String sampleParquetDataFile = schemaConfig.sampleParquetDataFile;
		Schema avroSchema = null;
		if (avroSchemaContent != null) {
			avroSchema = new Schema.Parser().parse(avroSchemaContent);
		} else if (avroSchemaFile != null) {
			File file = new File(avroSchemaFile);
			try (InputStream schemaInput = new BufferedInputStream(new FileInputStream(file))) {
				avroSchema = new Schema.Parser().parse(schemaInput);
	        } catch(Exception ex) {
	        	throw new RuntimeException("Failed to read avro schema file '" + file + "'", ex);
	        }
		} else if (sampleAvroDataFile != null) {
			File file = new File(sampleAvroDataFile);
			GenericDatumReader<GenericRecord> genericAvroReader = new GenericDatumReader<GenericRecord>();
	        try (DataFileReader<GenericRecord> reader = new DataFileReader<GenericRecord>(file, genericAvroReader)) {
	        	avroSchema = reader.getSchema();
	        } catch(Exception ex) {
	        	throw new RuntimeException("Failed to read sample avro file '" + file + "'", ex);
	        }
		} else if (sampleParquetDataFile != null) {
			File file = new File(sampleParquetDataFile);
			// TODO
			
		} else {
			// TODO
		}

		if (avroSchema != null) {
			return AvroImmutableStructTypeUtils.fromAvroSchema(avroSchema);
		}
		
		ImmutableList<ImmutableStructField> fields = ImmutableList.of();
		// TODO
		
		ImmutableStructType res = new ImmutableStructType(fields);
		return res;
	}

	private ImmutableBucketSpec toBucketSpec(BucketSpecConfig bucketSpec) {
		if (bucketSpec == null) {
			return null; // or empty BucketSpec?
		}
		return new ImmutableBucketSpec(
				bucketSpec.numBuckets,
				toListOrEmpty(bucketSpec.bucketColumnNames),
				toListOrEmpty(bucketSpec.sortColumnNames));
	}

	// register Function
	// --------------------------------------------------------------------------------------------

	private FunctionModel registerFunctionModel(DatabaseModel dbModel, CatalogFunctionConfig funcConf) {
		String funcName = ensureValidString(funcConf.name, "func name");
		CatalogFunctionId identifier = new CatalogFunctionId(dbModel.getName(), funcName);
		String className = ensureValidString(funcConf.className, "func className");
		List<ImmutableCatalogFunctionResource> resources = toFuncResources(funcConf.resources);
		ImmutableCatalogFunctionDef def = new ImmutableCatalogFunctionDef(
				identifier, className, resources);
		FunctionModel funcModel = new FunctionModel(dbModel, def);
		dbModel.addFunction(funcModel);
		return funcModel;
	}

	private List<ImmutableCatalogFunctionResource> toFuncResources(List<CatalogFunctionResourceConfig> resourceConfs) {
		if (resourceConfs == null || resourceConfs.isEmpty()) return ImmutableList.of();
		return ImmutableList.copyOf(map(resourceConfs, x -> toFuncResource(x)));
	}

	private ImmutableCatalogFunctionResource toFuncResource(CatalogFunctionResourceConfig resourceConf) {
		FunctionResourceTypeEnum resourceType = ensureNotNull(resourceConf.resourceType, "function resource type");
		URI uri = ensureNotNull(resourceConf.uri, "function resource uri");
		return new ImmutableCatalogFunctionResource(resourceType, uri);
	}

	
	// Helper methods
	// --------------------------------------------------------------------------------------------

	protected String ensureValidString(String text, String msg) {
		ensureNotNull(text, msg);
		if (text.isEmpty()) {
			throw new IllegalArgumentException("empty " + msg);
		}
		return text;
	}

	protected <T> T ensureNotNull(T obj, String msg) {
		if (obj == null) {
			throw new IllegalArgumentException("null " + msg);
		}
		return obj;
	}

	protected <K,V> ImmutableMap<K,V> toMapOrEmpty(Map<K,V> src) {
		return (src != null)? ImmutableMap.copyOf(src) : ImmutableMap.of();
	}

	protected <T> ImmutableList<T> toListOrEmpty(List<T> src) {
		return (src != null)? ImmutableList.copyOf(src) : ImmutableList.of();
	}

}

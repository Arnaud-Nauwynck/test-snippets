package fr.an.metastore.spark.modeladapter;

import static fr.an.metastore.api.utils.MetastoreListUtils.map;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.BucketSpec;
import org.apache.spark.sql.catalyst.catalog.CatalogColumnStat;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.CatalogTableType;
import org.apache.spark.sql.catalyst.catalog.FunctionResource;
import org.apache.spark.sql.catalyst.catalog.FunctionResourceType;
import org.apache.spark.sql.catalyst.plans.logical.Histogram;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableProvider;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.internal.SQLConf;
import org.apache.spark.sql.types.StructType;

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
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogColumnStat;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStorageFormat;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableHistogram;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.api.utils.NotImpl;
import fr.an.metastore.spark.impl.SparkV2CatalogTable;
import fr.an.metastore.spark.util.ScalaCollUtils;
import fr.an.metastore.spark.util.SparkAvroSchemaConversionUtils;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.math.BigInt;

@Slf4j
public class SparkModelConverter {

	// Database
	// --------------------------------------------------------------------------------------------

	public ImmutableCatalogDatabaseDef toImmutableDatabaseDef(CatalogDatabase src) {
		return ImmutableCatalogDatabaseDef.builder()
				.name(src.name())
				.description(src.description())
				.locationUri(src.locationUri())
				.properties(toImmutableMapCopy(src.properties()))
				.build();
	}

	public CatalogDatabase toSparkDatabase(ImmutableCatalogDatabaseDef src) {
		return new CatalogDatabase(
			    src.getName(),
			    src.getDescription(),
			    src.getLocationUri(),
			    toScalaImmutableMap(src.getProperties())
				);
	}

	// Table
	// --------------------------------------------------------------------------------------------

	public ImmutableCatalogTableDef toImmutableTableDef(CatalogTable src) {
		val identifier = toTableId(src.identifier());
		CatalogTableTypeEnum tableType = toTableType(src.tableType());
		ImmutableCatalogStorageFormat storage = toImmutableStorageFormat(src.storage());
		return ImmutableCatalogTableDef.builder()
				.identifier(identifier)
				.tableType(tableType)
				.storage(storage)
// TODO
//		ImmutableStructType schema;
//		String provider;
//		ImmutableList<String> partitionColumnNames;
//		ImmutableBucketSpec bucketSpec;
//		String owner;
//		long createTime;
//		String createVersion;
//		ImmutableMap<String, String> properties;
//		String viewText;
//		String comment;
//		ImmutableList<String> unsupportedFeatures;
//		boolean tracksPartitionsInCatalog; // = false;
//		boolean schemaPreservesCase; // = true;
//		ImmutableMap<String, String> ignoredProperties;
//		String viewOriginalText;
//
				.build();
	}
	
	public CatalogTableId toTableId(TableIdentifier src) {
		return new CatalogTableId(toJavaOpt(src.database()), src.table());
	}

	public CatalogTable toSparkTable(ImmutableCatalogTableDef src) {
		TableIdentifier identifier = new TableIdentifier(
				src.identifier.table, toScalaOption(src.identifier.database));
		CatalogTableType tableType = toSparkTableType(src.tableType);
		CatalogStorageFormat storage = toSparkStorageFormat(src.storage);
		StructType schema = (StructType) src.schema.getAsSparkStruct();
	    scala.Option<String> provider = toScalaOption(src.provider);
	    scala.collection.Seq<String> partitionColumnNames = toScalaSeq(src.partitionColumnNames);
	    scala.Option<BucketSpec> bucketSpec = toSparkBucketSpecOpt(src.bucketSpec);
	    String owner = toStringOrEmpty(src.owner);
	    long createTime = src.createTime;
	    long lastAccessTime = -1; // TOCHANGE
	    String createVersion = toStringOrEmpty(src.createVersion);
	    scala.collection.immutable.Map<String, String> properties = toScalaImmutableMap(src.properties);
	    scala.Option<CatalogStatistics> stats = scala.Option.empty(); // TODO
	    scala.Option<String> viewText = toScalaOption(src.viewText);
	    scala.Option<String> comment = toScalaOption(src.comment);
	    scala.collection.Seq<String> unsupportedFeatures = toScalaSeq(src.unsupportedFeatures);
	    boolean tracksPartitionsInCatalog = src.tracksPartitionsInCatalog;
	    boolean schemaPreservesCase = src.schemaPreservesCase;
	    scala.collection.immutable.Map<String, String> ignoredProperties = toScalaImmutableMap(src.ignoredProperties);
	    scala.Option<String> viewOriginalText = toScalaOption(src.viewOriginalText);
	    	
		return new CatalogTable(
			    identifier, tableType, storage, schema, provider, 
			    partitionColumnNames,
			    bucketSpec, owner, createTime, lastAccessTime, createVersion, 
			    properties,
			    stats, viewText, comment, unsupportedFeatures, tracksPartitionsInCatalog,
			    schemaPreservesCase, 
			    ignoredProperties, 
			    viewOriginalText);
	}

	private String toStringOrEmpty(String src) {
		return (src != null)? src : "";
	}

	private scala.Option<BucketSpec> toSparkBucketSpecOpt(ImmutableBucketSpec src) {
		return (src != null)? scala.Option.apply(toSparkBucketSpec(src)) : scala.Option.empty();
	}
	
	private BucketSpec toSparkBucketSpec(ImmutableBucketSpec src) {
		return new BucketSpec(
				src.numBuckets,
				toScalaSeq(src.bucketColumnNames),
				toScalaSeq(src.sortColumnNames));
	}

	public Seq<CatalogTable> toSparkTables(List<ImmutableCatalogTableDef> src) {
		return toScalaSeq(map(src, t -> toSparkTable(t)));
	}

	public Table toSparkTableV2(ImmutableCatalogTableDef tableDef) {
		// does not work yet when querying data on table ...
		// return new SparkV2CatalogTable(tableDef);

		// does not work either when querying data on table ...
		// CatalogTable v1Table = sparkConverter.toSparkTable(tableDef);
		// return Accessor_V1Table.createV1Table(v1Table);
		String provider = tableDef.provider;
		if (provider == null) {
			String serde = tableDef.storage.serde;
			if (serde != null && serde.toLowerCase().endsWith("parquet")) {
				provider = "parquet";
			}
		}
		SQLConf conf = SparkSession.active().sqlContext().conf();
		Option<TableProvider> tableProviderOpt = org.apache.spark.sql.execution.datasources.DataSource$.MODULE$
					.lookupDataSourceV2(provider, conf);
		if (tableProviderOpt.isEmpty()) {
			// cf code... "parquet", "orc", ... are explicitly configured to use V1 !!!
			// val useV1Sources = conf.getConf(SQLConf.USE_V1_SOURCE_LIST).toLowerCase(Locale.ROOT).split(","); // .map(_.trim)
			// force explicit retry..
			try {
				Object tableProviderObj = Class.forName(provider).newInstance();
				if (tableProviderObj instanceof TableProvider) {
					tableProviderOpt = scala.Option.apply((TableProvider) tableProviderObj);
				}
			} catch(Exception ex) {
				log.error("Failed to instanciate " + provider + " TableProvider " + ex.getMessage());
			}
		}
		if (tableProviderOpt.isEmpty()) {
			// does not work yet when querying data on table ...
			log.warn("tableProvider not found for '" + provider + "' .. use dummy (unreadable) Table description");
			return new SparkV2CatalogTable(tableDef);
		}
		StructType schema = SparkAvroSchemaConversionUtils.schemaDefToSparkStructType(tableDef.schema);
		if (schema == null) {
			log.error("NULL schema for table ??");
			// redo for debug?!
			schema = SparkAvroSchemaConversionUtils.schemaDefToSparkStructType(tableDef.schema);
		}
		Transform[] partitioning = new Transform[0]; // TODO
		Table res = tableProviderOpt.get().getTable(schema, partitioning, tableDef.properties);
		return res;
	}


	
	// Table properties
	// --------------------------------------------------------------------------------------------

	protected CatalogTableTypeEnum toTableType(CatalogTableType src) {
		switch(src.name()) { // can not use package protected CatalogTableType$MODULE
		case "EXTERNAL":
			return CatalogTableTypeEnum.EXTERNAL;
		case "MANAGED":
			return CatalogTableTypeEnum.MANAGED;
		case "VIEW":
			return CatalogTableTypeEnum.VIEW;
		default:
			throw new IllegalStateException();
		}
	}

	protected CatalogTableType toSparkTableType(CatalogTableTypeEnum src) {
		switch(src) {
		case EXTERNAL: return CatalogTableType.EXTERNAL();
		case MANAGED: return CatalogTableType.MANAGED();
		case VIEW: return CatalogTableType.VIEW();
		default: throw new IllegalStateException();
		}
	}
	
	public ImmutableCatalogStorageFormat toImmutableStorageFormat(CatalogStorageFormat src) {
		URI locationUri = toJavaOpt(src.locationUri());
	    String inputFormat = toJavaOpt(src.inputFormat());
	    String outputFormat = toJavaOpt(src.outputFormat());
	    String serde = toJavaOpt(src.serde());
	    boolean compressed = src.compressed();
	    ImmutableMap<String, String> properties = toImmutableMapCopy(src.properties());
		return new ImmutableCatalogStorageFormat(locationUri, //
				inputFormat, outputFormat, serde, compressed, properties);
	}

	public CatalogStorageFormat toSparkStorageFormat(ImmutableCatalogStorageFormat src) {
		scala.Option<URI> locationUri = toScalaOption(src.locationUri);
		scala.Option<String> inputFormat = toScalaOption(src.inputFormat);
		scala.Option<String> outputFormat = toScalaOption(src.outputFormat);
		scala.Option<String> serde = toScalaOption(src.serde);
	    Boolean compressed = src.compressed;
	    scala.collection.immutable.Map<String, String> properties = toScalaImmutableMap(src.properties);
		return new CatalogStorageFormat(locationUri, inputFormat, outputFormat, serde, 
				compressed, properties);
	}

	// PartitionSpec
	// --------------------------------------------------------------------------------------------

	public List<ImmutablePartitionSpec> toImmutablePartitionSpecs(
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> src) {
		List<scala.collection.immutable.Map<String, String>> sparkPartSpecList = toJavaList(src);
		return map(sparkPartSpecList, spec -> toImmutablePartitionSpec(spec));
	}

	public ImmutablePartitionSpec toImmutablePartitionSpec(scala.collection.immutable.Map<String, String> src) {
		Map<String,String> data = toImmutableMapCopy(src);
		return new ImmutablePartitionSpec(data);
	}

	public scala.collection.immutable.Map<String, String> toSparkPartitionSpec(ImmutablePartitionSpec src) {
		return toScalaImmutableMap(src.getData());
	}

	public scala.collection.Seq<scala.collection.immutable.Map<String, String>> toSparkPartitionSpecs(List<ImmutablePartitionSpec> src) {
		return toScalaSeq(map(src, x -> toSparkPartitionSpec(x)));
	}

	public scala.Option<CatalogTablePartition> toSparkTablePartitionOpt(Optional<CatalogTablePartitionInfo> src) {
		return (src.isPresent())? toScalaOption(toSparkTablePartition(src.get())) : scala.Option.empty();
	}

	// TablePartition
	// --------------------------------------------------------------------------------------------

	public List<ImmutableCatalogTablePartitionDef> toImmutableTablePartitionDefs(scala.collection.Seq<CatalogTablePartition> sparkParts) {
		return map(toJavaList(sparkParts), x -> toImmutableTablePartitionDef(x));
	}

	public ImmutableCatalogTablePartitionDef toImmutableTablePartitionDef(CatalogTablePartition spark) {
		throw NotImpl.notImplEx(); // TODO
//		return ImmutableCatalogTablePartitionDef.builder()
//				.build();
	}

	public CatalogTablePartition toSparkTablePartition(CatalogTablePartitionInfo src) {
		ImmutableCatalogTablePartitionDef def = src.def;
		scala.collection.immutable.Map<String, String> spec = toSparkPartitionSpec(def.spec);
		CatalogStorageFormat storage = toSparkStorageFormat(def.storage);
	    scala.collection.immutable.Map<String,String> parameters = toScalaImmutableMap(def.parameters);
	    long createTime = def.createTime;
	    long lastAccessTime = src.lastAccessTime;
	    scala.Option<CatalogStatistics> stats = toSparkOptionCatalogStatistics(src.stats);
		return new CatalogTablePartition(spec, storage, parameters, createTime, lastAccessTime, stats);
	}


	public Seq<CatalogTablePartition> toSparkTablePartitions(List<CatalogTablePartitionInfo> src) {
		return toScalaSeq(map(src, x -> toSparkTablePartition(x)));
	}
	
	// TableStatistics
	// --------------------------------------------------------------------------------------------
	
	public ImmutableCatalogTableStatistics toImmutableTableStatistics(CatalogStatistics src) {
		BigInteger sizeInBytes = toJavaBigInt(src.sizeInBytes());
		BigInteger rowCount = toJavaBigInt(src.rowCount());
		ImmutableMap<String, ImmutableCatalogColumnStat> colStats = null;

		return ImmutableCatalogTableStatistics.builder()
				.sizeInBytes(sizeInBytes).rowCount(rowCount).colStats(colStats)
				.build();
	}

	public ImmutableCatalogColumnStat toImmutableCatalogColumnStat(CatalogColumnStat src) {
		BigInteger distinctCount = toJavaBigInt(src.distinctCount());
		Long maxLen = optObjectToJavaLong(src.maxLen());
		String min = toJavaOpt(src.min());
		String max = toJavaOpt(src.max());
		BigInteger nullCount = toJavaBigInt(src.nullCount());
		Long avgLen = optObjectToJavaLong(src.avgLen());
		return ImmutableCatalogColumnStat.builder()
				.distinctCount(distinctCount)
				.min(min)
				.max(max)
				.nullCount(nullCount)
				.avgLen(avgLen)
				.maxLen(maxLen)
				.histogram(toImmutableHistogram(src.histogram()))
				.version(src.version())
				.build();
	}

	public ImmutableHistogram toImmutableHistogram(scala.Option<Histogram> src) {
		return src.isDefined()? toImmutableHistogram(src.get()) : null;
	}

	public ImmutableHistogram toImmutableHistogram(Histogram src) {
		return ImmutableHistogram.builder()
				// TODO
				.build();
	}
	
	public CatalogStatistics toSparkCatalogStatistics(ImmutableCatalogTableStatistics src) {
		scala.math.BigInt sizeInBytes = toScalaBigInt(src.sizeInBytes);
		scala.Option<scala.math.BigInt> rowCount = toScalaOptionBigInt(src.rowCount);
		scala.collection.immutable.Map<String,CatalogColumnStat> colStats = null;
			// TODO
		    // Map[String, CatalogColumnStat] = Map.empty;

		return new CatalogStatistics(sizeInBytes, rowCount, colStats);
	}

	public scala.Option<CatalogStatistics> toSparkOptionCatalogStatistics(ImmutableCatalogTableStatistics src) {
		return (src != null)? toScalaOption(toSparkCatalogStatistics(src)) : scala.Option.empty();
	}

	// Function
	// --------------------------------------------------------------------------------------------

	public ImmutableCatalogFunctionDef toImmutableFunctionDef(CatalogFunction func) {
		CatalogFunctionId identifier = toFunctionId(func.identifier());
		String className = func.className();
		List<ImmutableCatalogFunctionResource> resources = toImmutableFunctionResources(func.resources());
		return new ImmutableCatalogFunctionDef(identifier, className, resources);
	}

	public CatalogFunction toSparkFunction(ImmutableCatalogFunctionDef src) {
		FunctionIdentifier identifier = toSparkFunctionId(src.identifier);
		String className = src.className;
		scala.collection.Seq<FunctionResource> resources = toSparkFunctionResources(src.resources);
		return new CatalogFunction(identifier, className, resources);
	}

	public CatalogFunctionId toFunctionId(FunctionIdentifier sparkId) {
		return new CatalogFunctionId(toJavaOpt(sparkId.database()), sparkId.funcName());
	}

	public FunctionIdentifier toSparkFunctionId(CatalogFunctionId src) {
		return new FunctionIdentifier(src.funcName, toScalaOption(src.database));
	}

	public List<ImmutableCatalogFunctionResource> toImmutableFunctionResources(scala.collection.Seq<FunctionResource> resources) {
		return map(toJavaList(resources), x -> toImmutableFunctionResource(x));
	}

	public ImmutableCatalogFunctionResource toImmutableFunctionResource(FunctionResource src) {
		val resourceType = toFunctionResourceType(src.resourceType());
		URI uri = toJavaURI(src.uri());
		return new ImmutableCatalogFunctionResource(resourceType, uri);
	}

	public FunctionResource toSparkFunctionResource(ImmutableCatalogFunctionResource src) {
		val resourceType = toSparkFunctionResourceType(src.resourceType);
		val uri = src.uri.toString();
		return new FunctionResource(resourceType, uri);
	}

	public scala.collection.Seq<FunctionResource> toSparkFunctionResources(List<ImmutableCatalogFunctionResource> src) {
		return toScalaSeq(map(src, x -> toSparkFunctionResource(x)));
	}
	
	public FunctionResourceTypeEnum toFunctionResourceType(FunctionResourceType src) {
		return FunctionResourceTypeEnum.valueOf(src.resourceType());
	}

	public FunctionResourceType toSparkFunctionResourceType(FunctionResourceTypeEnum src) {
		return FunctionResourceType.fromString(src.name());
	}

	private URI toJavaURI(String src) {
		try {
			return new URI(src);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Bad URI", ex);
		}
	}

	private URI toJavaURI(scala.Option<String> src) {
		return src.isDefined()? toJavaURI(src.get()) : null;
	}
	
	// --------------------------------------------------------------------------------------------

	protected <T> scala.Option<T> toScalaOption(T src) {
		return (src != null)? scala.Option.apply(src) : scala.Option.empty();
	}

	private <A> A toJavaOpt(scala.Option<A> opt) {
		return opt.isDefined()? opt.get() : null;
	}

	// strange Option<Object> in spark instead of Option<Long> ?
	private Long optObjectToJavaLong(scala.Option<Object> opt) {
		if (!opt.isDefined()) return null;
		Object obj = opt.get();
		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return null;
	}

	private <A> List<A> toJavaList(scala.collection.Seq<A> ls) {
		return JavaConverters.seqAsJavaList(ls);
	}
	
	private <A> scala.collection.Seq<A> toScalaSeq(java.util.Collection<A> ls) {
		return JavaConverters.collectionAsScalaIterable(ls).toSeq();
	}
	
	protected <K,V> ImmutableMap<K,V> toImmutableMapCopy(scala.collection.Map<K,V> src) {
		return ImmutableMap.copyOf(ScalaCollUtils.mapAsJavaMap(src));
	}

	protected <K,V> scala.collection.immutable.Map<K,V> toScalaImmutableMap(java.util.Map<K,V> src) {
		return ScalaCollUtils.toScalaImutableMap(src);
	}

	protected <K,V> scala.collection.mutable.Map<K,V> toScalaMutableMap(java.util.Map<K,V> src) {
		return ScalaCollUtils.toScalaMutableMap(src);
	}

	protected BigInt toScalaBigInt(BigInteger src) {
		return new BigInt(src);
	}

	protected BigInteger toJavaBigInt(BigInt src) {
		return (src != null)? src.bigInteger() : null;
	}

	protected BigInteger toJavaBigInt(scala.Option<BigInt> src) {
		return (src != null && src.isDefined())? src.get().bigInteger() : null;
	}

	protected scala.Option<BigInt> toScalaOptionBigInt(BigInteger src) {
		return (src != null)? scala.Option.apply(toScalaBigInt(src)) : scala.Option.empty();
	}



}

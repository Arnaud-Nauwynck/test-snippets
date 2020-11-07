package fr.an.metastore.spark.modeladapter;

import static fr.an.metastore.impl.utils.MetastoreListUtils.map;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.TableIdentifier;
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

import com.google.common.collect.ImmutableMap;

import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.FunctionResourceTypeEnum;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef.ImmutableCatalogFunctionResource;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogColumnStat;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStorageFormat;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableHistogram;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.impl.utils.NotImpl;
import fr.an.metastore.spark.util.ScalaCollUtils;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.math.BigInt;

public class SparkModelConverter {

	// Database
	// --------------------------------------------------------------------------------------------

//	public static CatalogDatabase toSparkDatabase(DatabaseModel src) {
//		String name = src.getName();
//	    String description = src.getDescription();
//	    URI locationUri = src.getLocationUri();
//		java.util.Map<String, String> props = src.getProperties();
//		scala.collection.immutable.Map<String, String> scalaProps = ScalaCollUtils.toScalaImutableMap(props);
//
//		return new CatalogDatabase(name, description, locationUri, scalaProps);
//	}

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
		return ImmutableCatalogTableDef.builder()
				.identifier(new CatalogTableId(src.identifier().database().getOrElse(() -> null), 
						src.identifier().table()))
// TODO
//		CatalogTableTypeEnum tableType;
//		
//		ImmutableCatalogStorageFormat storage;
//
//		ImmutableStructType schema;
//
//		String provider;
//		
//		ImmutableList<String> partitionColumnNames;
//		
//		ImmutableBucketSpec bucketSpec;
//		
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
	
	public CatalogTable toSparkTable(ImmutableCatalogTableDef src) {
		TableIdentifier identifier = new TableIdentifier(
				src.identifier.database, toScalaOption(src.identifier.table));
		CatalogTableType catalogType = toSparkTableType(src.tableType);
//		return new CatalogTable(
//			    identifier,
//			    tableType,
//			    storage: CatalogStorageFormat,
//			    schema: StructType,
//			    provider: Option[String] = None,
//			    partitionColumnNames: Seq[String] = Seq.empty,
//			    bucketSpec: Option[BucketSpec] = None,
//			    owner: String = "",
//			    createTime: Long = System.currentTimeMillis,
//			    lastAccessTime: Long = -1,
//			    createVersion: String = "",
//			    properties: Map[String, String] = Map.empty,
//			    stats: Option[CatalogStatistics] = None,
//			    viewText: Option[String] = None,
//			    comment: Option[String] = None,
//			    unsupportedFeatures: Seq[String] = Seq.empty,
//			    tracksPartitionsInCatalog: Boolean = false,
//			    schemaPreservesCase: Boolean = true,
//			    ignoredProperties: Map[String, String] = Map.empty,
//			    viewOriginalText: Option[String] = None) {
//				);
		throw NotImpl.notImplEx(); // TODO
	}

	public Seq<CatalogTable> toSparkTables(List<ImmutableCatalogTableDef> src) {
		return toScalaSeq(map(src, t -> toSparkTable(t)));
	}
	
	protected static CatalogTableType toSparkTableType(CatalogTableTypeEnum src) {
		switch(src) {
		case EXTERNAL: return CatalogTableType.EXTERNAL();
		case MANAGED: return CatalogTableType.MANAGED();
		case VIEW: return CatalogTableType.VIEW();
		default: throw new IllegalStateException();
		}
	}
	

	private CatalogStorageFormat toSparkStorageFormat(ImmutableCatalogStorageFormat src) {
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
		return ImmutableCatalogFunctionDef.builder()
				.identifier(identifier)
				.className(className)
				.resources(resources)
				.build();
	}

	private List<ImmutableCatalogFunctionResource> toImmutableFunctionResources(scala.collection.Seq<FunctionResource> resources) {
		return map(toJavaList(resources), x -> toImmutableFunctionResource(x));
	}

	public CatalogFunctionId toFunctionId(FunctionIdentifier sparkId) {
		return new CatalogFunctionId(toJavaOpt(sparkId.database()), sparkId.funcName());
	}

	public ImmutableCatalogFunctionResource toImmutableFunctionResource(FunctionResource src) {
		URI uri = toJavaURI(src.uri());
		FunctionResourceTypeEnum resourceType = toFunctionResourceType(src.resourceType());
		return new ImmutableCatalogFunctionResource(
				resourceType, uri);
	}

	private FunctionResourceTypeEnum toFunctionResourceType(FunctionResourceType src) {
		switch(src.resourceType()) {
		case "jar": return FunctionResourceTypeEnum.jar;
		case "file": return FunctionResourceTypeEnum.file;
		case "archive": return  FunctionResourceTypeEnum.archive;
		default: 
			throw new IllegalStateException();
		}
	}

	private static URI toJavaURI(String src) {
		try {
			return new URI(src);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Bad URI", ex);
		}
	}

	// --------------------------------------------------------------------------------------------

	protected static <T> scala.Option<T> toScalaOption(T src) {
		return (src != null)? scala.Option.apply(src) : scala.Option.empty();
	}

	private static <A> A toJavaOpt(scala.Option<A> opt) {
		return opt.isDefined()? opt.get() : null;
	}

	// strange Option<Object> in spark instead of Option<Long> ?
	private static Long optObjectToJavaLong(scala.Option<Object> opt) {
		if (!opt.isDefined()) return null;
		Object obj = opt.get();
		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return null;
	}

	private static <A> List<A> toJavaList(scala.collection.Seq<A> ls) {
		return JavaConverters.seqAsJavaList(ls);
	}
	
	private static <A> scala.collection.Seq<A> toScalaSeq(java.util.Collection<A> ls) {
		return JavaConverters.collectionAsScalaIterable(ls).toSeq();
	}
	
	protected static <K,V> ImmutableMap<K,V> toImmutableMapCopy(scala.collection.Map<K,V> src) {
		return ImmutableMap.copyOf(ScalaCollUtils.mapAsJavaMap(src));
	}

	protected static <K,V> scala.collection.immutable.Map<K,V> toScalaImmutableMap(java.util.Map<K,V> src) {
		return ScalaCollUtils.toScalaImutableMap(src);
	}

	protected static <K,V> scala.collection.mutable.Map<K,V> toScalaMutableMap(java.util.Map<K,V> src) {
		return ScalaCollUtils.toScalaMutableMap(src);
	}

	protected static BigInt toScalaBigInt(BigInteger src) {
		return new BigInt(src);
	}

	protected static BigInteger toJavaBigInt(BigInt src) {
		return (src != null)? src.bigInteger() : null;
	}

	protected static BigInteger toJavaBigInt(scala.Option<BigInt> src) {
		return (src != null && src.isDefined())? src.get().bigInteger() : null;
	}

	protected static scala.Option<BigInt> toScalaOptionBigInt(BigInteger src) {
		return (src != null)? scala.Option.apply(toScalaBigInt(src)) : scala.Option.empty();
	}


}

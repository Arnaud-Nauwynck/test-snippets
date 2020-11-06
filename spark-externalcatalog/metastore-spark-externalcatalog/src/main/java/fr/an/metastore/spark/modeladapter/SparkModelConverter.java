package fr.an.metastore.spark.modeladapter;

import static fr.an.metastore.impl.utils.MetastoreListUtils.map;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.CatalogColumnStat;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.CatalogTableType;
import org.apache.spark.sql.catalyst.catalog.CatalogTypes;

import com.google.common.collect.ImmutableMap;

import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStorageFormat;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.spark.util.NotImpl;
import fr.an.metastore.spark.util.ScalaCollUtils;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.math.BigInt;

public class SparkModelConverter {


	public static CatalogDatabase toSparkCatalog(DatabaseModel src) {
		String name = src.getName();
	    String description = src.getDescription();
	    URI locationUri = src.getLocationUri();
		java.util.Map<String, String> props = src.getProperties();
		scala.collection.immutable.Map<String, String> scalaProps = ScalaCollUtils.toScalaImutableMap(props);

		return new CatalogDatabase(name, description, locationUri, scalaProps);
	}

	public ImmutableCatalogDatabaseDef toImmutableCatalogDef(CatalogDatabase src) {
		return ImmutableCatalogDatabaseDef.builder()
				.name(src.name())
				.description(src.description())
				.locationUri(src.locationUri())
				.properties(toImmutableMapCopy(src.properties()))
				.build();
	}

	public CatalogDatabase toSparkCatalogDatabase(ImmutableCatalogDatabaseDef src) {
		return new CatalogDatabase(
			    src.getName(),
			    src.getDescription(),
			    src.getLocationUri(),
			    toScalaImmutableMap(src.getProperties())
				);
	}

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
		CatalogTableType catalogType = toSparkCatalogTableType(src.tableType);
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
	
	protected static CatalogTableType toSparkCatalogTableType(CatalogTableTypeEnum src) {
		switch(src) {
		case EXTERNAL: return CatalogTableType.EXTERNAL();
		case MANAGED: return CatalogTableType.MANAGED();
		case VIEW: return CatalogTableType.VIEW();
		default: throw new IllegalStateException();
		}
	}
	

	public List<ImmutableCatalogTablePartitionDef> toImmutableTablePartitionDefs(scala.collection.Seq<CatalogTablePartition> sparkParts) {
		return map(toJavaList(sparkParts), x -> toImmutableTablePartitionDef(x));
	}

	public ImmutableCatalogTablePartitionDef toImmutableTablePartitionDef(CatalogTablePartition spark) {
		throw NotImpl.notImplEx(); // TODO
//		return ImmutableCatalogTablePartitionDef.builder()
//				.build();
	}

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

	public CatalogTablePartition toSparkTablePartition(CatalogTablePartitionInfo src) {
		ImmutableCatalogTablePartitionDef def = src.def;
		scala.collection.immutable.Map<String, String> spec = toSparkPartitionSpec(def.spec);
		CatalogStorageFormat storage = toSparkStorage(def.storage);
	    scala.collection.immutable.Map<String,String> parameters = toScalaImmutableMap(def.parameters);
	    long createTime = def.createTime;
	    long lastAccessTime = src.lastAccessTime;
	    scala.Option<CatalogStatistics> stats = toSparkOptionCatalogStatistics(src.stats);
		return new CatalogTablePartition(spec, storage, parameters, createTime, lastAccessTime, stats);
	}

	private CatalogStorageFormat toSparkStorage(ImmutableCatalogStorageFormat src) {
		scala.Option<URI> locationUri = toScalaOption(src.locationUri);
		scala.Option<String> inputFormat = toScalaOption(src.inputFormat);
		scala.Option<String> outputFormat = toScalaOption(src.outputFormat);
		scala.Option<String> serde = toScalaOption(src.serde);
	    Boolean compressed = src.compressed;
	    scala.collection.immutable.Map<String, String> properties = toScalaImmutableMap(src.properties);
		return new CatalogStorageFormat(locationUri, inputFormat, outputFormat, serde, 
				compressed, properties);
	}

	public Seq<CatalogTablePartition> toSparkTablePartitions(List<CatalogTablePartitionInfo> src) {
		return toScalaSeq(map(src, x -> toSparkTablePartition(x)));
	}
		
	public CatalogStatistics toSparkCatalogStatistics(ImmutableCatalogStatistics src) {
		scala.math.BigInt sizeInBytes = toScalaBigInt(src.sizeInBytes);
		scala.Option<scala.math.BigInt> rowCount = toScalaOptionBigInt(src.rowCount);
		scala.collection.immutable.Map<String,CatalogColumnStat> colStats = null;
			// TODO
		    // Map[String, CatalogColumnStat] = Map.empty;

		return new CatalogStatistics(sizeInBytes, rowCount, colStats);
	}

	public scala.Option<CatalogStatistics> toSparkOptionCatalogStatistics(ImmutableCatalogStatistics src) {
		return (src != null)? toScalaOption(toSparkCatalogStatistics(src)) : scala.Option.empty();
	}
	
	// --------------------------------------------------------------------------------------------

	protected static <T> scala.Option<T> toScalaOption(T src) {
		return (src != null)? scala.Option.apply(src) : scala.Option.empty();
	}

	private static <A> A toJavaOpt(scala.Option<A> opt) {
		return opt.isDefined()? opt.get() : null;
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

	protected static scala.Option<BigInt> toScalaOptionBigInt(BigInteger src) {
		return (src != null)? scala.Option.apply(toScalaBigInt(src)) : scala.Option.empty();
	}

}

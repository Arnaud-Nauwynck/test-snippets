package fr.an.metastore.spark;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.types.StructType;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.immutable.*;
import fr.an.metastore.impl.utils.MetastoreListUtils;
import static fr.an.metastore.impl.utils.MetastoreListUtils.map;
import fr.an.metastore.api.dto.*;
import fr.an.metastore.api.info.*;

import fr.an.metastore.spark.util.ScalaCollUtils;
import fr.an.metastore.spark.modeladapter.*;

import lombok.val;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;

/**
 * adapter for spark ExternalCatalog, delegate to AbstractJavaExternalCatalog
 * 
 * convert scala Seq to java List
 *
 */
public class JavaAdapterExternalCatalog implements ExternalCatalog {

	private final CatalogFacade delegate;
	private final SparkModelConverter sparkConverter;

	// --------------------------------------------------------------------------
	
	public JavaAdapterExternalCatalog(CatalogFacade delegate, SparkModelConverter sparkConverter) {
		this.delegate = delegate;
		this.sparkConverter = sparkConverter;
	}

	// --------------------------------------------------------------------------

	@Override
	public void setCurrentDatabase(String db) {
		this.delegate.setCurrentDatabase(db);
	}

	@Override
	public void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists) {
		String db = dbDefinition.name();
		ImmutableCatalogDatabaseDef def = sparkConverter.toImmutableCatalogDef(dbDefinition);
		delegate.createDatabase(db, def, ignoreIfExists);
	}

	@Override
	public void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade) {
		delegate.dropDatabase(db, ignoreIfNotExists, cascade);
	}

	@Override
	public void alterDatabase(CatalogDatabase dbDefinition) {
		String db = dbDefinition.name();
		ImmutableCatalogDatabaseDef def = sparkConverter.toImmutableCatalogDef(dbDefinition);
		delegate.alterDatabase(db, def);
	}

	@Override
	public CatalogDatabase getDatabase(String db) {
		ImmutableCatalogDatabaseDef def = delegate.getDatabase(db);
		return sparkConverter.toSparkCatalogDatabase(def);
	}

	@Override
	public boolean databaseExists(String db) {
		return delegate.databaseExists(db);
	}

	@Override
	public Seq<String> listDatabases() {
		List<String> tmpres = delegate.listDatabases();
		return toScalaSeq(tmpres);
	}

	@Override
	public Seq<String> listDatabases(String pattern) {
		List<String> tmpres = delegate.listDatabases(pattern);
		return toScalaSeq(tmpres);
	}

	@Override
	public void createTable(CatalogTable tableDef, boolean ignoreIfExists) {
		ImmutableCatalogTableDef def = sparkConverter.toImmutableTableDef(tableDef);
		delegate.createTable(def, ignoreIfExists);
	}

	@Override
	public void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge) {
		delegate.dropTable(db, table, ignoreIfNotExists, purge);
	}

	@Override
	public void renameTable(String db, String oldName, String newName) {
		delegate.renameTable(db, oldName, newName);
	}

	@Override
	public void alterTable(CatalogTable tableDefinition) {
		ImmutableCatalogTableDef def = sparkConverter.toImmutableTableDef(tableDefinition);
		delegate.alterTable(def);
	}

	@Override
	public void alterTableDataSchema(String db, String table, StructType newDataSchema) {
		// TODO delegate.alterTableDataSchema(db, table, newDataSchema);
	}

	@Override
	public void alterTableStats(String db, String table, scala.Option<CatalogStatistics> stats) {
		CatalogStatistics jstats = toJavaOpt(stats);
		// TODO delegate.alterTableStats(db, table, jstats);
	}

	@Override
	public CatalogTable getTable(String db, String table) {
		ImmutableCatalogTableDef tmpres = delegate.getTableDef(db, table);
		return sparkConverter.toSparkTable(tmpres);
	}

	@Override
	public Seq<CatalogTable> getTablesByName(String db, Seq<String> tables) {
		val jtables = toJavaList(tables);
		val tableDefs = delegate.getTableDefsByName(db, jtables);
		val sparkTables = MetastoreListUtils.map(tableDefs, t -> sparkConverter.toSparkTable(t));
		return toScalaSeq(sparkTables);
	}

	@Override
	public boolean tableExists(String db, String table) {
		return delegate.tableExists(db, table);
	}

	@Override
	public Seq<String> listTables(String db) {
		val tmpres = delegate.listTables(db);
		return toScalaSeq(tmpres);
	}

	@Override
	public Seq<String> listTables(String db, String pattern) {
		val tmpres= delegate.listTables(db, pattern);
		return toScalaSeq(tmpres);
	}

	@Override
	public void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		delegate.loadTable(db, table, loadPath, isOverwrite, isSrcLocal);
	}

	@Override
	public void createPartitions(String db, String table, Seq<CatalogTablePartition> parts, boolean ignoreIfExists) {
		List<ImmutableCatalogTablePartitionDef> immutablePartitionDefs = sparkConverter.toImmutableTablePartitionDefs(parts);
		delegate.createPartitions(db, table, immutablePartitionDefs, ignoreIfExists);
	}

	@Override
	public void dropPartitions(String db, String table, 
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData) {
		List<ImmutablePartitionSpec> jPartSpecs = sparkConverter.toImmutablePartitionSpecs(partSpecs);
		delegate.dropPartitions(db, table, jPartSpecs, ignoreIfNotExists, purge, retainData);
	}

	@Override
	public void renamePartitions(String db, String table, 
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> specs,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> newSpecs) {
		val jspecs = sparkConverter.toImmutablePartitionSpecs(specs);
		val jnewSpecs = sparkConverter.toImmutablePartitionSpecs(newSpecs);
		delegate.renamePartitions(db, table, jspecs, jnewSpecs);
	}

	@Override
	public void alterPartitions(String db, String table, Seq<CatalogTablePartition> parts) {
		List<ImmutableCatalogTablePartitionDef> jparts = sparkConverter.toImmutableTablePartitionDefs(parts);
		delegate.alterPartitions(db, table, jparts);
	}

	@Override
	public CatalogTablePartition getPartition(String db, String table, 
			scala.collection.immutable.Map<String, String> spec) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		CatalogTablePartitionInfo part = delegate.getPartition(db, table, jspec);
		
	}

	@Override
	public Option<CatalogTablePartition> getPartitionOption(String db, String table, 
			scala.collection.immutable.Map<String, String> spec) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		delegate.getPartitionOption(db, table, jspec);
	}

	@Override
	public Seq<String> listPartitionNames(String db, String table, 
			scala.Option<scala.collection.immutable.Map<String, String>> partialSpec) {
		ImmutablePartitionSpec jpartialSpec = partialSpec.isDefined()? sparkConverter.toImmutablePartitionSpec(partialSpec.get()) : null;
		List<String> tmpres = delegate.listPartitionNamesByPartialSpec(db, table, jpartialSpec);
		return toScalaSeq(tmpres);
	}

	@Override
	public Seq<CatalogTablePartition> listPartitions(String db, String table, scala.Option<scala.collection.immutable.Map<String, String>> partialSpec) {
		ImmutablePartitionSpec jpartialSpec = partialSpec.isDefined()? sparkConverter.toImmutablePartitionSpec(partialSpec.get()) : null;
		List<CatalogTablePartitionInfo> jParts = delegate.listPartitionsByPartialSpec(db, table, jpartialSpec);
		return toScalaSeq(map(jParts, x -> sparkConverter.toSparkTablePartition(src)));
	}

	@Override
	public Seq<CatalogTablePartition> listPartitionsByFilter(String db, String table, Seq<Expression> predicates,
			String defaultTimeZoneId) {
		val jpredicates = toJavaList(predicates);
		val tmpres = delegate.listPartitionsByFilter(db, table, jpredicates, defaultTimeZoneId);
		return toScalaSeq(tmpres);
	}

	@Override
	public void loadPartition(String db, String table, String loadPath, 
			scala.collection.immutable.Map<String, String> spec, boolean isOverwrite,
			boolean inheritTableSpecs, boolean isSrcLocal) {
		val jspec = toJavaPartSpec(spec);
		delegate.loadPartition(db, table, loadPath, jspec, isOverwrite, inheritTableSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(String db, String table, String loadPath, scala.collection.immutable.Map<String, String> spec,
			boolean replace, int numDP) {
		val jspec = toJavaPartSpec(spec);
		delegate.loadDynamicPartitions(db, table, loadPath, jspec, replace, numDP);
	}

	@Override
	public void createFunction(String db, CatalogFunction func) {
		delegate.createFunction(db, func);
	}

	@Override
	public void dropFunction(String db, String funcName) {
		delegate.dropFunction(db, funcName);
	}

	@Override
	public void alterFunction(String db, CatalogFunction func) {
		delegate.alterFunction(db, func);
	}

	@Override
	public void renameFunction(String db, String oldName, String newName) {
		delegate.renameFunction(db, oldName, newName);
	}

	@Override
	public CatalogFunction getFunction(String db, String funcName) {
		return delegate.getFunction(db, funcName);
	}

	@Override
	public boolean functionExists(String db, String funcName) {
		return delegate.functionExists(db, funcName);
	}

	@Override
	public Seq<String> listFunctions(String db, String pattern) {
		List<String> tmpres = delegate.listFunctions(db, pattern);
		return toScalaSeq(tmpres);
	}

	// converter helper scala.collection <-> java.collection
	// --------------------------------------------------------------------------------------------

	private static <A> scala.collection.Seq<A> toScalaSeq(
			java.util.Collection<A> ls) {
		return JavaConverters.collectionAsScalaIterable(ls).toSeq();
	}

	private static <A> List<A> toJavaList(
			scala.collection.Seq<A> ls) {
		return JavaConverters.seqAsJavaList(ls);
	}
	
	private static <A> A toJavaOpt(scala.Option<A> opt) {
		return opt.isDefined()? opt.get() : null;
	}

	private static <TSrc,TDest> List<TDest> seqMapToList(scala.collection.Seq<TSrc> src, Function<TSrc,TDest> mapper) {
		return map(toJavaList(src), mapper);
	}
	

	private static List<PartitionSpec> toJavaListPartSpecs(scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs) {
		return ScalaCollUtils.map(partSpecs, partSpec -> toJavaPartSpec(partSpec));
	}

	private static PartitionSpec toJavaPartSpec(scala.collection.immutable.Map<String, String> partSpecs) {
		Map<String, String> jpartSpecs = JavaConverters.mapAsJavaMapConverter(partSpecs).asJava();
		return new PartitionSpec(jpartSpecs);
	}


}

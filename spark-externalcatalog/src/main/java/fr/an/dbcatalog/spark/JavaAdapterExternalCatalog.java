package fr.an.dbcatalog.spark;

import java.util.List;
import java.util.Map;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.types.StructType;

import fr.an.dbcatalog.api.AbstractJavaDbCatalog;
import fr.an.dbcatalog.api.PartitionSpec;
import fr.an.dbcatalog.spark.util.ScalaCollUtils;
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

	private final AbstractJavaDbCatalog delegate;

	// --------------------------------------------------------------------------
	
	public JavaAdapterExternalCatalog(AbstractJavaDbCatalog delegate) {
		this.delegate = delegate;
	}

	// --------------------------------------------------------------------------

	@Override
	public void setCurrentDatabase(String db) {
		this.delegate.setCurrentDatabase(db);
	}

	@Override
	public void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists) {
		delegate.createDatabase(dbDefinition, ignoreIfExists);
	}

	@Override
	public void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade) {
		delegate.dropDatabase(db, ignoreIfNotExists, cascade);
	}

	@Override
	public void alterDatabase(CatalogDatabase dbDefinition) {
		delegate.alterDatabase(dbDefinition);
	}

	@Override
	public CatalogDatabase getDatabase(String db) {
		return delegate.getDatabase(db);
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
	public void createTable(CatalogTable tableDefinition, boolean ignoreIfExists) {
		delegate.createTable(tableDefinition, ignoreIfExists);
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
		delegate.alterTable(tableDefinition);
	}

	@Override
	public void alterTableDataSchema(String db, String table, StructType newDataSchema) {
		delegate.alterTableDataSchema(db, table, newDataSchema);
	}

	@Override
	public void alterTableStats(String db, String table, scala.Option<CatalogStatistics> stats) {
		CatalogStatistics jstats = toJavaOpt(stats);
		delegate.alterTableStats(db, table, jstats);
	}

	@Override
	public CatalogTable getTable(String db, String table) {
		return delegate.getTable(db, table);
	}

	@Override
	public Seq<CatalogTable> getTablesByName(String db, Seq<String> tables) {
		val jtables = toJavaList(tables);
		val tmpres = delegate.getTablesByName(db, jtables);
		return toScalaSeq(tmpres);
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
		val scalaParts = toJavaList(parts);
		delegate.createPartitions(db, table, scalaParts, ignoreIfExists);
	}

	@Override
	public void dropPartitions(String db, String table, 
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData) {
		val jPartSpecs = toJavaListPartSpecs(partSpecs);
		delegate.dropPartitions(db, table, jPartSpecs, ignoreIfNotExists, purge, retainData);
	}

	@Override
	public void renamePartitions(String db, String table, 
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> specs,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> newSpecs) {
		val jspecs = toJavaListPartSpecs(specs);
		val jnewSpecs = toJavaListPartSpecs(newSpecs);
		delegate.renamePartitions(db, table, jspecs, jnewSpecs);
	}

	@Override
	public void alterPartitions(String db, String table, Seq<CatalogTablePartition> parts) {
		val jparts = toJavaList(parts);
		delegate.alterPartitions(db, table, jparts);
	}

	@Override
	public CatalogTablePartition getPartition(String db, String table, scala.collection.immutable.Map<String, String> spec) {
		val jspec = toJavaPartSpec(spec);
		return delegate.getPartition(db, table, jspec);
	}

	@Override
	public Option<CatalogTablePartition> getPartitionOption(String db, String table, scala.collection.immutable.Map<String, String> spec) {
		val jspec = toJavaPartSpec(spec);
		return delegate.getPartitionOption(db, table, jspec);
	}

	@Override
	public Seq<String> listPartitionNames(String db, String table, scala.Option<scala.collection.immutable.Map<String, String>> spec) {
		val jspec = spec.isDefined()? toJavaPartSpec(spec.get()) : null;
		val tmpres = delegate.listPartitionNames(db, table, jspec);
		return toScalaSeq(tmpres);
	}

	@Override
	public Seq<CatalogTablePartition> listPartitions(String db, String table, scala.Option<scala.collection.immutable.Map<String, String>> spec) {
		val jspec = spec.isDefined()? toJavaPartSpec(spec.get()) : null;
		val tmpres = delegate.listPartitions(db, table, jspec);
		return toScalaSeq(tmpres);
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

	private static List<PartitionSpec> toJavaListPartSpecs(scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs) {
		return ScalaCollUtils.map(partSpecs, partSpec -> toJavaPartSpec(partSpec));
	}

	private static PartitionSpec toJavaPartSpec(scala.collection.immutable.Map<String, String> partSpecs) {
		Map<String, String> jpartSpecs = JavaConverters.mapAsJavaMapConverter(partSpecs).asJava();
		return new PartitionSpec(jpartSpecs);
	}

}

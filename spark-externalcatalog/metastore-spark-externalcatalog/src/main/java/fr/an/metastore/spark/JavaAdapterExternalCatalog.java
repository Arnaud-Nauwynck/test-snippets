package fr.an.metastore.spark;

import static fr.an.metastore.api.utils.MetastoreListUtils.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.spark.sql.catalyst.analysis.NamespaceAlreadyExistsException;
import org.apache.spark.sql.catalyst.analysis.NoSuchNamespaceException;
import org.apache.spark.sql.catalyst.analysis.NoSuchTableException;
import org.apache.spark.sql.catalyst.analysis.TableAlreadyExistsException;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.NamespaceChange;
import org.apache.spark.sql.connector.catalog.SupportsNamespaces;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableCatalog;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.api.utils.NotImpl;
import fr.an.metastore.spark.impl.SparkV2CatalogTable;
import fr.an.metastore.spark.modeladapter.SparkModelConverter;
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
public class JavaAdapterExternalCatalog implements ExternalCatalog, TableCatalog, SupportsNamespaces {

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
		ImmutableCatalogDatabaseDef def = sparkConverter.toImmutableDatabaseDef(dbDefinition);
		delegate.createDatabase(db, def, ignoreIfExists);
	}

	@Override
	public void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade) {
		delegate.dropDatabase(db, ignoreIfNotExists, cascade);
	}

	@Override
	public void alterDatabase(CatalogDatabase dbDefinition) {
		String db = dbDefinition.name();
		ImmutableCatalogDatabaseDef def = sparkConverter.toImmutableDatabaseDef(dbDefinition);
		delegate.alterDatabase(db, def);
	}

	@Override
	public CatalogDatabase getDatabase(String db) {
		ImmutableCatalogDatabaseDef def = delegate.getDatabase(db);
		return sparkConverter.toSparkDatabase(def);
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

	// Tables
	// --------------------------------------------------------------------------------------------

	@Override
	public void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge) {
		delegate.dropTable(toTableId(db, table), ignoreIfNotExists, purge);
	}

	@Override
	public void renameTable(String db, String oldName, String newName) {
		val oldTableId = toTableId(db, oldName);
		delegate.renameTable(oldTableId, newName);
	}

	@Override
	public void alterTable(CatalogTable tableDefinition) {
		ImmutableCatalogTableDef def = sparkConverter.toImmutableTableDef(tableDefinition);
		delegate.alterTable(def);
	}

	@Override
	public void alterTableDataSchema(String db, String table, StructType newDataSchema) {
		// TODO delegate.alterTableDataSchema(db, table, newDataSchema);
		throw NotImpl.notImplEx();
	}

	@Override
	public void alterTableStats(String db, String table, scala.Option<CatalogStatistics> stats) {
		ImmutableCatalogTableStatistics jstats = stats.isDefined()
				? sparkConverter.toImmutableTableStatistics(stats.get())
				: null;
		delegate.alterTableStats(toTableId(db, table), jstats);
	}

	@Override
	public CatalogTable getTable(String db, String table) {
		val tableId = toTableId(db, table);
		ImmutableCatalogTableDef tmpres = delegate.getTableDef(tableId);
		return sparkConverter.toSparkTable(tmpres);
	}

	private CatalogTableId toTableId(String db, String table) {
		return new CatalogTableId(db, table);
	}

	@Override
	public Seq<CatalogTable> getTablesByName(String db, Seq<String> tables) {
		val jtables = toJavaList(tables);
		val tableDefs = delegate.getTableDefsByName(db, jtables);
		return sparkConverter.toSparkTables(tableDefs);
	}

	@Override
	public boolean tableExists(String db, String table) {
		val tableId = toTableId(db, table);
		return delegate.tableExists(tableId);
	}

	@Override
	public Seq<String> listTables(String db) {
		val tmpres = delegate.listTableNames(db);
		return toScalaSeq(tmpres);
	}

	@Override
	public Seq<String> listTables(String db, String pattern) {
		val tmpres = delegate.listTableNamesByPattern(db, pattern);
		return toScalaSeq(tmpres);
	}

	@Override
	public void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		val tableId = toTableId(db, table);
		delegate.loadTable(tableId, loadPath, isOverwrite, isSrcLocal);
	}

	// Table Partitions
	// --------------------------------------------------------------------------------------------

	@Override
	public void createPartitions(String db, String table, Seq<CatalogTablePartition> parts, boolean ignoreIfExists) {
		List<ImmutableCatalogTablePartitionDef> immutablePartitionDefs = sparkConverter
				.toImmutableTablePartitionDefs(parts);
		val tableId = toTableId(db, table);
		delegate.createPartitions(tableId, immutablePartitionDefs, ignoreIfExists);
	}

	@Override
	public void dropPartitions(String db, String table,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData) {
		List<ImmutablePartitionSpec> jPartSpecs = sparkConverter.toImmutablePartitionSpecs(partSpecs);
		val tableId = toTableId(db, table);
		delegate.dropPartitions(tableId, jPartSpecs, ignoreIfNotExists, purge, retainData);
	}

	@Override
	public void renamePartitions(String db, String table,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> specs,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> newSpecs) {
		val jspecs = sparkConverter.toImmutablePartitionSpecs(specs);
		val jnewSpecs = sparkConverter.toImmutablePartitionSpecs(newSpecs);
		val tableId = toTableId(db, table);
		delegate.renamePartitions(tableId, jspecs, jnewSpecs);
	}

	@Override
	public void alterPartitions(String db, String table, Seq<CatalogTablePartition> parts) {
		List<ImmutableCatalogTablePartitionDef> jparts = sparkConverter.toImmutableTablePartitionDefs(parts);
		val tableId = toTableId(db, table);
		delegate.alterPartitions(tableId, jparts);
	}

	@Override
	public CatalogTablePartition getPartition(String db, String table,
			scala.collection.immutable.Map<String, String> spec) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		val tableId = toTableId(db, table);
		CatalogTablePartitionInfo part = delegate.getPartition(tableId, jspec);
		return sparkConverter.toSparkTablePartition(part);
	}

	@Override
	public Option<CatalogTablePartition> getPartitionOption(String db, String table,
			scala.collection.immutable.Map<String, String> spec) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		val tableId = toTableId(db, table);
		Optional<CatalogTablePartitionInfo> partOpt = delegate.getPartitionOption(tableId, jspec);
		return sparkConverter.toSparkTablePartitionOpt(partOpt);
	}

	@Override
	public Seq<String> listPartitionNames(String db, String table,
			scala.Option<scala.collection.immutable.Map<String, String>> partialSpec) {
		ImmutablePartitionSpec jpartialSpec = (partialSpec.isDefined())?
				sparkConverter.toImmutablePartitionSpec(partialSpec.get()) : null;
		val tableId = toTableId(db, table);
		List<String> partNames = delegate.listPartitionNamesByPartialSpec(tableId, jpartialSpec);
		return toScalaSeq(partNames);
	}

	@Override
	public Seq<CatalogTablePartition> listPartitions(String db, String table,
			scala.Option<scala.collection.immutable.Map<String, String>> partialSpec) {
		ImmutablePartitionSpec jpartialSpec = (partialSpec.isDefined())?
				sparkConverter.toImmutablePartitionSpec(partialSpec.get()) : null;
		val tableId = toTableId(db, table);
		List<CatalogTablePartitionInfo> jParts = delegate.listPartitionsByPartialSpec(tableId, jpartialSpec);
		return sparkConverter.toSparkTablePartitions(jParts);
	}

	@Override
	public Seq<CatalogTablePartition> listPartitionsByFilter(String db, String table, Seq<Expression> predicates,
			String defaultTimeZoneId) {
//		val jpredicates = toJavaList(predicates);
//		val tmpres = delegate.listPartitionsByFilter(db, table, jpredicates, defaultTimeZoneId);
//		return toScalaSeq(tmpres);
		throw NotImpl.notImplEx();
	}

	@Override
	public void loadPartition(String db, String table, String loadPath,
			scala.collection.immutable.Map<String, String> spec, boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		val tableId = toTableId(db, table);
		delegate.loadPartition(tableId, loadPath, jspec, isOverwrite, inheritTableSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(String db, String table, String loadPath,
			scala.collection.immutable.Map<String, String> spec, boolean replace, int numDP) {
		val jspec = sparkConverter.toImmutablePartitionSpec(spec);
		val tableId = toTableId(db, table);
		delegate.loadDynamicPartitions(tableId, loadPath, jspec, replace, numDP);
	}

	// Functions
	// --------------------------------------------------------------------------------------------

	@Override
	public void createFunction(String db, CatalogFunction func) {
		val def = sparkConverter.toImmutableFunctionDef(func);
		delegate.createFunction(def);
	}

	@Override
	public void dropFunction(String db, String funcName) {
		val id = new CatalogFunctionId(db, funcName);
		delegate.dropFunction(id);
	}

	@Override
	public void alterFunction(String db, CatalogFunction func) {
		ImmutableCatalogFunctionDef def = sparkConverter.toImmutableFunctionDef(func);
		if (!def.identifier.database.equals(db)) {
			throw new IllegalStateException();
		}
		delegate.alterFunction(def);
	}

	@Override
	public void renameFunction(String db, String oldName, String newName) {
		delegate.renameFunction(db, oldName, newName);
	}

	@Override
	public CatalogFunction getFunction(String db, String funcName) {
		val id = new CatalogFunctionId(db, funcName);
		ImmutableCatalogFunctionDef func = delegate.getFunction(id);
		return sparkConverter.toSparkFunction(func);
	}

	@Override
	public boolean functionExists(String db, String funcName) {
		val id = new CatalogFunctionId(db, funcName);
		return delegate.functionExists(id);
	}

	@Override
	public Seq<String> listFunctions(String db, String pattern) {
		List<String> tmpres = delegate.listFunctions(db, pattern);
		return toScalaSeq(tmpres);
	}



	// implements SupportsNamespaces
	// --------------------------------------------------------------------------------------------
	
	// cf org.apache.spark.sql.connector.catalog.CatalogV2Implicits
	//
	//   def asNamespaceCatalog: SupportsNamespaces = plugin match {
	//   case namespaceCatalog: SupportsNamespaces =>
	//     namespaceCatalog
	//   case _ =>
	//     throw new AnalysisException(
	//       s"Cannot use catalog ${plugin.name}: does not support namespaces")
	// }

	@Override
	public String[][] listNamespaces() throws NoSuchNamespaceException {
		List<String> dbs = delegate.listDatabases();
		List<String[]> namespaces = map(dbs, db -> new String[] { db });
		return namespaces.toArray(new String[dbs.size()][]);
	}

	@Override
	public String[][] listNamespaces(String[] namespace) throws NoSuchNamespaceException {
		throw NotImpl.notImplEx();
	}

	@Override
	public Map<String, String> loadNamespaceMetadata(String[] namespace) throws NoSuchNamespaceException {
		throw NotImpl.notImplEx();
	}

	@Override
	public void createNamespace(String[] namespace, Map<String, String> metadata)
			throws NamespaceAlreadyExistsException {
		throw NotImpl.notImplEx();
	}

	@Override
	public void alterNamespace(String[] namespace, NamespaceChange... changes) throws NoSuchNamespaceException {
		throw NotImpl.notImplEx();
		
	}

	@Override
	public boolean dropNamespace(String[] namespace) throws NoSuchNamespaceException {
		throw NotImpl.notImplEx();
	}

	// implements TableCatalog (V2..)
	// --------------------------------------------------------------------------------------------

	@Override
	public void initialize(String name, CaseInsensitiveStringMap options) {
		// do nothing, cf override
	}

	@Override
	public String name() {
		return "spark_catalog";
	}

	@Override
	public Identifier[] listTables(String[] namespace) throws NoSuchNamespaceException {
		List<Identifier> res = new ArrayList<>();
		for(String db: namespace) {
			val tmpres = delegate.listTableNames(db);
			String[] dbNamespaces = new String[] { db };
			res.addAll(map(tmpres, n -> Identifier.of(dbNamespaces, n)));
		}
		return res.toArray(new Identifier[res.size()]);
	}

	@Override
	public Table loadTable(Identifier ident) throws NoSuchTableException {
		val tableId = toTableId(ident);
		ImmutableCatalogTableDef tableDef = delegate.getTableDef(tableId);
		return new SparkV2CatalogTable(tableDef);
	}

	protected CatalogTableId toTableId(Identifier ident) {
		String[] namespace = ident.namespace();
		String db = (namespace != null && namespace.length >= 1)? namespace[0] : null; // TOCHECK
		String table = ident.name();
		return new CatalogTableId(db, table);
	}
	
	@Override
	public Table createTable(Identifier ident, StructType schema, Transform[] partitions,
			Map<String, String> properties) throws TableAlreadyExistsException, NoSuchNamespaceException {
		val tableId = toTableId(ident);
		throw NotImpl.notImplEx();
	}

	@Override
	public Table alterTable(Identifier ident, TableChange... changes) throws NoSuchTableException {
		val tableId = toTableId(ident);
		throw NotImpl.notImplEx();
	}

	/**
	 * @return true if a table was deleted, false if no table exists for the identifier
	 */
	@Override
	public boolean dropTable(Identifier ident) {
		val tableId = toTableId(ident);
		boolean tableExists = delegate.tableExists(tableId);
		if (tableExists) {
			boolean purge = false; // ??
			delegate.dropTable(tableId, true, purge);
		}
		return tableExists;
	}

	@Override
	public void renameTable(Identifier oldIdent, Identifier newIdent)
			throws NoSuchTableException, TableAlreadyExistsException {
		val oldTableId = toTableId(oldIdent);
		val newTableId = toTableId(newIdent);
		delegate.renameTable(oldTableId, newTableId.table);
	}

	// new in spark 3.0.1
	@Override
	public Seq<String> listViews(String db, String pattern) {
		throw NotImpl.notImplEx();
	}

	
	
	// converter helper scala.collection <-> java.collection
	// --------------------------------------------------------------------------------------------

	private static <A> scala.collection.Seq<A> toScalaSeq(java.util.Collection<A> ls) {
		return JavaConverters.collectionAsScalaIterable(ls).toSeq();
	}

	private static <A> List<A> toJavaList(scala.collection.Seq<A> ls) {
		return JavaConverters.seqAsJavaList(ls);
	}

}

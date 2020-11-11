
$ grep -R --include '*.scala' SupportsRead | grep -v /test/ | grep -v target/ | grep -v stream

catalyst/src/main/scala/org/apache/spark/sql/execution/datasources/v2:
	DataSourceV2Implicits.scala:import org.apache.spark.sql.connector.catalog.{SupportsDelete, SupportsRead, SupportsWrite, Table, TableCapability}
	DataSourceV2Implicits.scala:    def asReadable: SupportsRead = {
	DataSourceV2Implicits.scala:        case support: SupportsRead =>

core/src/main/scala/org/apache/spark/sql:
	DataFrameReader.scala:import org.apache.spark.sql.connector.catalog.{CatalogV2Util, SupportsCatalogOptions, SupportsRead}
	DataFrameReader.scala:        case _: SupportsRead if table.supports(BATCH_READ) =>
	execution/datasources/DataSourceStrategy.scala:import org.apache.spark.sql.connector.catalog.SupportsRead
	execution/datasources/DataSourceStrategy.scala:      if (table.isInstanceOf[SupportsRead]
	execution/datasources/v2/FileTable.scala:import org.apache.spark.sql.connector.catalog.{SupportsRead, SupportsWrite, Table, TableCapability}
	execution/datasources/v2/FileTable.scala:  extends Table with SupportsRead with SupportsWrite {
	execution/datasources/v2/jdbc/JDBCTable.scala:  extends Table with SupportsRead with SupportsWrite {

	
	
	
DataFrameReader.scala :
	
	def load(paths: String*): DataFrame = {
	    if (source.toLowerCase(Locale.ROOT) == DDLUtils.HIVE_PROVIDER) {
	      throw new AnalysisException("Hive data source can only be used with tables, you can not " +
	        "read files of Hive data source directly.")
	    }
	
	    val legacyPathOptionBehavior = sparkSession.sessionState.conf.legacyPathOptionBehavior
	    if (!legacyPathOptionBehavior &&
	        (extraOptions.contains("path") || extraOptions.contains("paths")) && paths.nonEmpty) {
	      throw new AnalysisException("There is a 'path' or 'paths' option set and load() is called " +
	        "with path parameters. Either remove the path option if it's the same as the path " +
	        "parameter, or add it to the load() parameter if you do want to read multiple paths. " +
	        s"To ignore this check, set '${SQLConf.LEGACY_PATH_OPTION_BEHAVIOR.key}' to 'true'.")
	    }
	
	    DataSource.lookupDataSourceV2(source, sparkSession.sessionState.conf).map { provider =>
	      val catalogManager = sparkSession.sessionState.catalogManager
	      val sessionOptions = DataSourceV2Utils.extractSessionConfigs(
	        source = provider, conf = sparkSession.sessionState.conf)
	
	      val optionsWithPath = if (paths.isEmpty) {
	        extraOptions
	      } else if (paths.length == 1) {
	        extraOptions + ("path" -> paths.head)
	      } else {
	        val objectMapper = new ObjectMapper()
	        extraOptions + ("paths" -> objectMapper.writeValueAsString(paths.toArray))
	      }
	
	      val finalOptions = sessionOptions.filterKeys(!optionsWithPath.contains(_)).toMap ++
	        optionsWithPath.originalMap
	      val dsOptions = new CaseInsensitiveStringMap(finalOptions.asJava)
	      val (table, catalog, ident) = provider match {
	        case _: SupportsCatalogOptions if userSpecifiedSchema.nonEmpty =>
	          throw new IllegalArgumentException(
	            s"$source does not support user specified schema. Please don't specify the schema.")
	        case hasCatalog: SupportsCatalogOptions =>
	          val ident = hasCatalog.extractIdentifier(dsOptions)
	          val catalog = CatalogV2Util.getTableProviderCatalog(
	            hasCatalog,
	            catalogManager,
	            dsOptions)
	          (catalog.loadTable(ident), Some(catalog), Some(ident))
	        case _ =>
	          // TODO: Non-catalog paths for DSV2 are currently not well defined.
	          val tbl = DataSourceV2Utils.getTableFromProvider(provider, dsOptions, userSpecifiedSchema)
	          (tbl, None, None)
	      }
	      import org.apache.spark.sql.execution.datasources.v2.DataSourceV2Implicits._
	      table match {
	        case _: SupportsRead if table.supports(BATCH_READ) =>
	          Dataset.ofRows(
	            sparkSession,
	            DataSourceV2Relation.create(table, catalog, ident, dsOptions))
	
	        case _ => loadV1Source(paths: _*)
	      }
	    }.getOrElse(loadV1Source(paths: _*))
	  }
	
	  private def loadV1Source(paths: String*) = {
	    val legacyPathOptionBehavior = sparkSession.sessionState.conf.legacyPathOptionBehavior
	    val (finalPaths, finalOptions) = if (!legacyPathOptionBehavior && paths.length == 1) {
	      (Nil, extraOptions + ("path" -> paths.head))
	    } else {
	      (paths, extraOptions)
	    }
	
	    // Code path for data source v1.
	    sparkSession.baseRelationToDataFrame(
	      DataSource.apply(
	        sparkSession,
	        paths = finalPaths,
	        userSpecifiedSchema = userSpecifiedSchema,
	        className = source,
	        options = finalOptions.originalMap).resolveRelation())
	  }





$ grep -R --include '*.scala' ScanBuilder | grep -v /test/ | grep -v target/ | grep -v stream

catalyst/src/main/scala/org/apache/spark/sql/execution/datasources/v2:
	DataSourceV2Relation.scala:import org.apache.spark.sql.connector.read.{Scan, ScanBuilder, Statistics => V2Statistics, SupportsReportStatistics}
	DataSourceV2Relation.scala: * @param options The options for this table operation. It's used to create fresh [[ScanBuilder]]
	DataSourceV2Relation.scala:      table.asReadable.newScanBuilder(options) match {

core/src/main/scala/org/apache/spark/sql/execution/datasources/v2:
	csv/CSVScanBuilder.scala:import org.apache.spark.sql.execution.datasources.v2.FileScanBuilder
	csv/CSVScanBuilder.scala:case class CSVScanBuilder(
	csv/CSVScanBuilder.scala:  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) with SupportsPushDownFilters {
	csv/CSVTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): CSVScanBuilder =
	csv/CSVTable.scala:    CSVScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	FileScanBuilder.scala:import org.apache.spark.sql.connector.read.{ScanBuilder, SupportsPushDownRequiredColumns}
	FileScanBuilder.scala:abstract class FileScanBuilder(
	FileScanBuilder.scala:    dataSchema: StructType) extends ScanBuilder with SupportsPushDownRequiredColumns {
	jdbc/JDBCScanBuilder.scala:import org.apache.spark.sql.connector.read.{Scan, ScanBuilder, SupportsPushDownFilters, SupportsPushDownRequiredColumns}
	jdbc/JDBCScanBuilder.scala:case class JDBCScanBuilder(
	jdbc/JDBCScanBuilder.scala:  extends ScanBuilder with SupportsPushDownFilters with SupportsPushDownRequiredColumns {
	jdbc/JDBCTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): JDBCScanBuilder = {
	jdbc/JDBCTable.scala:    JDBCScanBuilder(SparkSession.active, schema, mergedOptions)
	json/JsonScanBuilder.scala:import org.apache.spark.sql.execution.datasources.v2.FileScanBuilder
	json/JsonScanBuilder.scala:class JsonScanBuilder (
	json/JsonScanBuilder.scala:  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) with SupportsPushDownFilters {
	json/JsonTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): JsonScanBuilder =
	json/JsonTable.scala:    new JsonScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	orc/OrcScanBuilder.scala:import org.apache.spark.sql.execution.datasources.v2.FileScanBuilder
	orc/OrcScanBuilder.scala:case class OrcScanBuilder(
	orc/OrcScanBuilder.scala:  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) with SupportsPushDownFilters {
	orc/OrcTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): OrcScanBuilder =
	orc/OrcTable.scala:    new OrcScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	parquet/ParquetScanBuilder.scala:import org.apache.spark.sql.execution.datasources.v2.FileScanBuilder
	parquet/ParquetScanBuilder.scala:case class ParquetScanBuilder(
	parquet/ParquetScanBuilder.scala:  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) with SupportsPushDownFilters {
	parquet/ParquetTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): ParquetScanBuilder =
	parquet/ParquetTable.scala:    new ParquetScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	PushDownUtils.scala:import org.apache.spark.sql.connector.read.{Scan, ScanBuilder, SupportsPushDownFilters, SupportsPushDownRequiredColumns}
	PushDownUtils.scala:      scanBuilder: ScanBuilder,
	PushDownUtils.scala:      scanBuilder: ScanBuilder,
	text/TextScanBuilder.scala:import org.apache.spark.sql.execution.datasources.v2.FileScanBuilder
	text/TextScanBuilder.scala:case class TextScanBuilder(
	text/TextScanBuilder.scala:  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) {
	text/TextTable.scala:  override def newScanBuilder(options: CaseInsensitiveStringMap): TextScanBuilder =
	text/TextTable.scala:    TextScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	V2ScanRelationPushDown.scala:      val scanBuilder = relation.table.asReadable.newScanBuilder(relation.options)	



parquet/ParquetScanBuilder.scala:

	case class ParquetScanBuilder(
	    sparkSession: SparkSession,
	    fileIndex: PartitioningAwareFileIndex,
	    schema: StructType,
	    dataSchema: StructType,
	    options: CaseInsensitiveStringMap)
	  extends FileScanBuilder(sparkSession, fileIndex, dataSchema) with SupportsPushDownFilters {

parquet/ParquetTable.scala:
	  
	case class ParquetTable(
	    name: String,
	    sparkSession: SparkSession,
	    options: CaseInsensitiveStringMap,
	    paths: Seq[String],
	    userSpecifiedSchema: Option[StructType],
	    fallbackFileFormat: Class[_ <: FileFormat])
	  extends FileTable(sparkSession, options, paths, userSpecifiedSchema) {
	
	  override def newScanBuilder(options: CaseInsensitiveStringMap): ParquetScanBuilder =
	    new ParquetScanBuilder(sparkSession, fileIndex, schema, dataSchema, options)
	
	  override def inferSchema(files: Seq[FileStatus]): Option[StructType] =
	    ParquetUtils.inferSchema(sparkSession, options.asScala.toMap, files)
	
	  override def newWriteBuilder(info: LogicalWriteInfo): WriteBuilder =
	    new ParquetWriteBuilder(paths, formatName, supportsDataType, info)
	
	  override def supportsDataType(dataType: DataType): Boolean = dataType match {
			..
	  override def formatName: String = "Parquet"
	}

$ grep -R --include '*.scala' ParquetTable | grep -v /test/ | grep -v target/
core/src/main/scala/org/apache/spark/sql/execution/datasources/v2/parquet:
	ParquetDataSourceV2.scala:    ParquetTable(tableName, sparkSession, optionsWithoutPaths, paths, None, fallbackFileFormat)
	ParquetDataSourceV2.scala:    ParquetTable(
	ParquetTable.scala:case class ParquetTable(

ParquetTable.scala :

	class ParquetDataSourceV2 extends FileDataSourceV2 {
	
	  override def fallbackFileFormat: Class[_ <: FileFormat] = classOf[ParquetFileFormat]
	
	  override def shortName(): String = "parquet"
	
	  override def getTable(options: CaseInsensitiveStringMap): Table = {
	    val paths = getPaths(options)
	    val tableName = getTableName(options, paths)
	    val optionsWithoutPaths = getOptionsWithoutPaths(options)
	    ParquetTable(tableName, sparkSession, optionsWithoutPaths, paths, None, fallbackFileFormat)
	  }
	
	  override def getTable(options: CaseInsensitiveStringMap, schema: StructType): Table = {
	    val paths = getPaths(options)
	    val tableName = getTableName(options, paths)
	    val optionsWithoutPaths = getOptionsWithoutPaths(options)
	    ParquetTable(
	      tableName, sparkSession, optionsWithoutPaths, paths, Some(schema), fallbackFileFormat)
	  }
	}

$ grep -R --include '*.scala' FileDataSourceV2 | grep -v /test/ | grep -v target/

core/src/main/scala/org/apache/spark/sql/catalyst/analysis:
	ResolveSessionCatalog.scala:import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2
	ResolveSessionCatalog.scala:      case Some(_: FileDataSourceV2) => false core/src/main/scala/org/apache/spark/sql/DataFrameWriter.scala:        if (provider.isInstanceOf[FileDataSourceV2]) {

core/src/main/scala/org/apache/spark/sql/:
	DataFrameWriter.scala:      case Some(_: FileDataSourceV2) => None
	execution/datasources/DataSource.scala:import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2
	execution/datasources/DataSource.scala:      case f: FileDataSourceV2 => f.fallbackFileFormat
	execution/datasources/FallBackFileSourceV2.scala:import org.apache.spark.sql.execution.datasources.v2.{DataSourceV2Relation, FileDataSourceV2, FileTable}
	execution/datasources/rules.scala:import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2
	execution/datasources/rules.scala:    case f: FileDataSourceV2 => f.fallbackFileFormat
	execution/datasources/v2/csv/CSVDataSourceV2.scala:import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2
	execution/datasources/v2/csv/CSVDataSourceV2.scala:class CSVDataSourceV2 extends FileDataSourceV2 {
	execution/datasources/v2/FileDataSourceV2.scala:trait FileDataSourceV2 extends TableProvider with DataSourceRegister {
	execution/datasources/v2/json/JsonDataSourceV2.scala:class JsonDataSourceV2 extends FileDataSourceV2 {
	execution/datasources/v2/orc/OrcDataSourceV2.scala:class OrcDataSourceV2 extends FileDataSourceV2 {
	execution/datasources/v2/parquet/ParquetDataSourceV2.scala:class ParquetDataSourceV2 extends FileDataSourceV2 {
	execution/datasources/v2/text/TextDataSourceV2.scala:import org.apache.spark.sql.execution.datasources.v2.FileDataSourceV2
	execution/datasources/v2/text/TextDataSourceV2.scala:class TextDataSourceV2 extends FileDataSourceV2 {
	streaming/DataStreamReader.scala:import org.apache.spark.sql.execution.datasources.v2.{DataSourceV2Utils, FileDataSourceV2}
	streaming/DataStreamReader.scala:      case provider: TableProvider if !provider.isInstanceOf[FileDataSourceV2] =>
	streaming/DataStreamWriter.scala:import org.apache.spark.sql.execution.datasources.v2.{DataSourceV2Utils, FileDataSourceV2}
	streaming/DataStreamWriter.scala:        classOf[FileDataSourceV2].isAssignableFrom(cls)

execution/datasources/DataSource.scala:

	case class DataSource(
	    sparkSession: SparkSession,
	    className: String,
	    paths: Seq[String] = Nil,
	    userSpecifiedSchema: Option[StructType] = None,
	    partitionColumns: Seq[String] = Seq.empty,
	    bucketSpec: Option[BucketSpec] = None,
	    options: Map[String, String] = Map.empty,
	    catalogTable: Option[CatalogTable] = None) 
    
	object DataSource {
	
      /**
	   * Returns an optional [[TableProvider]] instance for the given provider. It returns None if
	   * there is no corresponding Data Source V2 implementation, or the provider is configured to
	   * fallback to Data Source V1 code path.
	   */
	  def lookupDataSourceV2(provider: String, conf: SQLConf): Option[TableProvider] = {
	    val useV1Sources = conf.getConf(SQLConf.USE_V1_SOURCE_LIST).toLowerCase(Locale.ROOT)
	      .split(",").map(_.trim)
	    val cls = lookupDataSource(provider, conf)
	    cls.newInstance() match {
	      case d: DataSourceRegister if useV1Sources.contains(d.shortName()) => None
	      case t: TableProvider
	          if !useV1Sources.contains(cls.getCanonicalName.toLowerCase(Locale.ROOT)) =>
	        Some(t)
	      case _ => None
	    }
	  }


WARN datasources.DataSource: All paths were ignored:
....

Thread [main] (Suspended (breakpoint at line 326 in FileOutputStream))	
	Log4jLoggerAdapter.warn(String) line: 401	
	DataSource$(Logging).logWarning(Function0<String>) line: 69	
	Logging.logWarning$(Logging, Function0) line: 68	
	DataSource$.logWarning(Function0<String>) line: 584	
	DataSource$.checkAndGlobPathIfNecessary(Seq<String>, Configuration, boolean, boolean) line: 775	
	ParquetTable(FileTable).fileIndex$lzycompute() line: 56	
	ParquetTable(FileTable).fileIndex() line: 44	
	FileTable.$anonfun$dataSchema$1(FileTable, StructType) line: 65	
	783983592.apply(Object) line: not available	
	Some<A>(Option<A>).map(Function1<A,B>) line: 230	
	ParquetTable(FileTable).dataSchema$lzycompute() line: 64	
	ParquetTable(FileTable).dataSchema() line: 63	
	ParquetTable(FileTable).schema$lzycompute() line: 82	
	ParquetTable(FileTable).schema() line: 80	
	DataSourceV2Relation$.create(Table, Option<CatalogPlugin>, Option<Identifier>, CaseInsensitiveStringMap) line: 150	
	DataSourceV2Relation$.create(Table, Option<CatalogPlugin>, Option<Identifier>) line: 158	
	Analyzer$ResolveTables$.org$apache$spark$sql$catalyst$analysis$Analyzer$ResolveTables$$lookupV2Relation(Seq<String>) line: 924	
	Analyzer$ResolveTables$$anonfun$apply$8.applyOrElse(A1, Function1<A1,B1>) line: 886	
	Analyzer$ResolveTables$$anonfun$apply$8.applyOrElse(Object, Function1) line: 884	
	AnalysisHelper.$anonfun$resolveOperatorsUp$3(AnalysisHelper, PartialFunction) line: 90	
	1302262641.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	AnalysisHelper.$anonfun$resolveOperatorsUp$1(AnalysisHelper, PartialFunction) line: 90	
	114332542.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	UnresolvedRelation(AnalysisHelper).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 86	
	AnalysisHelper.resolveOperatorsUp$(AnalysisHelper, PartialFunction) line: 84	
	UnresolvedRelation(LogicalPlan).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	AnalysisHelper.$anonfun$resolveOperatorsUp$2(PartialFunction, LogicalPlan) line: 87	
	2108139826.apply(Object) line: not available	
	TreeNode<BaseType>.$anonfun$mapChildren$1(TreeNode, Function1, boolean, BooleanRef, Object) line: 399	
	1567433186.apply(Object) line: not available	
	Project(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>, boolean) line: 397	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>) line: 350	
	AnalysisHelper.$anonfun$resolveOperatorsUp$1(AnalysisHelper, PartialFunction) line: 87	
	114332542.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	Project(AnalysisHelper).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 86	
	AnalysisHelper.resolveOperatorsUp$(AnalysisHelper, PartialFunction) line: 84	
	Project(LogicalPlan).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	Analyzer$ResolveTables$.apply(LogicalPlan) line: 884	
	Analyzer$ResolveTables$.apply(TreeNode) line: 883	
	RuleExecutor<TreeType>.$anonfun$execute$2(RuleExecutor, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch, TreeNode, Rule) line: 149	
	1007483565.apply(Object, Object) line: not available	
	$colon$colon<B>(LinearSeqOptimized<A,Repr>).foldLeft(B, Function2<B,A,B>) line: 126	
	LinearSeqOptimized<A,Repr>.foldLeft$(LinearSeqOptimized, Object, Function2) line: 122	
	$colon$colon<B>(List<A>).foldLeft(B, Function2<B,A,B>) line: 89	
	RuleExecutor<TreeType>.$anonfun$execute$1(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 146	
	RuleExecutor<TreeType>.$anonfun$execute$1$adapted(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 138	
	1792893551.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).execute(TreeType) line: 138	
	HiveSessionStateBuilder$$anon$1(Analyzer).org$apache$spark$sql$catalyst$analysis$Analyzer$$executeSameContext(LogicalPlan) line: 176	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(LogicalPlan) line: 170	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(TreeNode) line: 130	
	RuleExecutor<TreeType>.$anonfun$executeAndTrack$1(RuleExecutor, TreeNode) line: 116	
	379782668.apply() line: not available	
	QueryPlanningTracker$.withTracker(QueryPlanningTracker, Function0<T>) line: 88	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).executeAndTrack(TreeType, QueryPlanningTracker) line: 116	
	Analyzer.$anonfun$executeAndCheck$1(Analyzer, LogicalPlan, QueryPlanningTracker) line: 154	
	2066514832.apply() line: not available	
	AnalysisHelper$.markInAnalyzer(Function0<T>) line: 201	
	HiveSessionStateBuilder$$anon$1(Analyzer).executeAndCheck(LogicalPlan, QueryPlanningTracker) line: 153	
	QueryExecution.$anonfun$analyzed$1(QueryExecution) line: 68	
	895029075.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	495628508.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.analyzed$lzycompute() line: 68	
	QueryExecution.analyzed() line: 66	
	QueryExecution.assertAnalyzed() line: 58	
	Dataset$.$anonfun$ofRows$2(SparkSession, LogicalPlan, QueryPlanningTracker) line: 99	
	268402121.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	Dataset$.ofRows(SparkSession, LogicalPlan, QueryPlanningTracker) line: 97	
	SparkSession.$anonfun$sql$1(SparkSession, String) line: 607	
    
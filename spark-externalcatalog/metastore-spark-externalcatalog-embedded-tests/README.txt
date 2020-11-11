
without redefining eternal external catalog..
------------------

scala> val extCatalog = spark.sharedState.externalCatalog
	extCatalog: org.apache.spark.sql.catalyst.catalog.ExternalCatalogWithListener = org.apache.spark.sql.catalyst.catalog.ExternalCatalogWithListener@59e0cb10

scala> extCatalog.listDatabases("*")
	res0: Seq[String] = Buffer(db1, default)

scala> val db1 = extCatalog.getDatabase("db1")
	db1: org.apache.spark.sql.catalyst.catalog.CatalogDatabase = CatalogDatabase(db1,,file:/D:/arn/hadoop/rootfs/db1,Map(owner -> ))

scala> extCatalog.listTables("db1")
	res1: Seq[String] = Buffer(iceberg_table1, iceberg_table2, t1, t2, t3, table2_event)

scala> val t1 = extCatalog.getTable("db1", "t1")
	t1: org.apache.spark.sql.catalyst.catalog.CatalogTable =
	CatalogTable(
	Database: db1
	Table: t1
	Owner: arnaud
	Created Time: Tue Sep 22 22:22:10 CEST 2020
	Last Access: UNKNOWN
	Created By: Spark 3.0.0-preview
	Type: EXTERNAL
	Provider: hive
	Table Properties: [transient_lastDdlTime=1600806130]
	Location: file:/D:/arn/hadoop/rootfs/db1/t1
	Serde Library: org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe
	InputFormat: org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat
	OutputFormat: org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat
	Storage Properties: [serialization.format=1]
	Partition Provider: Catalog
	Schema: root
	 |-- col_str: string (nullable = true)
	 |-- col_int: integer (nullable = true)
	)

	

scala> spark.sql("select * from db1.t1").show()
+-------+-------+
|col_str|col_int|
+-------+-------+
|  test2|   1234|
|  test2|   1234|
|   test|    123|
+-------+-------+

[Stage 8:>                                                          (0 + 1) / 1]
Stack trace of opening avro file for read:
	
Daemon Thread [Executor task launch worker for task 12] (Suspended (breakpoint at line 195 in FileInputStream))	
	owns: LazyRef<T>  (id=511)	
	FileInputStream.open(String) line: 195	
	FileInputStream.<init>(File) line: 138	
	RawLocalFileSystem$LocalFSFileInputStream.<init>(RawLocalFileSystem, Path) line: 111	
	RawLocalFileSystem.open(Path, int) line: 213	
	ChecksumFileSystem$ChecksumFSInputChecker.<init>(ChecksumFileSystem, Path, int) line: 152	
	ProxyLocalFileSystem(ChecksumFileSystem).open(Path, int) line: 347	
	ProxyLocalFileSystem(FileSystem).open(Path) line: 899	
	HadoopInputFile.newStream() line: 65	
	ParquetFileReader.readFooter(InputFile, ParquetMetadataConverter$MetadataFilter) line: 498	
	ParquetFileReader.readFooter(Configuration, Path, ParquetMetadataConverter$MetadataFilter) line: 448	
	ParquetFileFormat.footerFileMetaData$lzycompute$1(LazyRef, Configuration, Path) line: 272	
	ParquetFileFormat.footerFileMetaData$1(LazyRef, Configuration, Path) line: 271	
	ParquetFileFormat.$anonfun$buildReaderWithPartitionValues$2(ParquetFileFormat, StructType, Broadcast, boolean, boolean, boolean, boolean, boolean, int, boolean, Seq, boolean, boolean, boolean, int, boolean, boolean, StructType, PartitionedFile) line: 275	
	1453812534.apply(Object) line: not available	
	FileScanRDD$$anon$1.org$apache$spark$sql$execution$datasources$FileScanRDD$$anon$$readCurrentFile() line: 116	
	FileScanRDD$$anon$1.nextIterator() line: 169	
	FileScanRDD$$anon$1.hasNext() line: 93	
	FileSourceScanExec$$anon$1.hasNext() line: 491	
	GeneratedClass$GeneratedIteratorForCodegenStage1.columnartorow_nextBatch_0$(GeneratedClass$GeneratedIteratorForCodegenStage1) line: 30	
	GeneratedClass$GeneratedIteratorForCodegenStage1.processNext() line: 43	
	GeneratedClass$GeneratedIteratorForCodegenStage1(BufferedRowIterator).hasNext() line: 43	
	WholeStageCodegenExec$$anon$1.hasNext() line: 729	
	SparkPlan.$anonfun$getByteArrayRdd$1(boolean, int, Iterator) line: 340	
	1498693087.apply(Object) line: not available	
	RDD<T>.$anonfun$mapPartitionsInternal$2(Function1, TaskContext, int, Iterator) line: 872	
	RDD<T>.$anonfun$mapPartitionsInternal$2$adapted(Function1, TaskContext, Object, Iterator) line: 872	
	1041620384.apply(Object, Object, Object) line: not available	
	MapPartitionsRDD<U,T>.compute(Partition, TaskContext) line: 52	
	MapPartitionsRDD<U,T>(RDD<T>).computeOrReadCheckpoint(Partition, TaskContext) line: 349	
	MapPartitionsRDD<U,T>(RDD<T>).iterator(Partition, TaskContext) line: 313	
	ResultTask<T,U>.runTask(TaskContext) line: 90	
	ResultTask<T,U>(Task<T>).run(long, int, MetricsSystem, Map<String,ResourceInformation>) line: 127	
	Executor$TaskRunner.$anonfun$run$3(Executor$TaskRunner, BooleanRef) line: 446	
	942174434.apply() line: not available	
	Utils$.tryWithSafeFinally(Function0<T>, Function0<BoxedUnit>) line: 1377	
	Executor$TaskRunner.run() line: 449	
	ThreadPoolExecutor.runWorker(ThreadPoolExecutor$Worker) line: 1149	
	ThreadPoolExecutor$Worker.run() line: 624	
	UninterruptibleThread(Thread).run() line: 748	



stack trace on driver for ClassLoader.defineClass .. while compiling spark.sql() command

Thread [main] (Suspended (breakpoint at line 761 in ClassLoader))	
	owns: Object  (id=1175)	
	owns: QueryExecution  (id=1176)	
	owns: $eval$  (id=1093)	
	Launcher$AppClassLoader(ClassLoader).defineClass(String, byte[], int, int, ProtectionDomain) line: 761	
	Launcher$AppClassLoader(SecureClassLoader).defineClass(String, byte[], int, int, CodeSource) line: 142	
	Launcher$AppClassLoader(URLClassLoader).defineClass(String, Resource) line: 468	
	URLClassLoader.access$100(URLClassLoader, String, Resource) line: 74	
	URLClassLoader$1.run() line: 369	
	URLClassLoader$1.run() line: 363	
	AccessController.doPrivileged(PrivilegedExceptionAction<T>, AccessControlContext) line: not available [native method]	
	Launcher$AppClassLoader(URLClassLoader).findClass(String) line: 362	
	Launcher$AppClassLoader(ClassLoader).loadClass(String, boolean) line: 424	
	Launcher$AppClassLoader.loadClass(String, boolean) line: 349	
	Launcher$AppClassLoader(ClassLoader).loadClass(String) line: 357	
	Analyzer$ResolveFunctions$$anonfun$apply$16$$anonfun$applyOrElse$102.$anonfun$applyOrElse$105(Analyzer$ResolveFunctions$$anonfun$apply$16$$anonfun$applyOrElse$102, FunctionIdentifier, Seq, boolean, Option) line: 1967	
	1281268903.apply() line: not available	
	package$.withPosition(TreeNode<?>, Function0<A>) line: 53	
	Analyzer$ResolveFunctions$$anonfun$apply$16$$anonfun$applyOrElse$102.applyOrElse(A1, Function1<A1,B1>) line: 1944	
	Analyzer$ResolveFunctions$$anonfun$apply$16$$anonfun$applyOrElse$102.applyOrElse(Object, Function1) line: 1927	
	TreeNode<BaseType>.$anonfun$transformDown$1(TreeNode, PartialFunction) line: 309	
	663985858.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	UnresolvedFunction(TreeNode<BaseType>).transformDown(PartialFunction<BaseType,BaseType>) line: 309	
	TreeNode<BaseType>.$anonfun$transformDown$3(PartialFunction, TreeNode) line: 314	
	1476269737.apply(Object) line: not available	
	TreeNode<BaseType>.$anonfun$mapChildren$1(TreeNode, Function1, boolean, BooleanRef, Object) line: 399	
	2100591314.apply(Object) line: not available	
	UnresolvedAlias(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	UnresolvedAlias(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>, boolean) line: 397	
	UnresolvedAlias(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>) line: 350	
	UnresolvedAlias(TreeNode<BaseType>).transformDown(PartialFunction<BaseType,BaseType>) line: 314	
	QueryPlan<PlanType>.$anonfun$transformExpressionsDown$1(PartialFunction, Expression) line: 96	
	234249975.apply(Object) line: not available	
	QueryPlan<PlanType>.$anonfun$mapExpressions$1(Function1, Expression) line: 118	
	405629654.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	QueryPlan<PlanType>.transformExpression$1(Expression, Function1, BooleanRef) line: 118	
	QueryPlan<PlanType>.recursiveTransform$1(Object, Function1, BooleanRef) line: 129	
	QueryPlan<PlanType>.$anonfun$mapExpressions$3(Function1, BooleanRef, Object) line: 134	
	1053439564.apply(Object) line: not available	
	TraversableLike<A,Repr>.$anonfun$map$1(Builder, Function1, Object) line: 238	
	1855026648.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	$colon$colon<B>(TraversableLike<A,Repr>).map(Function1<A,B>, CanBuildFrom<Repr,B,That>) line: 238	
	TraversableLike<A,Repr>.map$(TraversableLike, Function1, CanBuildFrom) line: 231	
	$colon$colon<B>(List<A>).map(Function1<A,B>, CanBuildFrom<List<A>,B,That>) line: 298	
	QueryPlan<PlanType>.recursiveTransform$1(Object, Function1, BooleanRef) line: 134	
	QueryPlan<PlanType>.$anonfun$mapExpressions$4(Function1, BooleanRef, Object) line: 139	
	644292702.apply(Object) line: not available	
	Project(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	Project(QueryPlan<PlanType>).mapExpressions(Function1<Expression,Expression>) line: 139	
	Project(QueryPlan<PlanType>).transformExpressionsDown(PartialFunction<Expression,Expression>) line: 96	
	Project(QueryPlan<PlanType>).transformExpressions(PartialFunction<Expression,Expression>) line: 87	
	Analyzer$ResolveFunctions$$anonfun$apply$16.applyOrElse(A1, Function1<A1,B1>) line: 1927	
	Analyzer$ResolveFunctions$$anonfun$apply$16.applyOrElse(Object, Function1) line: 1925	
	AnalysisHelper.$anonfun$resolveOperatorsUp$3(AnalysisHelper, PartialFunction) line: 90	
	6219287.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	AnalysisHelper.$anonfun$resolveOperatorsUp$1(AnalysisHelper, PartialFunction) line: 90	
	1906508327.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	Project(AnalysisHelper).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 86	
	AnalysisHelper.resolveOperatorsUp$(AnalysisHelper, PartialFunction) line: 84	
	Project(LogicalPlan).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	Analyzer$ResolveFunctions$.apply(LogicalPlan) line: 1925	
	Analyzer$ResolveFunctions$.apply(TreeNode) line: 1923	
	RuleExecutor<TreeType>.$anonfun$execute$2(RuleExecutor, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch, TreeNode, Rule) line: 149	
	1211716631.apply(Object, Object) line: not available	
	$colon$colon<B>(LinearSeqOptimized<A,Repr>).foldLeft(B, Function2<B,A,B>) line: 126	
	LinearSeqOptimized<A,Repr>.foldLeft$(LinearSeqOptimized, Object, Function2) line: 122	
	$colon$colon<B>(List<A>).foldLeft(B, Function2<B,A,B>) line: 89	
	RuleExecutor<TreeType>.$anonfun$execute$1(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 146	
	RuleExecutor<TreeType>.$anonfun$execute$1$adapted(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 138	
	328115123.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).execute(TreeType) line: 138	
	HiveSessionStateBuilder$$anon$1(Analyzer).org$apache$spark$sql$catalyst$analysis$Analyzer$$executeSameContext(LogicalPlan) line: 176	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(LogicalPlan) line: 170	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(TreeNode) line: 130	
	RuleExecutor<TreeType>.$anonfun$executeAndTrack$1(RuleExecutor, TreeNode) line: 116	
	896023646.apply() line: not available	
	QueryPlanningTracker$.withTracker(QueryPlanningTracker, Function0<T>) line: 88	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).executeAndTrack(TreeType, QueryPlanningTracker) line: 116	
	Analyzer.$anonfun$executeAndCheck$1(Analyzer, LogicalPlan, QueryPlanningTracker) line: 154	
	1402701580.apply() line: not available	
	AnalysisHelper$.markInAnalyzer(Function0<T>) line: 201	
	HiveSessionStateBuilder$$anon$1(Analyzer).executeAndCheck(LogicalPlan, QueryPlanningTracker) line: 153	
	QueryExecution.$anonfun$analyzed$1(QueryExecution) line: 68	
	310284466.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	1794428624.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.analyzed$lzycompute() line: 68	
	QueryExecution.analyzed() line: 66	
	QueryExecution.assertAnalyzed() line: 58	
	Dataset$.$anonfun$ofRows$2(SparkSession, LogicalPlan, QueryPlanningTracker) line: 99	
	695324329.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	Dataset$.ofRows(SparkSession, LogicalPlan, QueryPlanningTracker) line: 97	
	SparkSession.$anonfun$sql$1(SparkSession, String) line: 607	
	126478282.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	SparkSession.sql(String) line: 602	


stack trace .. 
	InputRDDCodegen.doProduce$(InputRDDCodegen, CodegenContext) line: 456	

Thread [main] (Suspended (breakpoint at line 761 in ClassLoader))	
	owns: Object  (id=1777)	
	owns: Count  (id=1290)	
	owns: $eval$  (id=1093)	
	Launcher$AppClassLoader(ClassLoader).defineClass(String, byte[], int, int, ProtectionDomain) line: 761	
	Launcher$AppClassLoader(SecureClassLoader).defineClass(String, byte[], int, int, CodeSource) line: 142	
	Launcher$AppClassLoader(URLClassLoader).defineClass(String, Resource) line: 468	
	URLClassLoader.access$100(URLClassLoader, String, Resource) line: 74	
	URLClassLoader$1.run() line: 369	
	URLClassLoader$1.run() line: 363	
	AccessController.doPrivileged(PrivilegedExceptionAction<T>, AccessControlContext) line: not available [native method]	
	Launcher$AppClassLoader(URLClassLoader).findClass(String) line: 362	
	Launcher$AppClassLoader(ClassLoader).loadClass(String, boolean) line: 424	
	Launcher$AppClassLoader.loadClass(String, boolean) line: 349	
	Launcher$AppClassLoader(ClassLoader).loadClass(String) line: 357	
	Count.mergeExpressions$lzycompute() line: 60	
	Count.mergeExpressions() line: 59	
	HashAggregateExec.$anonfun$doConsumeWithoutKeys$3(AggregateExpression) line: 343	
	1823575035.apply(Object) line: not available	
	TraversableLike<A,Repr>.$anonfun$map$1(Builder, Function1, Object) line: 238	
	1855026648.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	$colon$colon<B>(TraversableLike<A,Repr>).map(Function1<A,B>, CanBuildFrom<Repr,B,That>) line: 238	
	TraversableLike<A,Repr>.map$(TraversableLike, Function1, CanBuildFrom) line: 231	
	$colon$colon<B>(List<A>).map(Function1<A,B>, CanBuildFrom<List<A>,B,That>) line: 298	
	HashAggregateExec.doConsumeWithoutKeys(CodegenContext, Seq<ExprCode>) line: 338	
	HashAggregateExec.doConsume(CodegenContext, Seq<ExprCode>, ExprCode) line: 175	
	InputAdapter(CodegenSupport).constructDoConsumeFunction(CodegenContext, Seq<ExprCode>, String) line: 221	
	InputAdapter(CodegenSupport).consume(CodegenContext, Seq<ExprCode>, String) line: 192	
	CodegenSupport.consume$(CodegenSupport, CodegenContext, Seq, String) line: 149	
	InputAdapter.consume(CodegenContext, Seq<ExprCode>, String) line: 496	
	InputAdapter(InputRDDCodegen).doProduce(CodegenContext) line: 483	
	InputRDDCodegen.doProduce$(InputRDDCodegen, CodegenContext) line: 456	
	InputAdapter.doProduce(CodegenContext) line: 496	
	CodegenSupport.$anonfun$produce$1(CodegenSupport, CodegenSupport, CodegenContext) line: 95	
	1934387496.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	662058657.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	InputAdapter(SparkPlan).executeQuery(Function0<T>) line: 210	
	InputAdapter(CodegenSupport).produce(CodegenContext, CodegenSupport) line: 90	
	CodegenSupport.produce$(CodegenSupport, CodegenContext, CodegenSupport) line: 90	
	InputAdapter.produce(CodegenContext, CodegenSupport) line: 496	
	HashAggregateExec.doProduceWithoutKeys(CodegenContext) line: 243	
	HashAggregateExec.doProduce(CodegenContext) line: 167	
	CodegenSupport.$anonfun$produce$1(CodegenSupport, CodegenSupport, CodegenContext) line: 95	
	1934387496.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	662058657.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	HashAggregateExec(SparkPlan).executeQuery(Function0<T>) line: 210	
	HashAggregateExec(CodegenSupport).produce(CodegenContext, CodegenSupport) line: 90	
	CodegenSupport.produce$(CodegenSupport, CodegenContext, CodegenSupport) line: 90	
	HashAggregateExec.produce(CodegenContext, CodegenSupport) line: 48	
	WholeStageCodegenExec.doCodeGen() line: 632	
	WholeStageCodegenExec.doExecute() line: 692	
	SparkPlan.$anonfun$execute$1(SparkPlan) line: 175	
	474815069.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	662058657.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	WholeStageCodegenExec(SparkPlan).executeQuery(Function0<T>) line: 210	
	WholeStageCodegenExec(SparkPlan).execute() line: 171	
	WholeStageCodegenExec(SparkPlan).getByteArrayRdd(int, boolean) line: 316	
	WholeStageCodegenExec(SparkPlan).executeTake(int, boolean) line: 434	
	WholeStageCodegenExec(SparkPlan).executeTake(int) line: 420	
	CollectLimitExec.executeCollect() line: 47	
	Dataset<T>.collectFromPlan(SparkPlan) line: 3627	
	Dataset<T>.$anonfun$head$1(Dataset, SparkPlan) line: 2697	
	368964486.apply(Object) line: not available	
	Dataset<T>.$anonfun$withAction$1(QueryExecution, Function1) line: 3618	
	1506137502.apply() line: not available	
	SQLExecution$.$anonfun$withNewExecutionId$5(SparkContext, long, String, CallSite, QueryExecution, Function0, Option) line: 100	
	1028403599.apply() line: not available	
	SQLExecution$.withSQLConfPropagated(SparkSession, Function0<T>) line: 160	
	SQLExecution$.$anonfun$withNewExecutionId$1(QueryExecution, Function0, Option) line: 87	
	1645416527.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	SQLExecution$.withNewExecutionId(QueryExecution, Option<String>, Function0<T>) line: 64	
	Dataset<T>.withAction(String, QueryExecution, Function1<SparkPlan,U>) line: 3616	
	Dataset<T>.head(int) line: 2697	
	Dataset<T>.take(int) line: 2904	
	Dataset<T>.getRows(int, int) line: 300	
	Dataset<T>.showString(int, int, boolean) line: 337	
	Dataset<T>.show(int, boolean) line: 824	
	Dataset<T>.show(int) line: 783	
	Dataset<T>.show() line: 792	


stack trace CatalogTable.storage()  .. called from resolve plan

Thread [main] (Suspended (entry into method storage in CatalogTable))	
	owns: HiveExternalCatalog  (id=455)	
	owns: LazyRef<T>  (id=966)	
	owns: QueryExecution  (id=967)	
	owns: $eval$  (id=968)	
	CatalogTable.storage() line: 233	
	HiveExternalCatalog.restoreHiveSerdeTable(CatalogTable) line: 792	
	HiveExternalCatalog.restoreTableMetadata(CatalogTable) line: 754	
	HiveExternalCatalog.$anonfun$getTable$1(HiveExternalCatalog, String, String) line: 723	
	2032436447.apply() line: not available	
	HiveExternalCatalog.withClient(Function0<T>) line: 103	
	HiveExternalCatalog.getTable(String, String) line: 723	
	ExternalCatalogWithListener.getTable(String, String) line: 138	
	HiveSessionCatalog(SessionCatalog).getTableMetadata(TableIdentifier) line: 446	
	V2SessionCatalog.loadTable(Identifier) line: 66	
	CatalogV2Util$.loadTable(CatalogPlugin, Identifier) line: 283	
	Analyzer$ResolveRelations$.loaded$lzycompute$1(LazyRef, CatalogPlugin, Identifier) line: 1010	
	Analyzer$ResolveRelations$.loaded$1(LazyRef, CatalogPlugin, Identifier) line: 1010	
	Analyzer$ResolveRelations$.$anonfun$lookupRelation$3(Analyzer$ResolveRelations$, String[], LazyRef, CatalogPlugin, Identifier) line: 1022	
	481441333.apply() line: not available	
	None$(Option<A>).orElse(Function0<Option<B>>) line: 447	
	Analyzer$ResolveRelations$.org$apache$spark$sql$catalyst$analysis$Analyzer$ResolveRelations$$lookupRelation(Seq<String>) line: 1021	
	Analyzer$ResolveRelations$$anonfun$apply$9.applyOrElse(A1, Function1<A1,B1>) line: 977	
	Analyzer$ResolveRelations$$anonfun$apply$9.applyOrElse(Object, Function1) line: 962	
	AnalysisHelper.$anonfun$resolveOperatorsUp$3(AnalysisHelper, PartialFunction) line: 90	
	452045545.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	AnalysisHelper.$anonfun$resolveOperatorsUp$1(AnalysisHelper, PartialFunction) line: 90	
	1503386238.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	UnresolvedRelation(AnalysisHelper).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 86	
	AnalysisHelper.resolveOperatorsUp$(AnalysisHelper, PartialFunction) line: 84	
	UnresolvedRelation(LogicalPlan).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	AnalysisHelper.$anonfun$resolveOperatorsUp$2(PartialFunction, LogicalPlan) line: 87	
	957497429.apply(Object) line: not available	
	TreeNode<BaseType>.$anonfun$mapChildren$1(TreeNode, Function1, boolean, BooleanRef, Object) line: 399	
	566911291.apply(Object) line: not available	
	Project(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>, boolean) line: 397	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>) line: 350	
	AnalysisHelper.$anonfun$resolveOperatorsUp$1(AnalysisHelper, PartialFunction) line: 87	
	1503386238.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	Project(AnalysisHelper).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 86	
	AnalysisHelper.resolveOperatorsUp$(AnalysisHelper, PartialFunction) line: 84	
	Project(LogicalPlan).resolveOperatorsUp(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	Analyzer$ResolveRelations$.apply(LogicalPlan) line: 962	
	Analyzer$ResolveRelations$.apply(TreeNode) line: 934	
	RuleExecutor<TreeType>.$anonfun$execute$2(RuleExecutor, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch, TreeNode, Rule) line: 149	
	141830962.apply(Object, Object) line: not available	
	$colon$colon<B>(LinearSeqOptimized<A,Repr>).foldLeft(B, Function2<B,A,B>) line: 126	
	LinearSeqOptimized<A,Repr>.foldLeft$(LinearSeqOptimized, Object, Function2) line: 122	
	$colon$colon<B>(List<A>).foldLeft(B, Function2<B,A,B>) line: 89	
	RuleExecutor<TreeType>.$anonfun$execute$1(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 146	
	RuleExecutor<TreeType>.$anonfun$execute$1$adapted(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 138	
	2004078905.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).execute(TreeType) line: 138	
	HiveSessionStateBuilder$$anon$1(Analyzer).org$apache$spark$sql$catalyst$analysis$Analyzer$$executeSameContext(LogicalPlan) line: 176	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(LogicalPlan) line: 170	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(TreeNode) line: 130	
	RuleExecutor<TreeType>.$anonfun$executeAndTrack$1(RuleExecutor, TreeNode) line: 116	
	1799479168.apply() line: not available	
	QueryPlanningTracker$.withTracker(QueryPlanningTracker, Function0<T>) line: 88	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).executeAndTrack(TreeType, QueryPlanningTracker) line: 116	
	Analyzer.$anonfun$executeAndCheck$1(Analyzer, LogicalPlan, QueryPlanningTracker) line: 154	
	1876392982.apply() line: not available	
	AnalysisHelper$.markInAnalyzer(Function0<T>) line: 201	
	HiveSessionStateBuilder$$anon$1(Analyzer).executeAndCheck(LogicalPlan, QueryPlanningTracker) line: 153	
	QueryExecution.$anonfun$analyzed$1(QueryExecution) line: 68	
	1175073230.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	823993873.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.analyzed$lzycompute() line: 68	
	QueryExecution.analyzed() line: 66	
	QueryExecution.assertAnalyzed() line: 58	
	Dataset$.$anonfun$ofRows$2(SparkSession, LogicalPlan, QueryPlanningTracker) line: 99	
	348993624.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	Dataset$.ofRows(SparkSession, LogicalPlan, QueryPlanningTracker) line: 97	



Thread [main] (Suspended (entry into method storage in CatalogTable))	
	owns: QueryExecution  (id=967)	
	owns: $eval$  (id=968)	
	CatalogTable.storage() line: 233	
	RelationConversions.org$apache$spark$sql$hive$RelationConversions$$isConvertible(CatalogTable) line: 199	
	RelationConversions.org$apache$spark$sql$hive$RelationConversions$$isConvertible(HiveTableRelation) line: 195	
	RelationConversions$$anonfun$apply$4.applyOrElse(A1, Function1<A1,B1>) line: 219	
	RelationConversions$$anonfun$apply$4.applyOrElse(Object, Function1) line: 207	
	AnalysisHelper.$anonfun$resolveOperatorsDown$2(AnalysisHelper, PartialFunction) line: 108	
	1173244135.apply() line: not available	
	CurrentOrigin$.withOrigin(Origin, Function0<A>) line: 72	
	AnalysisHelper.$anonfun$resolveOperatorsDown$1(AnalysisHelper, PartialFunction) line: 108	
	1646977722.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	HiveTableRelation(AnalysisHelper).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 106	
	AnalysisHelper.resolveOperatorsDown$(AnalysisHelper, PartialFunction) line: 104	
	HiveTableRelation(LogicalPlan).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	AnalysisHelper.$anonfun$resolveOperatorsDown$4(PartialFunction, LogicalPlan) line: 113	
	1474535864.apply(Object) line: not available	
	TreeNode<BaseType>.$anonfun$mapChildren$1(TreeNode, Function1, boolean, BooleanRef, Object) line: 399	
	566911291.apply(Object) line: not available	
	SubqueryAlias(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	SubqueryAlias(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>, boolean) line: 397	
	SubqueryAlias(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>) line: 350	
	AnalysisHelper.$anonfun$resolveOperatorsDown$1(AnalysisHelper, PartialFunction) line: 113	
	1646977722.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	SubqueryAlias(AnalysisHelper).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 106	
	AnalysisHelper.resolveOperatorsDown$(AnalysisHelper, PartialFunction) line: 104	
	SubqueryAlias(LogicalPlan).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	AnalysisHelper.$anonfun$resolveOperatorsDown$4(PartialFunction, LogicalPlan) line: 113	
	1474535864.apply(Object) line: not available	
	TreeNode<BaseType>.$anonfun$mapChildren$1(TreeNode, Function1, boolean, BooleanRef, Object) line: 399	
	566911291.apply(Object) line: not available	
	Project(TreeNode<BaseType>).mapProductIterator(Function1<Object,B>, ClassTag<B>) line: 237	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>, boolean) line: 397	
	Project(TreeNode<BaseType>).mapChildren(Function1<BaseType,BaseType>) line: 350	
	AnalysisHelper.$anonfun$resolveOperatorsDown$1(AnalysisHelper, PartialFunction) line: 113	
	1646977722.apply() line: not available	
	AnalysisHelper$.allowInvokingTransformsInAnalyzer(Function0<T>) line: 194	
	Project(AnalysisHelper).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 106	
	AnalysisHelper.resolveOperatorsDown$(AnalysisHelper, PartialFunction) line: 104	
	Project(LogicalPlan).resolveOperatorsDown(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	Project(AnalysisHelper).resolveOperators(PartialFunction<LogicalPlan,LogicalPlan>) line: 73	
	AnalysisHelper.resolveOperators$(AnalysisHelper, PartialFunction) line: 72	
	Project(LogicalPlan).resolveOperators(PartialFunction<LogicalPlan,LogicalPlan>) line: 29	
	RelationConversions.apply(LogicalPlan) line: 207	
	RelationConversions.apply(TreeNode) line: 191	
	RuleExecutor<TreeType>.$anonfun$execute$2(RuleExecutor, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch, TreeNode, Rule) line: 149	
	141830962.apply(Object, Object) line: not available	
	ArrayBuffer<A>(IndexedSeqOptimized<A,Repr>).foldLeft(B, Function2<B,A,B>) line: 60	
	IndexedSeqOptimized<A,Repr>.foldLeft$(IndexedSeqOptimized, Object, Function2) line: 68	
	ArrayBuffer<A>.foldLeft(B, Function2<B,A,B>) line: 49	
	RuleExecutor<TreeType>.$anonfun$execute$1(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 146	
	RuleExecutor<TreeType>.$anonfun$execute$1$adapted(RuleExecutor, ObjectRef, QueryExecutionMetering, RuleExecutor$PlanChangeLogger, Option, RuleExecutor$Batch) line: 138	
	2004078905.apply(Object) line: not available	
	$colon$colon<B>(List<A>).foreach(Function1<A,U>) line: 392	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).execute(TreeType) line: 138	
	HiveSessionStateBuilder$$anon$1(Analyzer).org$apache$spark$sql$catalyst$analysis$Analyzer$$executeSameContext(LogicalPlan) line: 176	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(LogicalPlan) line: 170	
	HiveSessionStateBuilder$$anon$1(Analyzer).execute(TreeNode) line: 130	
	RuleExecutor<TreeType>.$anonfun$executeAndTrack$1(RuleExecutor, TreeNode) line: 116	
	1799479168.apply() line: not available	
	QueryPlanningTracker$.withTracker(QueryPlanningTracker, Function0<T>) line: 88	
	HiveSessionStateBuilder$$anon$1(RuleExecutor<TreeType>).executeAndTrack(TreeType, QueryPlanningTracker) line: 116	
	Analyzer.$anonfun$executeAndCheck$1(Analyzer, LogicalPlan, QueryPlanningTracker) line: 154	
	1876392982.apply() line: not available	
	AnalysisHelper$.markInAnalyzer(Function0<T>) line: 201	
	HiveSessionStateBuilder$$anon$1(Analyzer).executeAndCheck(LogicalPlan, QueryPlanningTracker) line: 153	
	QueryExecution.$anonfun$analyzed$1(QueryExecution) line: 68	
	1175073230.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	823993873.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.analyzed$lzycompute() line: 68	
	QueryExecution.analyzed() line: 66	
	QueryExecution.assertAnalyzed() line: 58	
	Dataset$.$anonfun$ofRows$2(SparkSession, LogicalPlan, QueryPlanningTracker) line: 99	
	348993624.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	Dataset$.ofRows(SparkSession, LogicalPlan, QueryPlanningTracker) line: 97	





cf code...

org.apache.spark.sql.execution.WholeStageCodegenExec

	trait InputRDDCodegen extends CodegenSupport {
	  def inputRDD: RDD[InternalRow]
	  ..
	  override def doProduce(ctx: CodegenContext): String = {


	case class InputAdapter(child: SparkPlan) extends UnaryExecNode with InputRDDCodegen {


$ grep -R --include '*.scala' InputRDDCodegen
core/src/main/scala/org/apache/spark/sql/execution
	/DataSourceScanExec.scala:  extends DataSourceScanExec with InputRDDCodegen {
	/ExistingRDD.scala:    override val outputOrdering: Seq[SortOrder] = Nil) extends LeafExecNode with InputRDDCodegen {
	/LocalTableScanExec.scala:    @transient rows: Seq[InternalRow]) extends LeafExecNode with InputRDDCodegen {
	/WholeStageCodegenExec.scala:trait InputRDDCodegen extends CodegenSupport {
	/WholeStageCodegenExec.scala:case class InputAdapter(child: SparkPlan) extends UnaryExecNode with InputRDDCodegen {







SparkSession
  def sql(sqlText: String): DataFrame = withActive {
    val tracker = new QueryPlanningTracker
    val plan = tracker.measurePhase(QueryPlanningTracker.PARSING) {
      sessionState.sqlParser.parsePlan(sqlText)
    }
    Dataset.ofRows(self, plan, tracker)
  }	
  =>
   Dataset
  /** A variant of ofRows that allows passing in a tracker so we can track query parsing time. */
  def ofRows(sparkSession: SparkSession, logicalPlan: LogicalPlan, tracker: QueryPlanningTracker)
    : DataFrame = sparkSession.withActive {
    val qe = new QueryExecution(sparkSession, logicalPlan, tracker)
    qe.assertAnalyzed()
    new Dataset[Row](qe, RowEncoder(qe.analyzed.schema))
  }
  
  =>
  QueryExecution
    lazy val analyzed: LogicalPlan = executePhase(QueryPlanningTracker.ANALYSIS) {
    // We can't clone `logical` here, which will reset the `_analyzed` flag.
    sparkSession.sessionState.analyzer.executeAndCheck(logical, tracker)
  }
  =>
  Analyzer
    def executeAndCheck(plan: LogicalPlan, tracker: QueryPlanningTracker): LogicalPlan = {
    AnalysisHelper.markInAnalyzer {
      val analyzed = executeAndTrack(plan, tracker)
      try {
        checkAnalysis(analyzed)
        analyzed
      } catch {
        case e: AnalysisException =>
          val ae = new AnalysisException(e.message, e.line, e.startPosition, Option(analyzed))
          ae.setStackTrace(e.getStackTrace)
          throw ae
      }
    }
  }
  =>
  Analyzer
    object ResolveTables extends Rule[LogicalPlan] {
    def apply(plan: LogicalPlan): LogicalPlan = ResolveTempViews(plan).resolveOperatorsUp {
      ...

      case u @ UnresolvedTableOrView(NonSessionCatalogAndIdentifier(catalog, ident), _) =>
        CatalogV2Util.loadTable(catalog, ident)
          .map(ResolvedTable(catalog.asTableCatalog, ident, _))
          .getOrElse(u)

LogicalPlan:
	'Project [*]
	+- 'UnresolvedRelation [db1, t1]

 => after analyzed:
 
	 SubqueryAlias spark_catalog.db1.t1
	+- Relation[col_str#0,col_int#1] parquet
 
 
 
 
  
  => HiveSessionCatalog
    def getRelation(
      metadata: CatalogTable,
      options: CaseInsensitiveStringMap = CaseInsensitiveStringMap.empty()): LogicalPlan = {
    val name = metadata.identifier
    val db = formatDatabaseName(name.database.getOrElse(currentDb))
    val table = formatTableName(name.table)
    val multiParts = Seq(CatalogManager.SESSION_CATALOG_NAME, db, table)

    if (metadata.tableType == CatalogTableType.VIEW) {
      val viewText = metadata.viewText.getOrElse(sys.error("Invalid view without text."))
      logDebug(s"'$viewText' will be used for the view($table).")
      // The relation is a view, so we wrap the relation by:
      // 1. Add a [[View]] operator over the relation to keep track of the view desc;
      // 2. Wrap the logical plan in a [[SubqueryAlias]] which tracks the name of the view.
      val child = View(
        desc = metadata,
        output = metadata.schema.toAttributes,
        child = parser.parsePlan(viewText))
      SubqueryAlias(multiParts, child)
    } else {
      SubqueryAlias(multiParts, UnresolvedCatalogRelation(metadata, options))
    }
  }
  =>
    object ResolveTempViews extends Rule[LogicalPlan] {
    def apply(plan: LogicalPlan): LogicalPlan = plan.resolveOperatorsUp {
      case u @ UnresolvedRelation(ident, _, isStreaming) =>
        lookupTempView(ident, isStreaming).getOrElse(u)


 => HiveSessionStateBuilder
 
  /**
   * A logical query plan `Analyzer` with rules specific to Hive.
   */
  override protected def analyzer: Analyzer = new Analyzer(catalogManager, conf) {
    override val extendedResolutionRules: Seq[Rule[LogicalPlan]] =
      new ResolveHiveSerdeTable(session) +:
        new FindDataSourceTable(session) +:
        new ResolveSQLOnFile(session) +:
        new FallBackFileSourceV2(session) +:
        ResolveEncodersInScalaAgg +:
        new ResolveSessionCatalog(
          catalogManager, conf, catalog.isTempView, catalog.isTempFunction) +:
        customResolutionRules

    override val postHocResolutionRules: Seq[Rule[LogicalPlan]] =
      new DetectAmbiguousSelfJoin(conf) +:
        new DetermineTableStats(session) +:
        RelationConversions(conf, catalog) +:
        PreprocessTableCreation(session) +:
        PreprocessTableInsertion(conf) +:
        DataSourceAnalysis(conf) +:
        HiveAnalysis +:
        customPostHocResolutionRules

    override val extendedCheckRules: Seq[LogicalPlan => Unit] =
      PreWriteCheck +:
        PreReadCheck +:
        TableCapabilityCheck +:
        CommandCheck(conf) +:
        customCheckRules
  }



execute on driver : wait DAG.runJob on executors..

Thread [main] (Suspended)	
	owns: $eval$  (id=201)	
	DAGScheduler.runJob(RDD<T>, Function2<TaskContext,Iterator<T>,U>, Seq<Object>, CallSite, Function2<Object,U,BoxedUnit>, Properties) line: 766	
	SparkContext.runJob(RDD<T>, Function2<TaskContext,Iterator<T>,U>, Seq<Object>, Function2<Object,U,BoxedUnit>, ClassTag<U>) line: 2099	
	SparkContext.runJob(RDD<T>, Function2<TaskContext,Iterator<T>,U>, Seq<Object>, ClassTag<U>) line: 2120	
	SparkContext.runJob(RDD<T>, Function1<Iterator<T>,U>, Seq<Object>, ClassTag<U>) line: 2139	
	WholeStageCodegenExec(SparkPlan).executeTake(int, boolean) line: 467	
	WholeStageCodegenExec(SparkPlan).executeTake(int) line: 420	
	CollectLimitExec.executeCollect() line: 47	
	Dataset<T>.collectFromPlan(SparkPlan) line: 3627	
	Dataset<T>.$anonfun$head$1(Dataset, SparkPlan) line: 2697	
	907135720.apply(Object) line: not available	
	Dataset<T>.$anonfun$withAction$1(QueryExecution, Function1) line: 3618	
	522818337.apply() line: not available	
	SQLExecution$.$anonfun$withNewExecutionId$5(SparkContext, long, String, CallSite, QueryExecution, Function0, Option) line: 100	
	763619867.apply() line: not available	
	SQLExecution$.withSQLConfPropagated(SparkSession, Function0<T>) line: 160	
	SQLExecution$.$anonfun$withNewExecutionId$1(QueryExecution, Function0, Option) line: 87	
	355159860.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	SQLExecution$.withNewExecutionId(QueryExecution, Option<String>, Function0<T>) line: 64	
	Dataset<T>.withAction(String, QueryExecution, Function1<SparkPlan,U>) line: 3616	
	Dataset<T>.head(int) line: 2697	
	Dataset<T>.take(int) line: 2904	
	Dataset<T>.getRows(int, int) line: 300	
	Dataset<T>.showString(int, int, boolean) line: 337	
	Dataset<T>.show(int, boolean) line: 824	
	Dataset<T>.show(int) line: 783	
	Dataset<T>.show() line: 792	






Breakpoint in ctor
	class FileScanRDD(
	    @transient private val sparkSession: SparkSession,
	    readFunction: (PartitionedFile) => Iterator[InternalRow],
	    @transient val filePartitions: Seq[FilePartition])
	  extends RDD[InternalRow](sparkSession.sparkContext, Nil) {
	

Thread [main] (Suspended (entry into method <init> in FileScanRDD))	
	owns: FileSourceScanExec  (id=132)	
	owns: $eval$  (id=133)	
	FileScanRDD.<init>(SparkSession, Function1<PartitionedFile,Iterator<InternalRow>>, Seq<FilePartition>) line: 58	
	FileSourceScanExec.createNonBucketedReadRDD(Function1<PartitionedFile,Iterator<InternalRow>>, PartitionDirectory[], HadoopFsRelation) line: 590	
	FileSourceScanExec.inputRDD$lzycompute() line: 405	
	FileSourceScanExec.inputRDD() line: 390	
	FileSourceScanExec.doExecuteColumnar() line: 485	
	SparkPlan.$anonfun$executeColumnar$1(SparkPlan) line: 202	
	434920092.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	1525619347.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	FileSourceScanExec(SparkPlan).executeQuery(Function0<T>) line: 210	
	FileSourceScanExec(SparkPlan).executeColumnar() line: 198	
	InputAdapter.doExecuteColumnar() line: 519	
	SparkPlan.$anonfun$executeColumnar$1(SparkPlan) line: 202	
	434920092.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	1525619347.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	InputAdapter(SparkPlan).executeQuery(Function0<T>) line: 210	
	InputAdapter(SparkPlan).executeColumnar() line: 198	
	ColumnarToRowExec.inputRDDs() line: 196	
	ProjectExec.inputRDDs() line: 47	

		basicPhysicalOperators.scala
		/** Physical plan for Project. */
		case class ProjectExec(projectList: Seq[NamedExpression], child: SparkPlan)
		  extends UnaryExecNode
		    with CodegenSupport
		    with AliasAwareOutputPartitioning
		    with AliasAwareOutputOrdering {
		
		  override def output: Seq[Attribute] = projectList.map(_.toAttribute)
		
		  override def inputRDDs(): Seq[RDD[InternalRow]] = {
		    child.asInstanceOf[CodegenSupport].inputRDDs()


	WholeStageCodegenExec.doExecute() line: 720	
	
		  override def doExecute(): RDD[InternalRow] = {
		    val (ctx, cleanedSource) = doCodeGen()
		    val (_, compiledCodeStats) = CodeGenerator.compile(cleanedSource)
		    val references = ctx.references.toArray

		    val rdds = child.asInstanceOf[CodegenSupport].inputRDDs()
				
	
	SparkPlan.$anonfun$execute$1(SparkPlan) line: 175	
	1192631012.apply() line: not available	
	SparkPlan.$anonfun$executeQuery$1(SparkPlan, Function0) line: 213	
	1525619347.apply() line: not available	
	RDDOperationScope$.withScope(SparkContext, String, boolean, boolean, Function0<T>) line: 151	
	WholeStageCodegenExec(SparkPlan).executeQuery(Function0<T>) line: 210	
	WholeStageCodegenExec(SparkPlan).execute() line: 171	
	WholeStageCodegenExec(SparkPlan).getByteArrayRdd(int, boolean) line: 316	
	WholeStageCodegenExec(SparkPlan).executeTake(int, boolean) line: 434	
	WholeStageCodegenExec(SparkPlan).executeTake(int) line: 420	
	CollectLimitExec.executeCollect() line: 47	
	Dataset<T>.collectFromPlan(SparkPlan) line: 3627	
		private def collectFromPlan(plan: SparkPlan): Array[T] = {
		    val fromRow = resolvedEnc.createDeserializer()
		    plan.executeCollect().map(fromRow)
	  	}
	
	Dataset<T>.$anonfun$head$1(Dataset, SparkPlan) line: 2697	
	907135720.apply(Object) line: not available	
	Dataset<T>.$anonfun$withAction$1(QueryExecution, Function1) line: 3618	
	522818337.apply() line: not available	
	SQLExecution$.$anonfun$withNewExecutionId$5(SparkContext, long, String, CallSite, QueryExecution, Function0, Option) line: 100	
	763619867.apply() line: not available	
	SQLExecution$.withSQLConfPropagated(SparkSession, Function0<T>) line: 160	
	SQLExecution$.$anonfun$withNewExecutionId$1(QueryExecution, Function0, Option) line: 87	
	355159860.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	SQLExecution$.withNewExecutionId(QueryExecution, Option<String>, Function0<T>) line: 64	
	Dataset<T>.withAction(String, QueryExecution, Function1<SparkPlan,U>) line: 3616	
	Dataset<T>.head(int) line: 2697	
	Dataset<T>.take(int) line: 2904	
	Dataset<T>.getRows(int, int) line: 300	
	Dataset<T>.showString(int, int, boolean) line: 337	
	Dataset<T>.show(int, boolean) line: 824	
	Dataset<T>.show(int) line: 783	
	Dataset<T>.show() line: 792	




stack for constructor ProjectExec

Thread [main] (Suspended (entry into method <init> in ProjectExec))	
	owns: QueryExecution  (id=578)	
	owns: $eval$  (id=579)	
	ProjectExec.<init>(Seq<NamedExpression>, SparkPlan) line: 41	
		
		case class ProjectExec(projectList: Seq[NamedExpression], child: SparkPlan)
		  extends UnaryExecNode
		    with CodegenSupport
		    with AliasAwareOutputPartitioning
		    with AliasAwareOutputOrdering {
		    
		src/main/scala/org/apache/spark/sql/execution/DataSourceScanExec.scala

	FileSourceStrategy$.apply(LogicalPlan) line: 219	

		  def apply(plan: LogicalPlan): Seq[SparkPlan] = plan match {
		    case ScanOperation(projects, filters,
		      l @ LogicalRelation(fsRelation: HadoopFsRelation, _, table, _)) =>
					<== fsRelation:
					fsRelation		
						bucketSpec	scala.None$
						dataSchema	org.apache.spark.sql.types.StructType
						fileFormat	org.apache.spark.sql.execution.datasources.parquet.ParquetFileFormat
						location	org.apache.spark.sql.execution.datasources.InMemoryFileIndex	
							cachedLeafDirToChildrenFiles	
								Map(file:/D:/arn/hadoop/rootfs/db1/t1 -> 
									LocatedFileStatus{path=file:/D:/arn/hadoop/rootfs/db1/t1/part-00000-2eedd51e-9af4-4b83-9494-92a97929a631-c000.snappy.parquet; isDirectory=false; length=657; replication=1; blocksize=33554432; modification_time=1600808108746; access_time=0; owner=; group=; permission=rw-rw-rw)
						options	org.apache.spark.sql.catalyst.util.CaseInsensitiveMap<T>  (id=471)	
						overlappedPartCols	scala.collection.immutable.Map$EmptyMap$  (id=459)	
						partitionSchema	org.apache.spark.sql.types.StructType  (id=473)	
						schema	org.apache.spark.sql.types.StructType  (id=474)	
						sparkSession	org.apache.spark.sql.SparkSession  (id=139)	
						x$1	scala.Tuple2<T1,T2>  (id=475)	
											
					

					.. filter, partition,bucket ...
		      val scan =
		        FileSourceScanExec(
		          fsRelation,
		          outputAttributes,
		          outputSchema,
		          partitionKeyFilters.toSeq,
		          bucketSet,
		          None,
		          dataFilters,
		          table.map(_.identifier))
		      		<===
		      		FileScan parquet db1.t1[col_str#80,col_int#81] Batched: true, DataFilters: [], Format: Parquet, Location: InMemoryFileIndex[file:/D:/arn/hadoop/rootfs/db1/t1], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<col_str:string,col_int:int>
		
		      val afterScanFilter = afterScanFilters.toSeq.reduceOption(expressions.And)
		      val withFilter = afterScanFilter.map(execution.FilterExec(_, scan)).getOrElse(scan) <== 
		      val withProjections = if (projects == withFilter.output) {
		        withFilter
		      } else {
		        execution.ProjectExec(projects, withFilter)  <============
		      }


	QueryPlanner<PhysicalPlan>.$anonfun$plan$1(LogicalPlan, GenericStrategy) line: 63	

		abstract class QueryPlanner[PhysicalPlan <: TreeNode[PhysicalPlan]] {
		  /** A list of execution strategies that can be used by the planner */
		  def strategies: Seq[GenericStrategy[PhysicalPlan]]
		
		  def plan(plan: LogicalPlan): Iterator[PhysicalPlan] = {
		    // Obviously a lot to do here still...
		
		    // Collect physical plan candidates.
		    val candidates = strategies.iterator.flatMap(_(plan))


	1922475570.apply(Object) line: not available	
	Iterator$$anon$11.nextCur() line: 484	
	Iterator$$anon$11.hasNext() line: 490	
	Iterator$$anon$11.hasNext() line: 489	
	HiveSessionStateBuilder$$anon$2(QueryPlanner<PhysicalPlan>).plan(LogicalPlan) line: 93	
	HiveSessionStateBuilder$$anon$2(SparkStrategies).plan(LogicalPlan) line: 68	

		  override protected def planner: SparkPlanner = {
		    new SparkPlanner(session, conf, experimentalMethods) with HiveStrategies {
		      override val sparkSession: SparkSession = session
		
		      override def extraPlanningStrategies: Seq[Strategy] =
		        super.extraPlanningStrategies ++ customPlanningStrategies ++
		          Seq(HiveTableScans, HiveScripts)
		    }
		  }

	QueryPlanner<PhysicalPlan>.$anonfun$plan$3(QueryPlanner, Iterator, Tuple2) line: 78	
	
		abstract class QueryPlanner[PhysicalPlan <: TreeNode[PhysicalPlan]] {
		  /** A list of execution strategies that can be used by the planner */
		  def strategies: Seq[GenericStrategy[PhysicalPlan]]
		
		  def plan(plan: LogicalPlan): Iterator[PhysicalPlan] = {
		    // Obviously a lot to do here still...
		
		    // Collect physical plan candidates.
		    val candidates = strategies.iterator.flatMap(_(plan))
		
		    // The candidates may contain placeholders marked as [[planLater]],
		    // so try to replace them by their child plans.
		    val plans = candidates.flatMap { candidate =>
		      val placeholders = collectPlaceholders(candidate)
		
		      if (placeholders.isEmpty) {
		        // Take the candidate as is because it does not contain placeholders.
		        Iterator(candidate)
		      } else {
		        // Plan the logical plan marked as [[planLater]] and replace the placeholders.
		        placeholders.iterator.foldLeft(Iterator(candidate)) {  
		          case (candidatesWithPlaceholders, (placeholder, logicalPlan)) =>
		            // Plan the logical plan for the placeholder.
		            val childPlans = this.plan(logicalPlan)   <=============
	
	
	344591852.apply(Object, Object) line: not available	
	TraversableOnce<A>.$anonfun$foldLeft$1(ObjectRef, Function2, Object) line: 162	
	TraversableOnce<A>.$anonfun$foldLeft$1$adapted(ObjectRef, Function2, Object) line: 162	
	2103569237.apply(Object) line: not available	
	IndexedSeqLike$Elements(Iterator<A>).foreach(Function1<A,U>) line: 941	
	Iterator<A>.foreach$(Iterator, Function1) line: 941	
	IndexedSeqLike$Elements(AbstractIterator<A>).foreach(Function1<A,U>) line: 1429	
	IndexedSeqLike$Elements(TraversableOnce<A>).foldLeft(B, Function2<B,A,B>) line: 162	
	TraversableOnce<A>.foldLeft$(TraversableOnce, Object, Function2) line: 160	
	IndexedSeqLike$Elements(AbstractIterator<A>).foldLeft(B, Function2<B,A,B>) line: 1429	
	QueryPlanner<PhysicalPlan>.$anonfun$plan$2(QueryPlanner, TreeNode) line: 75	
	572406623.apply(Object) line: not available	
	Iterator$$anon$11.nextCur() line: 484	
	Iterator$$anon$11.hasNext() line: 490	
	HiveSessionStateBuilder$$anon$2(QueryPlanner<PhysicalPlan>).plan(LogicalPlan) line: 93	
	HiveSessionStateBuilder$$anon$2(SparkStrategies).plan(LogicalPlan) line: 68	
	QueryExecution$.createSparkPlan(SparkSession, SparkPlanner, LogicalPlan) line: 330	

		  /**
		   * Transform a [[LogicalPlan]] into a [[SparkPlan]].
		   *
		   * Note that the returned physical plan still needs to be prepared for execution.
		   */
		  def createSparkPlan(
		      sparkSession: SparkSession,
		      planner: SparkPlanner,
		      plan: LogicalPlan): SparkPlan = {
		    // TODO: We use next(), i.e. take the first plan returned by the planner, here for now,
		    //       but we will implement to choose the best plan.
		    planner.plan(ReturnAnswer(plan)).next()
		  }
  
	QueryExecution.$anonfun$sparkPlan$1(QueryExecution) line: 94	
	627300756.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	823993873.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.sparkPlan$lzycompute() line: 94	
	QueryExecution.sparkPlan() line: 87	
	QueryExecution.$anonfun$executedPlan$1(QueryExecution) line: 107	
		  lazy val executedPlan: SparkPlan = {
		    // We need to materialize the optimizedPlan here, before tracking the planning phase, to ensure
		    // that the optimization time is not counted as part of the planning phase.
		    assertOptimized()
		    executePhase(QueryPlanningTracker.PLANNING) {
		      // clone the plan to avoid sharing the plan instance between different stages like analyzing,
		      // optimizing and planning.
		      QueryExecution.prepareForExecution(preparations, 
		      		sparkPlan         <======
		      			.clone())
		    }
		  }

	2040074572.apply() line: not available	
	QueryPlanningTracker.measurePhase(String, Function0<T>) line: 111	
	QueryExecution.$anonfun$executePhase$1(QueryExecution, String, Function0) line: 133	
	823993873.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	QueryExecution.executePhase(String, Function0<T>) line: 133	
	QueryExecution.executedPlan$lzycompute() line: 107	
	QueryExecution.executedPlan() line: 100	
	QueryExecution.$anonfun$writePlans$5(QueryExecution) line: 199	
	1579489605.apply() line: not available	
	QueryPlan$.append(Function0<QueryPlan<T>>, Function1<String,BoxedUnit>, boolean, boolean, int, boolean) line: 381	
	QueryExecution.org$apache$spark$sql$execution$QueryExecution$$writePlans(Function1<String,BoxedUnit>, int) line: 199	

		  private def writePlans(append: String => Unit, maxFields: Int): Unit = {
		    val (verbose, addSuffix) = (true, false)
		    append("== Parsed Logical Plan ==\n")
		    QueryPlan.append(logical, append, verbose, addSuffix, maxFields)
		    append("\n== Analyzed Logical Plan ==\n")
		    try {
		      append(
		        truncatedString(
		          analyzed.output.map(o => s"${o.name}: ${o.dataType.simpleString}"), ", ", maxFields)
		      )
		      append("\n")
		      QueryPlan.append(analyzed, append, verbose, addSuffix, maxFields)
		      append("\n== Optimized Logical Plan ==\n")
		      QueryPlan.append(optimizedPlan, append, verbose, addSuffix, maxFields)
		      append("\n== Physical Plan ==\n")
		      QueryPlan.append(executedPlan, append, verbose, addSuffix, maxFields)
		    } catch {
		      case e: AnalysisException => append(e.toString)
		    }
		  }

	QueryExecution.toString() line: 207	
	SQLExecution$.$anonfun$withNewExecutionId$5(SparkContext, long, String, CallSite, QueryExecution, Function0, Option) line: 95	
	763619867.apply() line: not available	
	SQLExecution$.withSQLConfPropagated(SparkSession, Function0<T>) line: 160	
	SQLExecution$.$anonfun$withNewExecutionId$1(QueryExecution, Function0, Option) line: 87	
	355159860.apply() line: not available	
	SparkSession.withActive(Function0<T>) line: 764	
	SQLExecution$.withNewExecutionId(QueryExecution, Option<String>, Function0<T>) line: 64	
	Dataset<T>.withAction(String, QueryExecution, Function1<SparkPlan,U>) line: 3616	
	Dataset<T>.head(int) line: 2697	
	Dataset<T>.take(int) line: 2904	
	Dataset<T>.getRows(int, int) line: 300	
	Dataset<T>.showString(int, int, boolean) line: 337	
	Dataset<T>.show(int, boolean) line: 824	
	Dataset<T>.show(int) line: 783	
	Dataset<T>.show() line: 792	


        
package fr.an.tests.spark.sql;

import static fr.an.tests.spark.sql.ScalaToJavaUtils.toJavaList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.analysis.NamedRelation;
import org.apache.spark.sql.catalyst.analysis.RelationTimeTravel;
import org.apache.spark.sql.catalyst.analysis.ResolvedDBObjectName;
import org.apache.spark.sql.catalyst.analysis.ResolvedNamespace;
import org.apache.spark.sql.catalyst.analysis.ResolvedNonPersistentFunc;
import org.apache.spark.sql.catalyst.analysis.ResolvedPersistentFunc;
import org.apache.spark.sql.catalyst.analysis.ResolvedTable;
import org.apache.spark.sql.catalyst.analysis.ResolvedView;
import org.apache.spark.sql.catalyst.analysis.UnresolvedDBObjectName;
import org.apache.spark.sql.catalyst.analysis.UnresolvedFunc;
import org.apache.spark.sql.catalyst.analysis.UnresolvedHaving;
import org.apache.spark.sql.catalyst.analysis.UnresolvedInlineTable;
import org.apache.spark.sql.catalyst.analysis.UnresolvedNamespace;
import org.apache.spark.sql.catalyst.analysis.UnresolvedRelation;
import org.apache.spark.sql.catalyst.analysis.UnresolvedSubqueryColumnAliases;
import org.apache.spark.sql.catalyst.analysis.UnresolvedTable;
import org.apache.spark.sql.catalyst.analysis.UnresolvedTableOrView;
import org.apache.spark.sql.catalyst.analysis.UnresolvedTableValuedFunction;
import org.apache.spark.sql.catalyst.analysis.UnresolvedView;
import org.apache.spark.sql.catalyst.catalog.HiveTableRelation;
import org.apache.spark.sql.catalyst.catalog.TemporaryViewRelation;
import org.apache.spark.sql.catalyst.catalog.UnresolvedCatalogRelation;
import org.apache.spark.sql.catalyst.encoders.DummyExpressionHolder;
import org.apache.spark.sql.catalyst.optimizer.OrderedJoin;
import org.apache.spark.sql.catalyst.plans.logical.Aggregate;
import org.apache.spark.sql.catalyst.plans.logical.AppendColumns;
import org.apache.spark.sql.catalyst.plans.logical.AsOfJoin;
import org.apache.spark.sql.catalyst.plans.logical.AttachDistributedSequence;
import org.apache.spark.sql.catalyst.plans.logical.BinaryNode;
import org.apache.spark.sql.catalyst.plans.logical.CTERelationDef;
import org.apache.spark.sql.catalyst.plans.logical.CTERelationRef;
import org.apache.spark.sql.catalyst.plans.logical.CoGroup;
import org.apache.spark.sql.catalyst.plans.logical.CollectMetrics;
import org.apache.spark.sql.catalyst.plans.logical.CommandResult;
import org.apache.spark.sql.catalyst.plans.logical.Deduplicate;
import org.apache.spark.sql.catalyst.plans.logical.DeserializeToObject;
import org.apache.spark.sql.catalyst.plans.logical.Distinct;
import org.apache.spark.sql.catalyst.plans.logical.DomainJoin;
import org.apache.spark.sql.catalyst.plans.logical.EventTimeWatermark;
import org.apache.spark.sql.catalyst.plans.logical.Except;
import org.apache.spark.sql.catalyst.plans.logical.Expand;
import org.apache.spark.sql.catalyst.plans.logical.Filter;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapCoGroupsInPandas;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInPandas;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInR;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInRWithArrow;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsWithState;
import org.apache.spark.sql.catalyst.plans.logical.Generate;
import org.apache.spark.sql.catalyst.plans.logical.GlobalLimit;
import org.apache.spark.sql.catalyst.plans.logical.InsertIntoDir;
import org.apache.spark.sql.catalyst.plans.logical.InsertIntoStatement;
import org.apache.spark.sql.catalyst.plans.logical.Intersect;
import org.apache.spark.sql.catalyst.plans.logical.Join;
import org.apache.spark.sql.catalyst.plans.logical.LateralJoin;
import org.apache.spark.sql.catalyst.plans.logical.LeafNode;
import org.apache.spark.sql.catalyst.plans.logical.LocalLimit;
import org.apache.spark.sql.catalyst.plans.logical.LocalRelation;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.catalyst.plans.logical.MapGroups;
import org.apache.spark.sql.catalyst.plans.logical.MapInPandas;
import org.apache.spark.sql.catalyst.plans.logical.MapPartitionsInRWithArrow;
import org.apache.spark.sql.catalyst.plans.logical.OneRowRelation;
import org.apache.spark.sql.catalyst.plans.logical.OrderPreservingUnaryNode;
import org.apache.spark.sql.catalyst.plans.logical.ParsedStatement;
import org.apache.spark.sql.catalyst.plans.logical.Pivot;
import org.apache.spark.sql.catalyst.plans.logical.Project;
import org.apache.spark.sql.catalyst.plans.logical.PythonMapInArrow;
import org.apache.spark.sql.catalyst.plans.logical.Range;
import org.apache.spark.sql.catalyst.plans.logical.RebalancePartitions;
import org.apache.spark.sql.catalyst.plans.logical.Repartition;
import org.apache.spark.sql.catalyst.plans.logical.RepartitionByExpression;
import org.apache.spark.sql.catalyst.plans.logical.RepartitionOperation;
import org.apache.spark.sql.catalyst.plans.logical.ResolvedHint;
import org.apache.spark.sql.catalyst.plans.logical.ReturnAnswer;
import org.apache.spark.sql.catalyst.plans.logical.Sample;
import org.apache.spark.sql.catalyst.plans.logical.ScriptTransformation;
import org.apache.spark.sql.catalyst.plans.logical.Subquery;
import org.apache.spark.sql.catalyst.plans.logical.SubqueryAlias;
import org.apache.spark.sql.catalyst.plans.logical.Tail;
import org.apache.spark.sql.catalyst.plans.logical.TypedFilter;
import org.apache.spark.sql.catalyst.plans.logical.UnaryNode;
import org.apache.spark.sql.catalyst.plans.logical.Union;
import org.apache.spark.sql.catalyst.plans.logical.UnresolvedHint;
import org.apache.spark.sql.catalyst.plans.logical.UnresolvedWith;
import org.apache.spark.sql.catalyst.plans.logical.View;
import org.apache.spark.sql.catalyst.plans.logical.Window;
import org.apache.spark.sql.catalyst.plans.logical.WithCTE;
import org.apache.spark.sql.catalyst.plans.logical.WithWindowDefinition;
import org.apache.spark.sql.catalyst.streaming.StreamingRelationV2;
import org.apache.spark.sql.catalyst.streaming.WriteToStream;
import org.apache.spark.sql.catalyst.streaming.WriteToStreamStatement;
import org.apache.spark.sql.execution.LogicalRDD;
import org.apache.spark.sql.execution.adaptive.LogicalQueryStage;
import org.apache.spark.sql.execution.datasources.CreateTable;
import org.apache.spark.sql.execution.datasources.LogicalRelation;
import org.apache.spark.sql.execution.datasources.v2.DataSourceV2Relation;
import org.apache.spark.sql.execution.datasources.v2.DataSourceV2ScanRelation;
import org.apache.spark.sql.execution.datasources.v2.ScanBuilderHolder;
import org.apache.spark.sql.execution.datasources.v2.StreamingDataSourceV2Relation;
import org.apache.spark.sql.execution.datasources.v2.WriteToDataSourceV2;
import org.apache.spark.sql.execution.streaming.OffsetHolder;
import org.apache.spark.sql.execution.streaming.StreamingExecutionRelation;
import org.apache.spark.sql.execution.streaming.StreamingRelation;
import org.apache.spark.sql.execution.streaming.continuous.WriteToContinuousDataSource;
import org.apache.spark.sql.execution.streaming.sources.MemoryPlan;
import org.apache.spark.sql.execution.streaming.sources.WriteToMicroBatchDataSource;

import lombok.val;

public class SparkSqlAstSummaryPrinter<Void> extends DefaultLogicalPlanExtVisitor<Void> {

	private PrintStream out;
	
	private String indent = "  ";
	private int indentLevel = 0;

	// ------------------------------------------------------------------------
	
	public SparkSqlAstSummaryPrinter(PrintStream out) {
		this.out = out;
	}

	public static String toSummary(LogicalPlan ast) {
		val bufferOut = new ByteArrayOutputStream();
		val out = new PrintStream(bufferOut);
		val visitor = new SparkSqlAstSummaryPrinter(out);
		visitor.accept(ast);
		return bufferOut.toString();
	}


	// ------------------------------------------------------------------------

	protected void incr() {
		this.indentLevel++;
	}
	protected void decr() {
		this.indentLevel--;
	}
	protected void indent() {
		for(int i = 0; i < indentLevel; i++) {
			out.print(indent);
		}
	}
	protected void indentPrintln(String line) {
		indent();
		out.print(line);
		out.print("\n");
	}
	protected void indentPrint(String text) {
		indent();
		out.print(text);
	}
	protected void print(String text) {
		out.print(text);
	}
	protected void println() {
		out.print("\n");
	}
	protected Void unknown(LogicalPlan p) {
		print("/* ? " + p.getClass().getSimpleName() + " */");
		return null;
	}
	protected Void unknown2(Object p) {
		print("/* ? " + p.getClass().getSimpleName() + " */");
		return null;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public Void caseLogicalPlan(LogicalPlan p) {
		return unknown(p);
	}

	/**
	 * (interface)
	 */
	@Override
	public Void caseNamedRelation(NamedRelation p) {
		print(p.name());
		return null;
	}

	@Override
	public Void caseUnion(Union p) {
		indent(); //?
		val children = ScalaToJavaUtils.toJavaList(p.children());
		int size = children.size();
		for(int i = 0; i < size; i++) {
			accept(children.get(i));
			if (i + 1 < size) {
				println();
				print("UNION ");
			}
		}
		return null;
	}

	@Override
	public Void caseWithCTE(WithCTE p) {
		val cteDefs = ScalaToJavaUtils.toJavaList(p.cteDefs());
		val plan = p.plan();
		val cteDefsCount = cteDefs.size();
		for(int i = 0; i < cteDefsCount; i++) {
			val cteDef = cteDefs.get(i);
			indentPrint("WITH ");
			
		}
		return null;
	}

	@Override
	public Void caseLeafNode(LeafNode p) {
		return unknown2(p);
	}

	@Override
	public Void caseUnaryNode(UnaryNode p) {
		return unknown2(p);
	}

	@Override
	public Void caseBinaryNode(BinaryNode p) {
		return unknown2(p);
	}

	@Override
	public Void caseParsedStatement(ParsedStatement p) {
		return unknown(p);
	}

	@Override
	public Void caseInsertIntoStatement(InsertIntoStatement p) {
	    val table = p.table();
	    val partitionSpec = p.partitionSpec(); //: Map[String, Option[String]],
	    // userSpecifiedCols: Seq[String],
	    val query = p.query();
	    val overwrite = p.overwrite();
	    val ifPartitionNotExists = p.ifPartitionNotExists();
		print("INSERT " + ((overwrite)? "OVERWRITE " : "INTO "));
		accept(table);
		print(" ");
		accept(query);
	    return null;
	}

	@Override
	public Void caseCreateTable(CreateTable p) {
		val tableDesc = p.tableDesc();
		// val mode = p.mode();
		val query = p.query();
		print("CREATE TABLE ");
		val tableId = tableDesc.identifier();
		print(tableId.identifier());
		print(" ");
		acceptOpt(query);
		return null;
	}

	// sub-classes of LeafNode
	// ------------------------------------------------------------------------

	@Override
	public Void caseRelationTimeTravel(RelationTimeTravel p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedRelation(UnresolvedRelation p) {
		val multipartIdentifier = toJavaList(p.multipartIdentifier());
		// print(mapStrJoin(multipartIdentifier, x -> x., ".");
		print(String.join(".", multipartIdentifier));
		return null;
	}

	@Override
	public Void caseUnresolvedInlineTable(UnresolvedInlineTable p) {
		val names = toJavaList(p.names());
		// rows: Seq[Seq[Expression]]
		print(String.join(".", names));
		return null;
	}

	@Override
	public Void caseUnresolvedTableValuedFunction(UnresolvedTableValuedFunction p) {
		FunctionIdentifier name = p.name(); // FunctionIdentifier,
	    // val functionArgs = toJavaList(p.functionArgs());
	    // val outputNames = toJavaList(p.outputNames());
	    val database = name.database();
	    if (database.isDefined()) {
	    	print(database.get() + ".");
	    }
	    print(name.funcName());
		return null;
	}

	@Override
	public Void caseUnresolvedNamespace(UnresolvedNamespace p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedTable(UnresolvedTable p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedView(UnresolvedView p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedTableOrView(UnresolvedTableOrView p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedFunc(UnresolvedFunc p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedDBObjectName(UnresolvedDBObjectName p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedNamespace(ResolvedNamespace p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedTable(ResolvedTable p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedView(ResolvedView p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedPersistentFunc(ResolvedPersistentFunc p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedNonPersistentFunc(ResolvedNonPersistentFunc p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedDBObjectName(ResolvedDBObjectName p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedCatalogRelation(UnresolvedCatalogRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseTemporaryViewRelation(TemporaryViewRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseHiveTableRelation(HiveTableRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseDummyExpressionHolder(DummyExpressionHolder p) {
		return unknown(p);
	}

	@Override
	public Void caseCTERelationRef(CTERelationRef p) {
		return unknown(p);
	}

	@Override
	public Void caseRange(Range p) {
		return unknown(p);
	}

	@Override
	public Void caseOneRowRelation(OneRowRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseLocalRelation(LocalRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseStreamingRelationV2(StreamingRelationV2 p) {
		return unknown(p);
	}

	@Override
	public Void caseDataSourceV2Relation(DataSourceV2Relation p) {
		return unknown(p);
	}

	@Override
	public Void caseDataSourceV2ScanRelation(DataSourceV2ScanRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseStreamingDataSourceV2Relation(StreamingDataSourceV2Relation p) {
		return unknown(p);
	}

	@Override
	public Void caseCommandResult(CommandResult p) {
		return unknown(p);
	}

	@Override
	public Void caseLogicalQueryStage(LogicalQueryStage p) {
		return unknown(p);
	}

	@Override
	public Void caseLogicalRelation(LogicalRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseScanBuilderHolder(ScanBuilderHolder p) {
		return unknown(p);
	}

	@Override
	public Void caseLogicalRDD(LogicalRDD p) {
		return unknown(p);
	}

	@Override
	public Void caseOffsetHolder(OffsetHolder p) {
		return unknown(p);
	}

	@Override
	public Void caseMemoryPlan(MemoryPlan p) {
		return unknown(p);
	}

	@Override
	public Void caseStreamingRelation(StreamingRelation p) {
		return unknown(p);
	}

	@Override
	public Void caseStreamingExecutionRelation(StreamingExecutionRelation p) {
		return unknown(p);
	}

	// UnaryNode sub-classes
	// ------------------------------------------------------------------------

	// import??
//	@Override public Void caseUnresolvedTVFAliases(UnresolvedTVFAliases p) {
//		return unknown(p);
//	}
	@Override
	public Void caseUnresolvedSubqueryColumnAliases(UnresolvedSubqueryColumnAliases p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedHaving(UnresolvedHaving p) {
		return unknown(p);
	}

	@Override
	public Void caseReturnAnswer(ReturnAnswer p) {
		return unknown(p);
	}

	@Override
	public Void caseGenerate(Generate p) {
		return unknown(p);
	}

	@Override
	public Void caseInsertIntoDir(InsertIntoDir p) {
		return unknown(p);
	}

	@Override
	public Void caseView(View p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedWith(UnresolvedWith p) {
		val child = p.child();
	    val cteRelations = toJavaList(p.cteRelations()); // [(String, SubqueryAlias)]
	    for(val cteRelation: cteRelations) {
	    	print("WITH " + cteRelation._1 + " AS (");
	    	accept(cteRelation._2);
	    	print(") ");
	    }
	    accept(child);
		return null;
	}

	@Override
	public Void caseCTERelationDef(CTERelationDef p) {
		val child = p.child();
	    // id: Long = CTERelationDef.newId,
	    val originalPlanWithPredicates = p.originalPlanWithPredicates(); // : Option[(LogicalPlan, Seq[Expression])] = None,
	    // underSubquery: Boolean = false
		return unknown(p);
	}

	@Override
	public Void caseWithWindowDefinition(WithWindowDefinition p) {
		return unknown(p);
	}

	@Override
	public Void caseAggregate(Aggregate p) {
		return unknown(p);
	}

	@Override
	public Void caseWindow(Window p) {
		return unknown(p);
	}

	@Override
	public Void caseExpand(Expand p) {
		return unknown(p);
	}

	@Override
	public Void casePivot(Pivot p) {
		return unknown(p);
	}

	// import??
//	@Override public Void caseUnpivot(Unpivot p) {
//		return unknown(p);
//	}

	@Override
	public Void caseGlobalLimit(GlobalLimit p) {
		return unknown(p);
	}

	@Override
	public Void caseSample(Sample p) {
		return unknown(p);
	}

	@Override
	public Void caseDistinct(Distinct p) {
		return unknown(p);
	}

	/**
	 * direct-sub-classes:
	 * </PRE>
	 * Repartition RepartitionByExpression
	 * </PRE>
	 */
	@Override
	public Void caseRepartitionOperation(RepartitionOperation p) {
		return unknown(p);
	}

	@Override
	public Void caseRepartition(Repartition p) {
		return unknown(p);
	}

	@Override
	public Void caseRepartitionByExpression(RepartitionByExpression p) {
		return unknown(p);
	}

	@Override
	public Void caseRebalancePartitions(RebalancePartitions p) {
		return unknown(p);
	}

	@Override
	public Void caseDeduplicate(Deduplicate p) {
		return unknown(p);
	}

	@Override
	public Void caseCollectMetrics(CollectMetrics p) {
		return unknown(p);
	}

	@Override
	public Void caseDomainJoin(DomainJoin p) {
		return unknown(p);
	}

	@Override
	public Void caseLateralJoin(LateralJoin p) {
		return unknown(p);
	}

	@Override
	public Void caseEventTimeWatermark(EventTimeWatermark p) {
		return unknown(p);
	}

	@Override
	public Void caseUnresolvedHint(UnresolvedHint p) {
		return unknown(p);
	}

	@Override
	public Void caseResolvedHint(ResolvedHint p) {
		return unknown(p);
	}

	/**
	 * TODO direct-sub-classes:
	 * </PRE>
	 * Filter LocalLimit Project Subquery SubqueryAlias Tail
	 * </PRE>
	 */
	@Override
	public Void caseOrderPreservingUnaryNode(OrderPreservingUnaryNode p) {
		return unknown(p);
	}

	@Override
	public Void caseFilter(Filter p) {
		val condition = p.condition();
		val child = p.child();
		accept(child);
		print(" WHERE /*..*/");
		return null;
	}

	@Override
	public Void caseLocalLimit(LocalLimit p) {
		return unknown(p);
	}

	@Override
	public Void caseProject(Project p) {
		val projectList = toJavaList(p.projectList());
		val child = p.child();
		print("SELECT /*" + projectList.size() + "*/ FROM ");
		accept(child);
		return null;
	}

	@Override
	public Void caseSubquery(Subquery p) {
		return unknown(p);
	}

	@Override
	public Void caseSubqueryAlias(SubqueryAlias p) {
		val identifier = p.identifier();
		val idName = identifier.name();
		val idQual = toJavaList(identifier.qualifier());
	    val child = p.child();
	    accept(child);
	    print(" ");
	    if (! idQual.isEmpty()) {
	    	print(String.join(".", idQual));
	    	print(".");
	    }
	    print(idName);
	    return null;
	}

	@Override
	public Void caseTail(Tail p) {
		return unknown(p);
	}

	@Override
	public Void caseDeserializeToObject(DeserializeToObject p) {
		return unknown(p);
	}

	@Override
	public Void caseMapPartitionsInRWithArrow(MapPartitionsInRWithArrow p) {
		return unknown(p);
	}

	@Override
	public Void caseTypedFilter(TypedFilter p) {
		return unknown(p);
	}

	@Override
	public Void caseAppendColumns(AppendColumns p) {
		return unknown(p);
	}

	@Override
	public Void caseMapGroups(MapGroups p) {
		return unknown(p);
	}

	@Override
	public Void caseFlatMapGroupsInR(FlatMapGroupsInR p) {
		return unknown(p);
	}

	@Override
	public Void caseFlatMapGroupsInRWithArrow(FlatMapGroupsInRWithArrow p) {
		return unknown(p);
	}

	@Override
	public Void caseFlatMapGroupsInPandas(FlatMapGroupsInPandas p) {
		return unknown(p);
	}

	@Override
	public Void caseMapInPandas(MapInPandas p) {
		return unknown(p);
	}

	@Override
	public Void casePythonMapInArrow(PythonMapInArrow p) {
		return unknown(p);
	}

//	// import?
//	@Override public Void caseFlatMapGroupsInPandasWithState(FlatMapGroupsInPandasWithState p) {
//		return unknown(p);
//	}

	@Override
	public Void caseAttachDistributedSequence(AttachDistributedSequence p) {
		return unknown(p);
	}

	@Override
	public Void caseScriptTransformation(ScriptTransformation p) {
		return unknown(p);
	}

	@Override
	public Void caseWriteToStream(WriteToStream p) {
		return unknown(p);
	}

	@Override
	public Void caseWriteToStreamStatement(WriteToStreamStatement p) {
		return unknown(p);
	}

	// @deprecated
	@Override
	public Void caseWriteToDataSourceV2(WriteToDataSourceV2 p) {
		return unknown(p);
	}

	// import?
//	@Override public Void caseWriteFiles(WriteFiles p) {
//		return unknown(p);
//	}

	@Override
	public Void caseWriteToContinuousDataSource(WriteToContinuousDataSource p) {
		return unknown(p);
	}

	@Override
	public Void caseWriteToMicroBatchDataSource(WriteToMicroBatchDataSource p) {
		return unknown(p);
	}

	// import?
//	@Override public Void caseWriteToMicroBatchDataSourceV1(WriteToMicroBatchDataSourceV1 p) {
//		return unknown(p);
//	}

	// BinaryNode sub-classes
	// ------------------------------------------------------------------------

	@Override
	public Void caseOrderedJoin(OrderedJoin p) {
		return unknown(p);
	}

	@Override
	public Void caseExcept(Except p) {
		return unknown(p);
	}

	@Override
	public Void caseIntersect(Intersect p) {
		return unknown(p);
	}

	@Override
	public Void caseJoin(Join p) {
		val left = p.left();
	    val right = p.right();
	    // joinType: JoinType,
	    // condition: Option[Expression],
	    // hint: JoinHint
	    accept(left);
	    print(" JOIN ");
	    accept(right);
	    print(" /*..*/");
		return null;
	}

	@Override
	public Void caseAsOfJoin(AsOfJoin p) {
		return unknown(p);
	}

	@Override
	public Void caseFlatMapGroupsWithState(FlatMapGroupsWithState p) {
		return unknown(p);
	}

	@Override
	public Void caseCoGroup(CoGroup p) {
		return unknown(p);
	}

	@Override
	public Void caseFlatMapCoGroupsInPandas(FlatMapCoGroupsInPandas p) {
		return unknown(p);
	}

}

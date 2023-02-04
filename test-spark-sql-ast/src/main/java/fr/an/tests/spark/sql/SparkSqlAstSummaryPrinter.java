package fr.an.tests.spark.sql;

import java.io.PrintStream;

import org.apache.spark.sql.catalyst.analysis.LeafNodeWithoutStats;
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
import org.apache.spark.sql.catalyst.plans.logical.BaseEvalPython;
import org.apache.spark.sql.catalyst.plans.logical.BinaryNode;
import org.apache.spark.sql.catalyst.plans.logical.CTERelationDef;
import org.apache.spark.sql.catalyst.plans.logical.CTERelationRef;
import org.apache.spark.sql.catalyst.plans.logical.CoGroup;
import org.apache.spark.sql.catalyst.plans.logical.CollectMetrics;
import org.apache.spark.sql.catalyst.plans.logical.Command;
import org.apache.spark.sql.catalyst.plans.logical.CommandResult;
import org.apache.spark.sql.catalyst.plans.logical.Deduplicate;
import org.apache.spark.sql.catalyst.plans.logical.DeserializeToObject;
import org.apache.spark.sql.catalyst.plans.logical.Distinct;
import org.apache.spark.sql.catalyst.plans.logical.DomainJoin;
import org.apache.spark.sql.catalyst.plans.logical.EventTimeWatermark;
import org.apache.spark.sql.catalyst.plans.logical.Except;
import org.apache.spark.sql.catalyst.plans.logical.Expand;
import org.apache.spark.sql.catalyst.plans.logical.ExposesMetadataColumns;
import org.apache.spark.sql.catalyst.plans.logical.Filter;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapCoGroupsInPandas;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInPandas;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInR;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsInRWithArrow;
import org.apache.spark.sql.catalyst.plans.logical.FlatMapGroupsWithState;
import org.apache.spark.sql.catalyst.plans.logical.Generate;
import org.apache.spark.sql.catalyst.plans.logical.GlobalLimit;
import org.apache.spark.sql.catalyst.plans.logical.IgnoreCachedData;
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
import org.apache.spark.sql.catalyst.plans.logical.ObjectConsumer;
import org.apache.spark.sql.catalyst.plans.logical.ObjectProducer;
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
import org.apache.spark.sql.catalyst.plans.logical.SetOperation;
import org.apache.spark.sql.catalyst.plans.logical.Subquery;
import org.apache.spark.sql.catalyst.plans.logical.SubqueryAlias;
import org.apache.spark.sql.catalyst.plans.logical.SupportsSubquery;
import org.apache.spark.sql.catalyst.plans.logical.Tail;
import org.apache.spark.sql.catalyst.plans.logical.TypedFilter;
import org.apache.spark.sql.catalyst.plans.logical.UnaryNode;
import org.apache.spark.sql.catalyst.plans.logical.Union;
import org.apache.spark.sql.catalyst.plans.logical.UnresolvedHint;
import org.apache.spark.sql.catalyst.plans.logical.UnresolvedWith;
import org.apache.spark.sql.catalyst.plans.logical.V2CreateTablePlan;
import org.apache.spark.sql.catalyst.plans.logical.View;
import org.apache.spark.sql.catalyst.plans.logical.Window;
import org.apache.spark.sql.catalyst.plans.logical.WithCTE;
import org.apache.spark.sql.catalyst.plans.logical.WithWindowDefinition;
import org.apache.spark.sql.catalyst.streaming.StreamingRelationV2;
import org.apache.spark.sql.catalyst.streaming.WriteToStream;
import org.apache.spark.sql.catalyst.streaming.WriteToStreamStatement;
import org.apache.spark.sql.execution.ExternalRDD;
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

public class SparkSqlAstSummaryPrinter<T> extends DefaultLogicalPlanExtVisitor<T> {

	private PrintStream out;
	
	private String indent = "  ";
	private int indentLevel = 0;

	// ------------------------------------------------------------------------
	
	public SparkSqlAstSummaryPrinter(PrintStream out) {
		this.out = out;
	}
	
	// ------------------------------------------------------------------------

	protected void incr() {
		this.indentLevel++;
	}
	protected void decr() {
		this.indentLevel--;
	}
	protected void printIndent() {
		for(int i = 0; i < indentLevel; i++) {
			out.print(indent);
		}
	}
	protected void printlnIndent(String line) {
		printIndent();
		out.print(line);
		out.print("\n");
	}
	
	// ------------------------------------------------------------------------
	
	TODO 
	
	@Override
	public T caseLogicalPlan(LogicalPlan p) {
		return null;
	}

	/**
	 * (interface)
	 */
	@Override
	public T caseNamedRelation(NamedRelation p) {
		return null;
	}

	@Override
	public T caseUnion(Union p) {
		return caseLogicalPlan(p);
	}

	@Override
	public T caseWithCTE(WithCTE p) {
		return caseLogicalPlan(p);
	}

	/**
	 * (interface)
	 */
	@Override
	public T caseSupportsSubquery(SupportsSubquery p) {
		return null;
	}

	/**
	 * (interface)
	 */
	@Override
	public T caseCommand(Command p) {
		return null;
	}

	/**
	 * (interface)
	 */
	@Override
	public T caseIgnoreCachedData(IgnoreCachedData p) {
		return null;
	}

	@Override
	public T caseLeafNode(LeafNode p) {
		return null;
	}

	@Override
	public T caseUnaryNode(UnaryNode p) {
		return null;
	}

	@Override
	public T caseBinaryNode(BinaryNode p) {
		return null;
	}

	/** interface */
	@Override
	public T caseExposesMetadataColumns(ExposesMetadataColumns p) {
		return null;
	}

	/** interface */
	@Override
	public T caseObjectProducer(ObjectProducer p) {
		return null;
	}

	@Override
	public T caseParsedStatement(ParsedStatement p) {
		return caseLogicalPlan(p);
	}

	@Override
	public T caseInsertIntoStatement(InsertIntoStatement p) {
		return caseParsedStatement(p);
	}

	/** interface */
	@Override
	public T caseV2CreateTablePlan(V2CreateTablePlan p) {
		return null;
	}

	@Override
	public T caseCreateTable(CreateTable p) {
		return null;
	}

	// sub-classes of LeafNode
	// ------------------------------------------------------------------------

	@Override
	public T caseRelationTimeTravel(RelationTimeTravel p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedRelation(UnresolvedRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedInlineTable(UnresolvedInlineTable p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedTableValuedFunction(UnresolvedTableValuedFunction p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedNamespace(UnresolvedNamespace p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedTable(UnresolvedTable p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedView(UnresolvedView p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedTableOrView(UnresolvedTableOrView p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedFunc(UnresolvedFunc p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedDBObjectName(UnresolvedDBObjectName p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseLeafNodeWithoutStats(LeafNodeWithoutStats p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedNamespace(ResolvedNamespace p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedTable(ResolvedTable p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedView(ResolvedView p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedPersistentFunc(ResolvedPersistentFunc p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedNonPersistentFunc(ResolvedNonPersistentFunc p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseResolvedDBObjectName(ResolvedDBObjectName p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseUnresolvedCatalogRelation(UnresolvedCatalogRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseTemporaryViewRelation(TemporaryViewRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseHiveTableRelation(HiveTableRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseDummyExpressionHolder(DummyExpressionHolder p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseCTERelationRef(CTERelationRef p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseRange(Range p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseOneRowRelation(OneRowRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseLocalRelation(LocalRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseStreamingRelationV2(StreamingRelationV2 p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseDataSourceV2Relation(DataSourceV2Relation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseDataSourceV2ScanRelation(DataSourceV2ScanRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseStreamingDataSourceV2Relation(StreamingDataSourceV2Relation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseCommandResult(CommandResult p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseLogicalQueryStage(LogicalQueryStage p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseLogicalRelation(LogicalRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseScanBuilderHolder(ScanBuilderHolder p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseExternalRDD(ExternalRDD p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseLogicalRDD(LogicalRDD p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseOffsetHolder(OffsetHolder p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseMemoryPlan(MemoryPlan p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseStreamingRelation(StreamingRelation p) {
		return caseLeafNode(p);
	}

	@Override
	public T caseStreamingExecutionRelation(StreamingExecutionRelation p) {
		return caseLeafNode(p);
	}

	// UnaryNode sub-classes
	// ------------------------------------------------------------------------

	// import??
//	@Override public T caseUnresolvedTVFAliases(UnresolvedTVFAliases p) {
//		return caseUnaryNode(p);
//	}
	@Override
	public T caseUnresolvedSubqueryColumnAliases(UnresolvedSubqueryColumnAliases p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedHaving(UnresolvedHaving p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseReturnAnswer(ReturnAnswer p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseGenerate(Generate p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseInsertIntoDir(InsertIntoDir p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseView(View p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedWith(UnresolvedWith p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseCTERelationDef(CTERelationDef p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseWithWindowDefinition(WithWindowDefinition p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseAggregate(Aggregate p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseWindow(Window p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseExpand(Expand p) {
		return caseUnaryNode(p);
	}

	@Override
	public T casePivot(Pivot p) {
		return caseUnaryNode(p);
	}

	// import??
//	@Override public T caseUnpivot(Unpivot p) {
//		return caseUnaryNode(p);
//	}

	@Override
	public T caseGlobalLimit(GlobalLimit p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseSample(Sample p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseDistinct(Distinct p) {
		return caseUnaryNode(p);
	}

	/**
	 * direct-sub-classes:
	 * </PRE>
	 * Repartition RepartitionByExpression
	 * </PRE>
	 */
	@Override
	public T caseRepartitionOperation(RepartitionOperation p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseRepartition(Repartition p) {
		return caseRepartitionOperation(p);
	}

	@Override
	public T caseRepartitionByExpression(RepartitionByExpression p) {
		return caseRepartitionOperation(p);
	}

	@Override
	public T caseRebalancePartitions(RebalancePartitions p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseDeduplicate(Deduplicate p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseCollectMetrics(CollectMetrics p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseDomainJoin(DomainJoin p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseLateralJoin(LateralJoin p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseEventTimeWatermark(EventTimeWatermark p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedHint(UnresolvedHint p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseResolvedHint(ResolvedHint p) {
		return caseUnaryNode(p);
	}

	/**
	 * TODO direct-sub-classes:
	 * </PRE>
	 * Filter LocalLimit Project Subquery SubqueryAlias Tail
	 * </PRE>
	 */
	@Override
	public T caseOrderPreservingUnaryNode(OrderPreservingUnaryNode p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseFilter(Filter p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseLocalLimit(LocalLimit p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseProject(Project p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseSubquery(Subquery p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseSubqueryAlias(SubqueryAlias p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseTail(Tail p) {
		return caseOrderPreservingUnaryNode(p);
	}

	@Override
	public T caseObjectConsumer(ObjectConsumer p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseDeserializeToObject(DeserializeToObject p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapPartitionsInRWithArrow(MapPartitionsInRWithArrow p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseTypedFilter(TypedFilter p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseAppendColumns(AppendColumns p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapGroups(MapGroups p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInR(FlatMapGroupsInR p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInRWithArrow(FlatMapGroupsInRWithArrow p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInPandas(FlatMapGroupsInPandas p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapInPandas(MapInPandas p) {
		return caseUnaryNode(p);
	}

	@Override
	public T casePythonMapInArrow(PythonMapInArrow p) {
		return caseUnaryNode(p);
	}

//	// import?
//	@Override public T caseFlatMapGroupsInPandasWithState(FlatMapGroupsInPandasWithState p) {
//		return caseUnaryNode(p);
//	}

	@Override
	public T caseBaseEvalPython(BaseEvalPython p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseAttachDistributedSequence(AttachDistributedSequence p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseScriptTransformation(ScriptTransformation p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseWriteToStream(WriteToStream p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseWriteToStreamStatement(WriteToStreamStatement p) {
		return caseUnaryNode(p);
	}

	// @deprecated
	@Override
	public T caseWriteToDataSourceV2(WriteToDataSourceV2 p) {
		return caseUnaryNode(p);
	}

	// import?
//	@Override public T caseWriteFiles(WriteFiles p) {
//		return caseUnaryNode(p);
//	}

	@Override
	public T caseWriteToContinuousDataSource(WriteToContinuousDataSource p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseWriteToMicroBatchDataSource(WriteToMicroBatchDataSource p) {
		return caseUnaryNode(p);
	}

	// import?
//	@Override public T caseWriteToMicroBatchDataSourceV1(WriteToMicroBatchDataSourceV1 p) {
//		return caseUnaryNode(p);
//	}

	// BinaryNode sub-classes
	// ------------------------------------------------------------------------

	@Override
	public T caseOrderedJoin(OrderedJoin p) {
		return caseBinaryNode(p);
	}

	/**
	 * abstract, sub-classes:
	 * 
	 * <PRE>
	 * Except
	 * Intersect
	 * </PRE>
	 */
	@Override
	public T caseSetOperation(SetOperation p) {
		return caseBinaryNode(p);
	}

	@Override
	public T caseExcept(Except p) {
		return caseSetOperation(p);
	}

	@Override
	public T caseIntersect(Intersect p) {
		return caseSetOperation(p);
	}

	@Override
	public T caseJoin(Join p) {
		return caseBinaryNode(p);
	}

	@Override
	public T caseAsOfJoin(AsOfJoin p) {
		return caseBinaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsWithState(FlatMapGroupsWithState p) {
		return caseBinaryNode(p);
	}

	@Override
	public T caseCoGroup(CoGroup p) {
		return caseBinaryNode(p);
	}

	@Override
	public T caseFlatMapCoGroupsInPandas(FlatMapCoGroupsInPandas p) {
		return caseBinaryNode(p);
	}

}

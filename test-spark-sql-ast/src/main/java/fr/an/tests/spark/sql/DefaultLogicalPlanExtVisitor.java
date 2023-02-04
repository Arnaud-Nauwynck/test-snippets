package fr.an.tests.spark.sql;

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

public abstract class DefaultLogicalPlanExtVisitor<T> extends LogicalPlanExtVisitor<T> {
	
	/**
	 * direct sub-clasess of LogicalPlan:
	 * <PRE>
	 * $ grep -R "extends LogicalPlan"
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/NamedRelation.scala:trait NamedRelation extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    allowMissingCol: Boolean = false) extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:case class WithCTE(plan: LogicalPlan, cteDefs: Seq[CTERelationDef]) extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:trait SupportsSubquery extends LogicalPlan
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/Command.scala:trait Command extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/DistinctKeyVisitor.scala:object DistinctKeyVisitor extends LogicalPlanVisitor[Set[ExpressionSet]] {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/IgnoreCachedData.scala:trait IgnoreCachedData extends LogicalPlan {}
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LogicalPlan.scala:trait LeafNode extends LogicalPlan with LeafLike[LogicalPlan] {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LogicalPlan.scala:trait UnaryNode extends LogicalPlan with UnaryLike[LogicalPlan] {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LogicalPlan.scala:trait BinaryNode extends LogicalPlan with BinaryLike[LogicalPlan]
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LogicalPlan.scala:trait ExposesMetadataColumns extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:trait ObjectProducer extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/statements.scala:abstract class ParsedStatement extends LogicalPlan {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/statsEstimation/BasicStatsPlanVisitor.scala:object BasicStatsPlanVisitor extends LogicalPlanVisitor[Statistics] {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/statsEstimation/SizeInBytesOnlyStatsPlanVisitor.scala:object SizeInBytesOnlyStatsPlanVisitor extends LogicalPlanVisitor[Statistics] {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/v2Commands.scala:trait V2CreateTablePlan extends LogicalPlan {
     * core/src/main/scala/org/apache/spark/sql/execution/datasources/ddl.scala:    query: Option[LogicalPlan]) extends LogicalPlan {
     * 
     * </PRE>
	 */
	public T caseLogicalPlan(LogicalPlan p) {
		return null;
	}

	/**
	 * (interface)
	 */
	public T caseNamedRelation(NamedRelation p) {
		return null;
	}

	public T caseUnion(Union p) {
		return caseLogicalPlan(p);
	}

	public T caseWithCTE(WithCTE p) {
		return caseLogicalPlan(p);
	}

	/**
	 * (interface)
	 */
	public T caseSupportsSubquery(SupportsSubquery p) {
		return null;
	}

	/**
	 * (interface)
	 */
	public T caseCommand(Command p) {
		return null;
	}

	/**
	 * (interface)
	 */
	public T caseIgnoreCachedData(IgnoreCachedData p) {
		return null;
	}

	/**
	 * direct sub-classes:
	 * <PRE>
	 * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/RelationTimeTravel.scala:    version: Option[String]) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends LeafNode with NamedRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:case class UnresolvedNamespace(multipartIdentifier: Seq[String]) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    relationTypeMismatchHint: Option[String]) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    relationTypeMismatchHint: Option[String]) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    allowTempView: Boolean) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    possibleQualifiedName: Option[Seq[String]] = None) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:trait LeafNodeWithoutStats extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    viewSchema: StructType) extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:  extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/v2ResolutionPlans.scala:    identifier: Identifier) extends LeafNodeWithoutStats {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/catalog/interface.scala:    override val isStreaming: Boolean = false) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/catalog/interface.scala:    plan: Option[LogicalPlan] = None) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/catalog/interface.scala:  extends LeafNode with MultiInstanceRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/encoders/ExpressionEncoder.scala:case class DummyExpressionHolder(exprs: Seq[Expression]) extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    statsOpt: Option[Statistics] = None) extends LeafNode with MultiInstanceRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends LeafNode with MultiInstanceRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:case class OneRowRelation() extends LeafNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LocalRelation.scala:  extends LeafNode with analysis.MultiInstanceRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/streaming/StreamingRelationV2.scala:  extends LeafNode with MultiInstanceRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/execution/datasources/v2/DataSourceV2Relation.scala:  extends LeafNode with MultiInstanceRelation with NamedRelation with ExposesMetadataColumns {
     * catalyst/src/main/scala/org/apache/spark/sql/execution/datasources/v2/DataSourceV2Relation.scala:    ordering: Option[Seq[SortOrder]] = None) extends LeafNode with NamedRelation {
     * catalyst/src/main/scala/org/apache/spark/sql/execution/datasources/v2/DataSourceV2Relation.scala:  extends LeafNode with MultiInstanceRelation {
     * core/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/CommandResult.scala:    @transient rows: Seq[InternalRow]) extends LeafNode {
     * core/src/main/scala/org/apache/spark/sql/execution/adaptive/LogicalQueryStage.scala:    physicalPlan: SparkPlan) extends LeafNode {
     * core/src/main/scala/org/apache/spark/sql/execution/datasources/LogicalRelation.scala:  extends LeafNode with MultiInstanceRelation with ExposesMetadataColumns {
     * core/src/main/scala/org/apache/spark/sql/execution/datasources/v2/V2ScanRelationPushDown.scala:    builder: ScanBuilder) extends LeafNode {
     * core/src/main/scala/org/apache/spark/sql/execution/ExistingRDD.scala:  extends LeafNode with ObjectProducer with MultiInstanceRelation {
     * core/src/main/scala/org/apache/spark/sql/execution/ExistingRDD.scala:  extends LeafNode with MultiInstanceRelation {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/MicroBatchExecution.scala:case class OffsetHolder(start: OffsetV2, end: OffsetV2) extends LeafNode {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/sources/memory.scala:case class MemoryPlan(sink: MemorySink, override val output: Seq[Attribute]) extends LeafNode {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/StreamingRelation.scala:  extends LeafNode with MultiInstanceRelation with ExposesMetadataColumns {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/StreamingRelation.scala:  extends LeafNode with MultiInstanceRelation {
	 * </PRE>
	 */
	public T caseLeafNode(LeafNode p) {
		return null;
	}

	/**
	 * direct sub-classes:
	 * <PRE>
	 * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:    outputNames: Seq[String]) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:case class ReturnAnswer(child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    cteRelations: Seq[(String, SubqueryAlias)]) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    underSubquery: Boolean = false) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:case class GlobalLimit(limitExpr: Expression, child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:case class Distinct(child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:abstract class RepartitionOperation extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    optNumPartitions: Option[Int] = None) extends UnaryNode with HasPartitionExpressions {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    condition: Option[Expression] = None) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    condition: Option[Expression]) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/EventTimeWatermark.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/hints.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/hints.scala:  extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/LogicalPlan.scala:abstract class OrderPreservingUnaryNode extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:trait ObjectConsumer extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode with ObjectProducer {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode with ObjectProducer {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode with ObjectProducer {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:trait BaseEvalPython extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    child: LogicalPlan) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/ScriptTransformation.scala:    ioschema: ScriptInputOutputSchema) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/streaming/WriteToStream.scala:    catalogTable: Option[CatalogTable]) extends UnaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/streaming/WriteToStreamStatement.scala:    catalogTable: Option[CatalogTable] = None) extends UnaryNode {
     * core/src/main/scala/org/apache/spark/sql/execution/datasources/v2/WriteToDataSourceV2Exec.scala:    customMetrics: Seq[CustomMetric]) extends UnaryNode {
     * core/src/main/scala/org/apache/spark/sql/execution/datasources/WriteFiles.scala:    staticPartitions: TablePartitionSpec) extends UnaryNode {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/continuous/WriteToContinuousDataSource.scala:  extends UnaryNode {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/sources/WriteToMicroBatchDataSource.scala:  extends UnaryNode {
     * core/src/main/scala/org/apache/spark/sql/execution/streaming/sources/WriteToMicroBatchDataSourceV1.scala:  extends UnaryNode {
	 * </PRE>
	 */
	public T caseUnaryNode(UnaryNode p) {
		return null;
	}

	/**
	 * direct sub-classes
	 * <PRE>
	 * catalyst/src/main/scala/org/apache/spark/sql/catalyst/optimizer/CostBasedJoinReorder.scala:    condition: Option[Expression]) extends BinaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:abstract class SetOperation(left: LogicalPlan, right: LogicalPlan) extends BinaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:  extends BinaryNode with PredicateHelper {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/basicLogicalOperators.scala:    toleranceAssertion: Option[Expression]) extends BinaryNode {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    child: LogicalPlan) extends BinaryNode with ObjectProducer {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/object.scala:    right: LogicalPlan) extends BinaryNode with ObjectProducer {
     * catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/pythonLogicalOperators.scala:    right: LogicalPlan) extends BinaryNode {
	 * </PRE>
	 */
	public T caseBinaryNode(BinaryNode p) {
		return null;
	}

	/** interface */
	public T caseExposesMetadataColumns(ExposesMetadataColumns p) {
		return null;
	}

	/** interface */
	public T caseObjectProducer(ObjectProducer p) {
		return null;
	}
	
	/**
	 * sub-classes
	 * <PRE>
	 * InsertIntoStatement
	 * </PRE>
	 */
	public T caseParsedStatement(ParsedStatement p) {
		return caseLogicalPlan(p);
	}
	
	public T caseInsertIntoStatement(InsertIntoStatement p) {
		return caseParsedStatement(p);
	}
	
	/** interface */
	public T caseV2CreateTablePlan(V2CreateTablePlan p) {
		return null;
	}
	
	public T caseCreateTable(CreateTable p) {
		return null;
	}

	// sub-classes of LeafNode
	// ------------------------------------------------------------------------
	
	public T caseRelationTimeTravel(RelationTimeTravel p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedRelation(UnresolvedRelation p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedInlineTable(UnresolvedInlineTable p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedTableValuedFunction(UnresolvedTableValuedFunction p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedNamespace(UnresolvedNamespace p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedTable(UnresolvedTable p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedView(UnresolvedView p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedTableOrView(UnresolvedTableOrView p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedFunc(UnresolvedFunc p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedDBObjectName(UnresolvedDBObjectName p) {
		return caseLeafNode(p);
	}

	public T caseLeafNodeWithoutStats(LeafNodeWithoutStats p) {
		return caseLeafNode(p);
	}

	public T caseResolvedNamespace(ResolvedNamespace p) {
		return caseLeafNode(p);
	}

	public T caseResolvedTable(ResolvedTable p) {
		return caseLeafNode(p);
	}

	public T caseResolvedView(ResolvedView p) {
		return caseLeafNode(p);
	}

	public T caseResolvedPersistentFunc(ResolvedPersistentFunc p) {
		return caseLeafNode(p);
	}

	public T caseResolvedNonPersistentFunc(ResolvedNonPersistentFunc p) {
		return caseLeafNode(p);
	}

	public T caseResolvedDBObjectName(ResolvedDBObjectName p) {
		return caseLeafNode(p);
	}

	public T caseUnresolvedCatalogRelation(UnresolvedCatalogRelation p) {
		return caseLeafNode(p);
	}

	public T caseTemporaryViewRelation(TemporaryViewRelation p) {
		return caseLeafNode(p);
	}

	public T caseHiveTableRelation(HiveTableRelation p) {
		return caseLeafNode(p);
	}

	public T caseDummyExpressionHolder(DummyExpressionHolder p) {
		return caseLeafNode(p);
	}

	public T caseCTERelationRef(CTERelationRef p) {
		return caseLeafNode(p);
	}

	public T caseRange(Range p) {
		return caseLeafNode(p);
	}

	public T caseOneRowRelation(OneRowRelation p) {
		return caseLeafNode(p);
	}

	public T caseLocalRelation(LocalRelation p) {
		return caseLeafNode(p);
	}

	public T caseStreamingRelationV2(StreamingRelationV2 p) {
		return caseLeafNode(p);
	}

	public T caseDataSourceV2Relation(DataSourceV2Relation p) {
		return caseLeafNode(p);
	}

	public T caseDataSourceV2ScanRelation(DataSourceV2ScanRelation p) {
		return caseLeafNode(p);
	}

	public T caseStreamingDataSourceV2Relation(StreamingDataSourceV2Relation p) {
		return caseLeafNode(p);
	}

	public T caseCommandResult(CommandResult p) {
		return caseLeafNode(p);
	}

	public T caseLogicalQueryStage(LogicalQueryStage p) {
		return caseLeafNode(p);
	}

	public T caseLogicalRelation(LogicalRelation p) {
		return caseLeafNode(p);
	}

	public T caseScanBuilderHolder(ScanBuilderHolder p) {
		return caseLeafNode(p);
	}

	public T caseExternalRDD(ExternalRDD p) {
		return caseLeafNode(p);
	}

	public T caseLogicalRDD(LogicalRDD p) {
		return caseLeafNode(p);
	}

	public T caseOffsetHolder(OffsetHolder p) {
		return caseLeafNode(p);
	}

	public T caseMemoryPlan(MemoryPlan p) {
		return caseLeafNode(p);
	}

	public T caseStreamingRelation(StreamingRelation p) {
		return caseLeafNode(p);
	}

	public T caseStreamingExecutionRelation(StreamingExecutionRelation p) {
		return caseLeafNode(p);
	}

	// UnaryNode sub-classes
	// ------------------------------------------------------------------------

	// import??
//	public T caseUnresolvedTVFAliases(UnresolvedTVFAliases p) {
//		return caseUnaryNode(p);
//	}
	public T caseUnresolvedSubqueryColumnAliases(UnresolvedSubqueryColumnAliases p) {
		return caseUnaryNode(p);
	}

	public T caseUnresolvedHaving(UnresolvedHaving p) {
		return caseUnaryNode(p);
	}

	public T caseReturnAnswer(ReturnAnswer p) {
		return caseUnaryNode(p);
	}

	public T caseGenerate(Generate p) {
		return caseUnaryNode(p);
	}

	public T caseInsertIntoDir(InsertIntoDir p) {
		return caseUnaryNode(p);
	}

	public T caseView(View p) {
		return caseUnaryNode(p);
	}

	public T caseUnresolvedWith(UnresolvedWith p) {
		return caseUnaryNode(p);
	}

	public T caseCTERelationDef(CTERelationDef p) {
		return caseUnaryNode(p);
	}

	public T caseWithWindowDefinition(WithWindowDefinition p) {
		return caseUnaryNode(p);
	}

	public T caseAggregate(Aggregate p) {
		return caseUnaryNode(p);
	}

	public T caseWindow(Window p) {
		return caseUnaryNode(p);
	}

	public T caseExpand(Expand p) {
		return caseUnaryNode(p);
	}

	public T casePivot(Pivot p) {
		return caseUnaryNode(p);
	}

	// import??
//	public T caseUnpivot(Unpivot p) {
//		return caseUnaryNode(p);
//	}

	public T caseGlobalLimit(GlobalLimit p) {
		return caseUnaryNode(p);
	}

	public T caseSample(Sample p) {
		return caseUnaryNode(p);
	}

	public T caseDistinct(Distinct p) {
		return caseUnaryNode(p);
	}

	/**
	 * direct-sub-classes:
	 * </PRE>
	 * Repartition
	 * RepartitionByExpression
	 * </PRE>
	 */
	public T caseRepartitionOperation(RepartitionOperation p) {
		return caseUnaryNode(p);
	}

	public T caseRepartition(Repartition p) {
		return caseRepartitionOperation(p);
	}

	public T caseRepartitionByExpression(RepartitionByExpression p) {
		return caseRepartitionOperation(p);
	}

	public T caseRebalancePartitions(RebalancePartitions p) {
		return caseUnaryNode(p);
	}

	public T caseDeduplicate(Deduplicate p) {
		return caseUnaryNode(p);
	}

	public T caseCollectMetrics(CollectMetrics p) {
		return caseUnaryNode(p);
	}

	public T caseDomainJoin(DomainJoin p) {
		return caseUnaryNode(p);
	}

	public T caseLateralJoin(LateralJoin p) {
		return caseUnaryNode(p);
	}

	public T caseEventTimeWatermark(EventTimeWatermark p) {
		return caseUnaryNode(p);
	}

	public T caseUnresolvedHint(UnresolvedHint p) {
		return caseUnaryNode(p);
	}

	public T caseResolvedHint(ResolvedHint p) {
		return caseUnaryNode(p);
	}

	/**
	 * TODO direct-sub-classes:
	 * </PRE>
	 * Filter
	 * LocalLimit
	 * Project
	 * Subquery
	 * SubqueryAlias
	 * Tail
	 * </PRE>
	 */
	public T caseOrderPreservingUnaryNode(OrderPreservingUnaryNode p) {
		return caseUnaryNode(p);
	}

	public T caseFilter(Filter p) {
		return caseOrderPreservingUnaryNode(p);
	}

	public T caseLocalLimit(LocalLimit p) {
		return caseOrderPreservingUnaryNode(p);
	}

	public T caseProject(Project p) {
		return caseOrderPreservingUnaryNode(p);
	}

	public T caseSubquery(Subquery p) {
		return caseOrderPreservingUnaryNode(p);
	}

	public T caseSubqueryAlias(SubqueryAlias p) {
		return caseOrderPreservingUnaryNode(p);
	}

	public T caseTail(Tail p) {
		return caseOrderPreservingUnaryNode(p);
	}

	
	public T caseObjectConsumer(ObjectConsumer p) {
		return caseUnaryNode(p);
	}

	public T caseDeserializeToObject(DeserializeToObject p) {
		return caseUnaryNode(p);
	}

	public T caseMapPartitionsInRWithArrow(MapPartitionsInRWithArrow p) {
		return caseUnaryNode(p);
	}

	public T caseTypedFilter(TypedFilter p) {
		return caseUnaryNode(p);
	}

	public T caseAppendColumns(AppendColumns p) {
		return caseUnaryNode(p);
	}

	public T caseMapGroups(MapGroups p) {
		return caseUnaryNode(p);
	}

	public T caseFlatMapGroupsInR(FlatMapGroupsInR p) {
		return caseUnaryNode(p);
	}

	public T caseFlatMapGroupsInRWithArrow(FlatMapGroupsInRWithArrow p) {
		return caseUnaryNode(p);
	}

	public T caseFlatMapGroupsInPandas(FlatMapGroupsInPandas p) {
		return caseUnaryNode(p);
	}

	public T caseMapInPandas(MapInPandas p) {
		return caseUnaryNode(p);
	}

	public T casePythonMapInArrow(PythonMapInArrow p) {
		return caseUnaryNode(p);
	}

//	// import?
//	public T caseFlatMapGroupsInPandasWithState(FlatMapGroupsInPandasWithState p) {
//		return caseUnaryNode(p);
//	}

	public T caseBaseEvalPython(BaseEvalPython p) {
		return caseUnaryNode(p);
	}

	public T caseAttachDistributedSequence(AttachDistributedSequence p) {
		return caseUnaryNode(p);
	}

	public T caseScriptTransformation(ScriptTransformation p) {
		return caseUnaryNode(p);
	}

	public T caseWriteToStream(WriteToStream p) {
		return caseUnaryNode(p);
	}

	public T caseWriteToStreamStatement(WriteToStreamStatement p) {
		return caseUnaryNode(p);
	}

	// @deprecated
	public T caseWriteToDataSourceV2(WriteToDataSourceV2 p) {
		return caseUnaryNode(p);
	}

	// import?
//	public T caseWriteFiles(WriteFiles p) {
//		return caseUnaryNode(p);
//	}

	public T caseWriteToContinuousDataSource(WriteToContinuousDataSource p) {
		return caseUnaryNode(p);
	}

	public T caseWriteToMicroBatchDataSource(WriteToMicroBatchDataSource p) {
		return caseUnaryNode(p);
	}

	// import?
//	public T caseWriteToMicroBatchDataSourceV1(WriteToMicroBatchDataSourceV1 p) {
//		return caseUnaryNode(p);
//	}

	// BinaryNode sub-classes
	// ------------------------------------------------------------------------
		
	public T caseOrderedJoin(OrderedJoin p) {
		return caseBinaryNode(p);
	}
	
	/** abstract, sub-classes:
	 * <PRE>
	 * Except
	 * Intersect
	 * </PRE>
	 */
	public T caseSetOperation(SetOperation p) {
		return caseBinaryNode(p);
	}

	public T caseExcept(Except p) {
		return caseSetOperation(p);
	}

	public T caseIntersect(Intersect p) {
		return caseSetOperation(p);
	}

	public T caseJoin(Join p) {
		return caseBinaryNode(p);
	}
	
	public T caseAsOfJoin(AsOfJoin p) {
		return caseBinaryNode(p);
	}

	public T caseFlatMapGroupsWithState(FlatMapGroupsWithState p) {
		return caseBinaryNode(p);
	}
	
	public T caseCoGroup(CoGroup p) {
		return caseBinaryNode(p);
	}

	public T caseFlatMapCoGroupsInPandas(FlatMapCoGroupsInPandas p) {
		return caseBinaryNode(p);
	}
	
}

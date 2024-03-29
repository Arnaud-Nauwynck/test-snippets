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

import lombok.val;
import scala.Option;
import scala.collection.Seq;

public abstract class LogicalPlanExtVisitor<T> {

	@SuppressWarnings("deprecation")
	public T accept(LogicalPlan p) {
		if (p == null) {
			return null;
		}
		if (p instanceof UnaryNode) { 
			// return switchUnaryNode((UnaryNode) p); 
			// import?? UnresolvedTVFAliases
			if (p instanceof UnresolvedSubqueryColumnAliases) {
				return caseUnresolvedSubqueryColumnAliases((UnresolvedSubqueryColumnAliases) p);
			} else if (p instanceof UnresolvedHaving) {
				return caseUnresolvedHaving((UnresolvedHaving) p);
			} else if (p instanceof ReturnAnswer) {
				return caseReturnAnswer((ReturnAnswer) p);
			} else if (p instanceof Generate) {
				return caseGenerate((Generate) p);
			} else if (p instanceof InsertIntoDir) {
				return caseInsertIntoDir((InsertIntoDir) p);
			} else if (p instanceof View) {
				return caseView((View) p);
			} else if (p instanceof UnresolvedWith) {
				return caseUnresolvedWith((UnresolvedWith) p);
			} else if (p instanceof CTERelationDef) {
				return caseCTERelationDef((CTERelationDef) p);
			} else if (p instanceof WithWindowDefinition) {
				return caseWithWindowDefinition((WithWindowDefinition) p);
			} else if (p instanceof Aggregate) {
				return caseAggregate((Aggregate) p);
			} else if (p instanceof Window) {
				return caseWindow((Window) p);
			} else if (p instanceof Expand) {
				return caseExpand((Expand) p);
			} else if (p instanceof Pivot) {
				return casePivot((Pivot) p);
			// import??
//			} else if (p instanceof Unpivot) {
//				return caseUnpivot((Unpivot) p);
			} else if (p instanceof GlobalLimit) {
				return caseGlobalLimit((GlobalLimit) p);
			} else if (p instanceof Sample) {
				return caseSample((Sample) p);
			} else if (p instanceof Distinct) {
				return caseDistinct((Distinct) p);
			} else if (p instanceof Repartition) {
				return caseRepartition((Repartition) p);
			} else if (p instanceof RepartitionByExpression) {
				return caseRepartitionByExpression((RepartitionByExpression) p);
			} else if (p instanceof RebalancePartitions) {
				return caseRebalancePartitions((RebalancePartitions) p);
			} else if (p instanceof Deduplicate) {
				return caseDeduplicate((Deduplicate) p);
			} else if (p instanceof CollectMetrics) {
				return caseCollectMetrics((CollectMetrics) p);
			} else if (p instanceof DomainJoin) {
				return caseDomainJoin((DomainJoin) p);
			} else if (p instanceof LateralJoin) {
				return caseLateralJoin((LateralJoin) p);
			} else if (p instanceof EventTimeWatermark) {
				return caseEventTimeWatermark((EventTimeWatermark) p);
			} else if (p instanceof UnresolvedHint) {
				return caseUnresolvedHint((UnresolvedHint) p);
			} else if (p instanceof ResolvedHint) {
				return caseResolvedHint((ResolvedHint) p);
			} else if (p instanceof OrderPreservingUnaryNode) {
				if (p instanceof Filter) {
					return caseFilter((Filter) p);
				} else if (p instanceof LocalLimit) {
					return caseLocalLimit((LocalLimit) p);
				} else if (p instanceof Project) {
					return caseProject((Project) p);
				} else if (p instanceof Subquery) {
					return caseSubquery((Subquery) p);
				} else if (p instanceof SubqueryAlias) {
					return caseSubqueryAlias((SubqueryAlias) p);
				} else if (p instanceof Tail) {
					return caseTail((Tail) p);
				} else {
					// should not occur
					return caseOrderPreservingUnaryNode((OrderPreservingUnaryNode) p);
				}
			} else if (p instanceof ObjectConsumer) {
				return caseObjectConsumer((ObjectConsumer) p);
			} else if (p instanceof DeserializeToObject) {
				return caseDeserializeToObject((DeserializeToObject) p);
			} else if (p instanceof MapPartitionsInRWithArrow) {
				return caseMapPartitionsInRWithArrow((MapPartitionsInRWithArrow) p);
			} else if (p instanceof TypedFilter) {
				return caseTypedFilter((TypedFilter) p);
			} else if (p instanceof AppendColumns) {
				return caseAppendColumns((AppendColumns) p);
			} else if (p instanceof MapGroups) {
				return caseMapGroups((MapGroups) p);
			} else if (p instanceof FlatMapGroupsInR) {
				return caseFlatMapGroupsInR((FlatMapGroupsInR) p);
			} else if (p instanceof FlatMapGroupsInRWithArrow) {
				return caseFlatMapGroupsInRWithArrow((FlatMapGroupsInRWithArrow) p);
			} else if (p instanceof FlatMapGroupsInPandas) {
				return caseFlatMapGroupsInPandas((FlatMapGroupsInPandas) p);
			} else if (p instanceof MapInPandas) {
				return caseMapInPandas((MapInPandas) p);
			} else if (p instanceof PythonMapInArrow) {
				return casePythonMapInArrow((PythonMapInArrow) p);
//			// import?
//			} else if (p instanceof FlatMapGroupsInPandasWithState) {
//				return caseFlatMapGroupsInPandasWithState((FlatMapGroupsInPandasWithState) p);
			} else if (p instanceof BaseEvalPython) {
				return caseBaseEvalPython((BaseEvalPython) p);
			} else if (p instanceof AttachDistributedSequence) {
				return caseAttachDistributedSequence((AttachDistributedSequence) p);
			} else if (p instanceof ScriptTransformation) {
				return caseScriptTransformation((ScriptTransformation) p);
			} else if (p instanceof WriteToStream) {
				return caseWriteToStream((WriteToStream) p);
			} else if (p instanceof WriteToStreamStatement) {
				return caseWriteToStreamStatement((WriteToStreamStatement) p);
			} else if (p instanceof WriteToDataSourceV2) {
				return caseWriteToDataSourceV2((WriteToDataSourceV2) p);
			// import?
//			} else if (p instanceof WriteFiles) {
//				return caseWriteFiles((WriteFiles) p);
			} else if (p instanceof WriteToContinuousDataSource) {
				return caseWriteToContinuousDataSource((WriteToContinuousDataSource) p);
			} else if (p instanceof WriteToMicroBatchDataSource) {
				return caseWriteToMicroBatchDataSource((WriteToMicroBatchDataSource) p);
			// import?
//			} else if (p instanceof WriteToMicroBatchDataSourceV1) {
//				return caseWriteToMicroBatchDataSourceV1((WriteToMicroBatchDataSourceV1) p);
			} else {
				// should not occur
				return caseUnaryNode((UnaryNode) p);
			}
		
		} else if (p instanceof BinaryNode) {
			// BinaryNode
			if (p instanceof OrderedJoin) {
				return caseOrderedJoin((OrderedJoin) p);
			} else if (p instanceof SetOperation) {
				if (p instanceof Except) {
					return caseExcept((Except) p);
				} else if (p instanceof Intersect) {
					return caseIntersect((Intersect) p);
				} else {
					// should not occur 
					return caseSetOperation((SetOperation) p);
				}
			} else if (p instanceof Join) {
				return caseJoin((Join) p);
			} else if (p instanceof AsOfJoin) {
				return caseAsOfJoin((AsOfJoin) p);
			} else if (p instanceof FlatMapGroupsWithState) {
				return caseFlatMapGroupsWithState((FlatMapGroupsWithState) p);
			} else if (p instanceof CoGroup) {
				return caseCoGroup((CoGroup) p);
			} else if (p instanceof FlatMapCoGroupsInPandas) {
				return caseFlatMapCoGroupsInPandas((FlatMapCoGroupsInPandas) p);
			} else {
				// should not occur
				return caseBinaryNode((BinaryNode) p); 
			}
		} else if (p instanceof LeafNode) { 
			// LeafNode
			if (p instanceof RelationTimeTravel) {
				return caseRelationTimeTravel((RelationTimeTravel) p);
			} else if (p instanceof UnresolvedRelation) {
				return caseUnresolvedRelation((UnresolvedRelation) p);
			} else if (p instanceof UnresolvedInlineTable) {
				return caseUnresolvedInlineTable((UnresolvedInlineTable) p);
			} else if (p instanceof UnresolvedTableValuedFunction) {
				return caseUnresolvedTableValuedFunction((UnresolvedTableValuedFunction) p);
			} else if (p instanceof UnresolvedNamespace) {
				return caseUnresolvedNamespace((UnresolvedNamespace) p);
			} else if (p instanceof UnresolvedTable) {
				return caseUnresolvedTable((UnresolvedTable) p);
			} else if (p instanceof UnresolvedView) {
				return caseUnresolvedView((UnresolvedView) p);
			} else if (p instanceof UnresolvedTableOrView) {
				return caseUnresolvedTableOrView((UnresolvedTableOrView) p);
			} else if (p instanceof UnresolvedFunc) {
				return caseUnresolvedFunc((UnresolvedFunc) p);
			} else if (p instanceof UnresolvedDBObjectName) {
				return caseUnresolvedDBObjectName((UnresolvedDBObjectName) p);
			} else if (p instanceof LeafNodeWithoutStats) {
				return caseLeafNodeWithoutStats((LeafNodeWithoutStats) p);
			} else if (p instanceof ResolvedNamespace) {
				return caseResolvedNamespace((ResolvedNamespace) p);
			} else if (p instanceof ResolvedTable) {
				return caseResolvedTable((ResolvedTable) p);
			} else if (p instanceof ResolvedView) {
				return caseResolvedView((ResolvedView) p);
			} else if (p instanceof ResolvedPersistentFunc) {
				return caseResolvedPersistentFunc((ResolvedPersistentFunc) p);
			} else if (p instanceof ResolvedNonPersistentFunc) {
				return caseResolvedNonPersistentFunc((ResolvedNonPersistentFunc) p);
			} else if (p instanceof ResolvedDBObjectName) {
				return caseResolvedDBObjectName((ResolvedDBObjectName) p);
			} else if (p instanceof UnresolvedCatalogRelation) {
				return caseUnresolvedCatalogRelation((UnresolvedCatalogRelation) p);
			} else if (p instanceof TemporaryViewRelation) {
				return caseTemporaryViewRelation((TemporaryViewRelation) p);
			} else if (p instanceof HiveTableRelation) {
				return caseHiveTableRelation((HiveTableRelation) p);
			} else if (p instanceof DummyExpressionHolder) {
				return caseDummyExpressionHolder((DummyExpressionHolder) p);
			} else if (p instanceof CTERelationRef) {
				return caseCTERelationRef((CTERelationRef) p);
			} else if (p instanceof Range) {
				return caseRange((Range) p);
			} else if (p instanceof OneRowRelation) {
				return caseOneRowRelation((OneRowRelation) p);
			} else if (p instanceof LocalRelation) {
				return caseLocalRelation((LocalRelation) p);
			} else if (p instanceof StreamingRelationV2) {
				return caseStreamingRelationV2((StreamingRelationV2) p);
			} else if (p instanceof DataSourceV2Relation) {
				return caseDataSourceV2Relation((DataSourceV2Relation) p);
			} else if (p instanceof DataSourceV2ScanRelation) {
				return caseDataSourceV2ScanRelation((DataSourceV2ScanRelation) p);
			} else if (p instanceof StreamingDataSourceV2Relation) {
				return caseStreamingDataSourceV2Relation((StreamingDataSourceV2Relation) p);
			} else if (p instanceof CommandResult) {
				return caseCommandResult((CommandResult) p);
			} else if (p instanceof LogicalQueryStage) {
				return caseLogicalQueryStage((LogicalQueryStage) p);
			} else if (p instanceof LogicalRelation) {
				return caseLogicalRelation((LogicalRelation) p);
			} else if (p instanceof ScanBuilderHolder) {
				return caseScanBuilderHolder((ScanBuilderHolder) p);
			} else if (p instanceof ExternalRDD) {
				return caseExternalRDD((ExternalRDD<?>) p);
			} else if (p instanceof LogicalRDD) {
				return caseLogicalRDD((LogicalRDD) p);
			} else if (p instanceof OffsetHolder) {
				return caseOffsetHolder((OffsetHolder) p);
			} else if (p instanceof MemoryPlan) {
				return caseMemoryPlan((MemoryPlan) p);
			} else if (p instanceof StreamingRelation) {
				return caseStreamingRelation((StreamingRelation) p);
			} else if (p instanceof StreamingExecutionRelation) {
				return caseStreamingExecutionRelation((StreamingExecutionRelation) p);
			} else {
				// should not occur
				return caseLeafNode((LeafNode) p);
			}
		} else if (p instanceof NamedRelation) { 
			return caseNamedRelation((NamedRelation) p); 
		} else if (p instanceof Union) {
			return caseUnion((Union) p);
		} else if (p instanceof WithCTE) {
			return caseWithCTE((WithCTE) p);
		} else if (p instanceof InsertIntoStatement) {
			return caseInsertIntoStatement((InsertIntoStatement) p);
		} else if (p instanceof CreateTable) {
			return caseCreateTable((CreateTable) p);
		} else if (p instanceof ParsedStatement) {
			if (p instanceof InsertIntoStatement) {
				return caseInsertIntoStatement((InsertIntoStatement) p);
			} else {
				// should not occur
				return caseParsedStatement((ParsedStatement) p);
			}
		} else {
			// should not occur
			return caseLogicalPlan(p);
		}
	}

	public void acceptSeq(Seq<? extends LogicalPlan> ls) {
		if (ls != null) {
			for(val e: ScalaToJavaUtils.toJavaList(ls)) {
				accept(e);
			}
		}
	}
	
	public T acceptOpt(Option<LogicalPlan> p) {
		if (p == null) {
			return null;
		}
		if (p.isDefined()) {
			return accept(p.get());
		} else {
			return null;
		}
	}
	
	
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
	public abstract T caseLogicalPlan(LogicalPlan p);

	/**
	 * (interface)
	 */
	public abstract T caseNamedRelation(NamedRelation p);

	public abstract T caseUnion(Union p);

	public abstract T caseWithCTE(WithCTE p);

	/**
	 * (interface)
	 */
	public abstract T caseSupportsSubquery(SupportsSubquery p);

	/**
	 * (interface)
	 */
	public abstract T caseCommand(Command p);

	/**
	 * (interface)
	 */
	public abstract T caseIgnoreCachedData(IgnoreCachedData p);

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
	public abstract T caseLeafNode(LeafNode p);

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
	public abstract T caseUnaryNode(UnaryNode p);

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
	public abstract T caseBinaryNode(BinaryNode p);

	/** interface */
	public abstract T caseExposesMetadataColumns(ExposesMetadataColumns p);

	/** interface */
	public abstract T caseObjectProducer(ObjectProducer p);
	
	/**
	 * sub-classes
	 * <PRE>
	 * InsertIntoStatement
	 * </PRE>
	 */
	public abstract T caseParsedStatement(ParsedStatement p);
	
	public abstract T caseInsertIntoStatement(InsertIntoStatement p);
	
	/** interface */
	public abstract T caseV2CreateTablePlan(V2CreateTablePlan p);
	
	public abstract T caseCreateTable(CreateTable p);

	// sub-classes of LeafNode
	// ------------------------------------------------------------------------
	
	public abstract T caseRelationTimeTravel(RelationTimeTravel p);

	public abstract T caseUnresolvedRelation(UnresolvedRelation p);

	public abstract T caseUnresolvedInlineTable(UnresolvedInlineTable p);

	public abstract T caseUnresolvedTableValuedFunction(UnresolvedTableValuedFunction p);

	public abstract T caseUnresolvedNamespace(UnresolvedNamespace p);

	public abstract T caseUnresolvedTable(UnresolvedTable p);

	public abstract T caseUnresolvedView(UnresolvedView p);

	public abstract T caseUnresolvedTableOrView(UnresolvedTableOrView p);

	public abstract T caseUnresolvedFunc(UnresolvedFunc p);

	public abstract T caseUnresolvedDBObjectName(UnresolvedDBObjectName p);

	public abstract T caseLeafNodeWithoutStats(LeafNodeWithoutStats p);

	public abstract T caseResolvedNamespace(ResolvedNamespace p);

	public abstract T caseResolvedTable(ResolvedTable p);

	public abstract T caseResolvedView(ResolvedView p);

	public abstract T caseResolvedPersistentFunc(ResolvedPersistentFunc p);

	public abstract T caseResolvedNonPersistentFunc(ResolvedNonPersistentFunc p);

	public abstract T caseResolvedDBObjectName(ResolvedDBObjectName p);

	public abstract T caseUnresolvedCatalogRelation(UnresolvedCatalogRelation p);

	public abstract T caseTemporaryViewRelation(TemporaryViewRelation p);

	public abstract T caseHiveTableRelation(HiveTableRelation p);

	public abstract T caseDummyExpressionHolder(DummyExpressionHolder p);

	public abstract T caseCTERelationRef(CTERelationRef p);

	public abstract T caseRange(Range p);

	public abstract T caseOneRowRelation(OneRowRelation p);

	public abstract T caseLocalRelation(LocalRelation p);

	public abstract T caseStreamingRelationV2(StreamingRelationV2 p);

	public abstract T caseDataSourceV2Relation(DataSourceV2Relation p);

	public abstract T caseDataSourceV2ScanRelation(DataSourceV2ScanRelation p);

	public abstract T caseStreamingDataSourceV2Relation(StreamingDataSourceV2Relation p);

	public abstract T caseCommandResult(CommandResult p);

	public abstract T caseLogicalQueryStage(LogicalQueryStage p);

	public abstract T caseLogicalRelation(LogicalRelation p);

	public abstract T caseScanBuilderHolder(ScanBuilderHolder p);

	public abstract T caseExternalRDD(ExternalRDD p);

	public abstract T caseLogicalRDD(LogicalRDD p);

	public abstract T caseOffsetHolder(OffsetHolder p);

	public abstract T caseMemoryPlan(MemoryPlan p);

	public abstract T caseStreamingRelation(StreamingRelation p);

	public abstract T caseStreamingExecutionRelation(StreamingExecutionRelation p);

	// UnaryNode sub-classes
	// ------------------------------------------------------------------------

	// import??
//	public abstract T caseUnresolvedTVFAliases(UnresolvedTVFAliases p) {
//		return caseUnaryNode(p);
//	}
	public abstract T caseUnresolvedSubqueryColumnAliases(UnresolvedSubqueryColumnAliases p);

	public abstract T caseUnresolvedHaving(UnresolvedHaving p);

	public abstract T caseReturnAnswer(ReturnAnswer p);

	public abstract T caseGenerate(Generate p);

	public abstract T caseInsertIntoDir(InsertIntoDir p);

	public abstract T caseView(View p);

	public abstract T caseUnresolvedWith(UnresolvedWith p);

	public abstract T caseCTERelationDef(CTERelationDef p);

	public abstract T caseWithWindowDefinition(WithWindowDefinition p);

	public abstract T caseAggregate(Aggregate p);

	public abstract T caseWindow(Window p);

	public abstract T caseExpand(Expand p);

	public abstract T casePivot(Pivot p);

	// import??
//	public abstract T caseUnpivot(Unpivot p) {
//		return caseUnaryNode(p);
//	}

	public abstract T caseGlobalLimit(GlobalLimit p);

	public abstract T caseSample(Sample p);

	public abstract T caseDistinct(Distinct p);

	/**
	 * direct-sub-classes:
	 * </PRE>
	 * Repartition
	 * RepartitionByExpression
	 * </PRE>
	 */
	public abstract T caseRepartitionOperation(RepartitionOperation p);

	public abstract T caseRepartition(Repartition p);

	public abstract T caseRepartitionByExpression(RepartitionByExpression p);

	public abstract T caseRebalancePartitions(RebalancePartitions p);

	public abstract T caseDeduplicate(Deduplicate p);

	public abstract T caseCollectMetrics(CollectMetrics p);

	public abstract T caseDomainJoin(DomainJoin p);

	public abstract T caseLateralJoin(LateralJoin p);

	public abstract T caseEventTimeWatermark(EventTimeWatermark p);

	public abstract T caseUnresolvedHint(UnresolvedHint p);

	public abstract T caseResolvedHint(ResolvedHint p);

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
	public abstract T caseOrderPreservingUnaryNode(OrderPreservingUnaryNode p);

	public abstract T caseFilter(Filter p);

	public abstract T caseLocalLimit(LocalLimit p);

	public abstract T caseProject(Project p);

	public abstract T caseSubquery(Subquery p);

	public abstract T caseSubqueryAlias(SubqueryAlias p);

	public abstract T caseTail(Tail p);

	public abstract T caseObjectConsumer(ObjectConsumer p);

	public abstract T caseDeserializeToObject(DeserializeToObject p);

	public abstract T caseMapPartitionsInRWithArrow(MapPartitionsInRWithArrow p);

	public abstract T caseTypedFilter(TypedFilter p);

	public abstract T caseAppendColumns(AppendColumns p);

	public abstract T caseMapGroups(MapGroups p);

	public abstract T caseFlatMapGroupsInR(FlatMapGroupsInR p);

	public abstract T caseFlatMapGroupsInRWithArrow(FlatMapGroupsInRWithArrow p);

	public abstract T caseFlatMapGroupsInPandas(FlatMapGroupsInPandas p);

	public abstract T caseMapInPandas(MapInPandas p);

	public abstract T casePythonMapInArrow(PythonMapInArrow p);

//	// import?
//	public abstract T caseFlatMapGroupsInPandasWithState(FlatMapGroupsInPandasWithState p) {
//		return caseUnaryNode(p);
//	}

	public abstract T caseBaseEvalPython(BaseEvalPython p);

	public abstract T caseAttachDistributedSequence(AttachDistributedSequence p);

	public abstract T caseScriptTransformation(ScriptTransformation p);

	public abstract T caseWriteToStream(WriteToStream p);

	public abstract T caseWriteToStreamStatement(WriteToStreamStatement p);

	// @deprecated
	public abstract T caseWriteToDataSourceV2(WriteToDataSourceV2 p);

	// import?
//	public abstract T caseWriteFiles(WriteFiles p) {
//		return caseUnaryNode(p);
//	}

	public abstract T caseWriteToContinuousDataSource(WriteToContinuousDataSource p);

	public abstract T caseWriteToMicroBatchDataSource(WriteToMicroBatchDataSource p);

	// import?
//	public abstract T caseWriteToMicroBatchDataSourceV1(WriteToMicroBatchDataSourceV1 p) {
//		return caseUnaryNode(p);
//	}

	// BinaryNode sub-classes
	// ------------------------------------------------------------------------
		
	public abstract T caseOrderedJoin(OrderedJoin p);
	
	/** abstract, sub-classes:
	 * <PRE>
	 * Except
	 * Intersect
	 * </PRE>
	 */
	public abstract T caseSetOperation(SetOperation p);

	public abstract T caseExcept(Except p);

	public abstract T caseIntersect(Intersect p);

	public abstract T caseJoin(Join p);
	
	public abstract T caseAsOfJoin(AsOfJoin p);

	public abstract T caseFlatMapGroupsWithState(FlatMapGroupsWithState p);
	
	public abstract T caseCoGroup(CoGroup p);

	public abstract T caseFlatMapCoGroupsInPandas(FlatMapCoGroupsInPandas p);
	
}

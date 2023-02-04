package fr.an.tests.spark.sql;

import org.apache.spark.sql.catalyst.analysis.UnresolvedHaving;
import org.apache.spark.sql.catalyst.analysis.UnresolvedSubqueryColumnAliases;
import org.apache.spark.sql.catalyst.catalog.TemporaryViewRelation;
import org.apache.spark.sql.catalyst.encoders.DummyExpressionHolder;
import org.apache.spark.sql.catalyst.optimizer.OrderedJoin;
import org.apache.spark.sql.catalyst.plans.logical.Aggregate;
import org.apache.spark.sql.catalyst.plans.logical.AppendColumns;
import org.apache.spark.sql.catalyst.plans.logical.AsOfJoin;
import org.apache.spark.sql.catalyst.plans.logical.AttachDistributedSequence;
import org.apache.spark.sql.catalyst.plans.logical.BaseEvalPython;
import org.apache.spark.sql.catalyst.plans.logical.CTERelationDef;
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
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.catalyst.plans.logical.MapGroups;
import org.apache.spark.sql.catalyst.plans.logical.MapInPandas;
import org.apache.spark.sql.catalyst.plans.logical.MapPartitionsInRWithArrow;
import org.apache.spark.sql.catalyst.plans.logical.ObjectConsumer;
import org.apache.spark.sql.catalyst.plans.logical.OrderPreservingUnaryNode;
import org.apache.spark.sql.catalyst.plans.logical.Pivot;
import org.apache.spark.sql.catalyst.plans.logical.PythonMapInArrow;
import org.apache.spark.sql.catalyst.plans.logical.RebalancePartitions;
import org.apache.spark.sql.catalyst.plans.logical.Repartition;
import org.apache.spark.sql.catalyst.plans.logical.RepartitionByExpression;
import org.apache.spark.sql.catalyst.plans.logical.RepartitionOperation;
import org.apache.spark.sql.catalyst.plans.logical.ResolvedHint;
import org.apache.spark.sql.catalyst.plans.logical.ReturnAnswer;
import org.apache.spark.sql.catalyst.plans.logical.Sample;
import org.apache.spark.sql.catalyst.plans.logical.ScriptTransformation;
import org.apache.spark.sql.catalyst.plans.logical.SetOperation;
import org.apache.spark.sql.catalyst.plans.logical.TypedFilter;
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
import org.apache.spark.sql.execution.adaptive.LogicalQueryStage;
import org.apache.spark.sql.execution.datasources.CreateTable;
import org.apache.spark.sql.execution.datasources.LogicalRelation;
import org.apache.spark.sql.execution.datasources.v2.DataSourceV2ScanRelation;
import org.apache.spark.sql.execution.datasources.v2.ScanBuilderHolder;
import org.apache.spark.sql.execution.datasources.v2.WriteToDataSourceV2;
import org.apache.spark.sql.execution.streaming.continuous.WriteToContinuousDataSource;
import org.apache.spark.sql.execution.streaming.sources.WriteToMicroBatchDataSource;

import lombok.val;
import scala.collection.Seq;

public abstract class DefaultRecursiveLogicalPlanExtVisitor<T> extends DefaultLogicalPlanExtVisitor<T> {

	@Override
	public T caseUnion(Union p) {
		Seq<LogicalPlan> children = p.children();
		if (children != null) {
			for(val child: ScalaToJavaUtils.toJavaList(children)) {
				accept(child);
			}
		}
		return caseLogicalPlan(p);
	}

	@Override
	public T caseWithCTE(WithCTE p) {
		val plan = p.plan();
		val cteDefs = p.cteDefs();
		if (cteDefs != null) {
			acceptSeq(cteDefs);
		}
		if (plan != null) {
			accept(plan);
		}
		return caseLogicalPlan(p);
	}


	@Override
	public T caseInsertIntoStatement(InsertIntoStatement p) {
    	accept(p.query());
		return caseParsedStatement(p);
	}

	@Override
	public T caseCreateTable(CreateTable p) {
		acceptOpt(p.query());
		return caseLogicalPlan(p);
	}

	// sub-classes of LeafNode
	// ------------------------------------------------------------------------

	@Override
	public T caseTemporaryViewRelation(TemporaryViewRelation p) {
		acceptOpt(p.plan());
		return caseLeafNode(p);
	}

	@Override
	public T caseDummyExpressionHolder(DummyExpressionHolder p) {
		// TOcheck Expression?
		return caseLeafNode(p);
	}

	@Override
	public T caseStreamingRelationV2(StreamingRelationV2 p) {
		acceptOpt(p.v1Relation());
		return caseLeafNode(p);
	}

	@Override
	public T caseDataSourceV2ScanRelation(DataSourceV2ScanRelation p) {
		accept(p.relation());
		return caseLeafNode(p);
	}

	@Override
	public T caseCommandResult(CommandResult p) {
		accept(p.commandLogicalPlan());
		// accept(p.commandPhysicalPlan());
		return caseLeafNode(p);
	}

	@Override
	public T caseLogicalQueryStage(LogicalQueryStage p) {
		accept(p.logicalPlan());
		return caseLeafNode(p);
	}

	@Override
	public T caseLogicalRelation(LogicalRelation p) {
		//? accept(p.relation());
		return caseLeafNode(p);
	}

	@Override
	public T caseScanBuilderHolder(ScanBuilderHolder p) {
		accept(p.relation());
		return caseLeafNode(p);
	}

	// UnaryNode sub-classes
	// ------------------------------------------------------------------------
	
	// import??
//	@Override
//	public T caseUnresolvedTVFAliases(UnresolvedTVFAliases p) {
//		return caseUnaryNode(p);
//	}
	@Override
	public T caseUnresolvedSubqueryColumnAliases(UnresolvedSubqueryColumnAliases p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedHaving(UnresolvedHaving p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseReturnAnswer(ReturnAnswer p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseGenerate(Generate p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseInsertIntoDir(InsertIntoDir p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseView(View p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedWith(UnresolvedWith p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseCTERelationDef(CTERelationDef p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseWithWindowDefinition(WithWindowDefinition p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseAggregate(Aggregate p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseWindow(Window p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseExpand(Expand p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T casePivot(Pivot p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	// import??
//	@Override
//	public T caseUnpivot(Unpivot p) {
//		accept(p.child());
//		return caseUnaryNode(p);
//	}

	@Override
	public T caseGlobalLimit(GlobalLimit p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseSample(Sample p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseDistinct(Distinct p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	/**
	 * direct-sub-classes:
	 * </PRE>
	 * Repartition
	 * RepartitionByExpression
	 * </PRE>
	 */
	@Override
	public T caseRepartitionOperation(RepartitionOperation p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseRepartition(Repartition p) {
		accept(p.child());
		return caseRepartitionOperation(p);
	}

	@Override
	public T caseRepartitionByExpression(RepartitionByExpression p) {
		accept(p.child());
		return caseRepartitionOperation(p);
	}

	@Override
	public T caseRebalancePartitions(RebalancePartitions p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseDeduplicate(Deduplicate p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseCollectMetrics(CollectMetrics p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseDomainJoin(DomainJoin p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseLateralJoin(LateralJoin p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseEventTimeWatermark(EventTimeWatermark p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseUnresolvedHint(UnresolvedHint p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseResolvedHint(ResolvedHint p) {
		accept(p.child());
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
	@Override
	public T caseOrderPreservingUnaryNode(OrderPreservingUnaryNode p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseObjectConsumer(ObjectConsumer p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseDeserializeToObject(DeserializeToObject p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapPartitionsInRWithArrow(MapPartitionsInRWithArrow p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseTypedFilter(TypedFilter p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseAppendColumns(AppendColumns p) {
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapGroups(MapGroups p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInR(FlatMapGroupsInR p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInRWithArrow(FlatMapGroupsInRWithArrow p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseFlatMapGroupsInPandas(FlatMapGroupsInPandas p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T caseMapInPandas(MapInPandas p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	@Override
	public T casePythonMapInArrow(PythonMapInArrow p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

//	// import?
//	@Override
//	public T caseFlatMapGroupsInPandasWithState(FlatMapGroupsInPandasWithState p) {
//		accept(p.child());
//		return caseUnaryNode(p);
//	}

	@Override
	public T caseBaseEvalPython(BaseEvalPython p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	public T caseAttachDistributedSequence(AttachDistributedSequence p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	public T caseScriptTransformation(ScriptTransformation p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	public T caseWriteToStream(WriteToStream p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	public T caseWriteToStreamStatement(WriteToStreamStatement p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	// @deprecated
	public T caseWriteToDataSourceV2(WriteToDataSourceV2 p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	// import?
//	public T caseWriteFiles(WriteFiles p) {
//		accept(p.child());
//		return caseUnaryNode(p);
//	}

	public T caseWriteToContinuousDataSource(WriteToContinuousDataSource p) {
		accept(p.child());
		return caseUnaryNode(p);
	}

	public T caseWriteToMicroBatchDataSource(WriteToMicroBatchDataSource p) {
		accept(p.child());
		accept(p.child());
		return caseUnaryNode(p);
	}

	// import?
//	public T caseWriteToMicroBatchDataSourceV1(WriteToMicroBatchDataSourceV1 p) {
//		accept(p.child());
//		return caseUnaryNode(p);
//	}

	// BinaryNode sub-classes
	// ------------------------------------------------------------------------
		
	public T caseOrderedJoin(OrderedJoin p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}
	
	/** abstract, sub-classes:
	 * <PRE>
	 * Except
	 * Intersect
	 * </PRE>
	 */
	public T caseSetOperation(SetOperation p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}

	public T caseExcept(Except p) {
		accept(p.left());
		accept(p.right());
		return caseSetOperation(p);
	}

	public T caseIntersect(Intersect p) {
		accept(p.left());
		accept(p.right());
		return caseSetOperation(p);
	}

	public T caseJoin(Join p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}
	
	public T caseAsOfJoin(AsOfJoin p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}

	public T caseFlatMapGroupsWithState(FlatMapGroupsWithState p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}
	
	public T caseCoGroup(CoGroup p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}

	public T caseFlatMapCoGroupsInPandas(FlatMapCoGroupsInPandas p) {
		accept(p.left());
		accept(p.right());
		return caseBinaryNode(p);
	}
	
}

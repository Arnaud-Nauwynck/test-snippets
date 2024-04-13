package fr.an.tests.spark.sql;

import org.apache.spark.sql.connector.expressions.aggregate.Avg;
import org.apache.spark.sql.connector.expressions.aggregate.Count;
import org.apache.spark.sql.connector.expressions.aggregate.CountStar;
import org.apache.spark.sql.connector.expressions.aggregate.GeneralAggregateFunc;
import org.apache.spark.sql.connector.expressions.aggregate.Max;
import org.apache.spark.sql.connector.expressions.aggregate.Min;

public abstract class ExpressionVisitor<T> {

//	public abstract T caseAvg(Avg p);
//	public abstract T caseCount(Count p);
//	public abstract T caseCountStar(CountStar p);
//	public abstract T caseGeneralAggregateFunc(GeneralAggregateFunc p);
//	public abstract T caseMax(Max p);
//	public abstract T caseMin(Min p);
//	public abstract T caseSum(Sum p);
	
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/Cast.java:public class Cast extends ExpressionWithToString {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/Extract.java:public class Extract extends ExpressionWithToString {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/GeneralScalarExpression.java:public class GeneralScalarExpression extends ExpressionWithToString {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/Literal.java:public interface Literal<T> extends Expression {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/NamedReference.java:public interface NamedReference extends Expression {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/SortOrder.java:public interface SortOrder extends Expression {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/Transform.java:public interface Transform extends Expression {
//	catalyst/src/main/java/org/apache/spark/sql/connector/expressions/UserDefinedScalarFunc.java:public class UserDefinedScalarFunc extends ExpressionWithToString {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/analysis/unresolved.scala:  extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/dsl/package.scala:  object expressions extends ExpressionConversions  // scalastyle:ignore
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/aggregate/Average.scala:object TryAverageExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/aggregate/interfaces.scala:  extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/aggregate/interfaces.scala:abstract class AggregateFunction extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/aggregate/Sum.scala:object TrySumExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/ApplyFunctionExpression.scala:  extends Expression with UserDefinedExpression with CodegenFallback with ImplicitCastInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/codegen/CodegenFallback.scala:trait CodegenFallback extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/collectionOperations.scala:  extends Expression with ExpectsInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/collectionOperations.scala:    nullReplacement: Option[Expression]) extends Expression with ExpectsInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/collectionOperations.scala:  extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/complexTypeCreator.scala:  extends Expression with NoThrow {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/complexTypeCreator.scala:  extends Expression with NoThrow {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/complexTypeCreator.scala:case class CreateNamedStruct(children: Seq[Expression]) extends Expression with NoThrow {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/complexTypeCreator.scala:trait StructFieldsOperation extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/complexTypeExtractors.scala:trait ExtractValue extends Expression with NullIntolerant {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:trait TimeZoneAwareExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object CurDateExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object ParseToTimestampNTZExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object ParseToTimestampLTZExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object TryToTimestampExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object MakeTimestampNTZExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object MakeTimestampLTZExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/datetimeExpressions.scala:object DatePartExpressionBuilder extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/ExpectsInputTypes.scala:trait ExpectsInputTypes extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait Unevaluable extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait RuntimeReplaceable extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait NonSQLExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait Nondeterministic extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait ConditionalExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class LeafExpression extends Expression with LeafLike[Expression]
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class UnaryExpression extends Expression with UnaryLike[Expression] {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait SupportQueryContext extends Expression with Serializable { catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class BinaryExpression extends Expression with BinaryLike[Expression] {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class TernaryExpression extends Expression with TernaryLike[Expression] {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class QuaternaryExpression extends Expression with QuaternaryLike[Expression] {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class QuinaryExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:abstract class SeptenaryExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait ComplexTypeMergingExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/Expression.scala:trait CommutativeExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/generators.scala:trait Generator extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/grouping.scala:trait BaseGroupingSets extends Expression with CodegenFallback {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/grouping.scala:case class Grouping(child: Expression) extends Expression with Unevaluable
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/grouping.scala:case class GroupingID(groupByExprs: Seq[Expression]) extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/hash.scala:abstract class HashExpression[E] extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/higherOrderFunctions.scala:  extends Expression with CodegenFallback {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/higherOrderFunctions.scala:trait HigherOrderFunction extends Expression with ExpectsInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/mathExpressions.scala:trait CeilFloorExpressionBuilderBase extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/namedExpressions.scala:trait NamedExpression extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:trait InvokeLike extends Expression with NonSQLExpression with ImplicitCastInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:    customCollectionCls: Option[Class[_]]) extends Expression with NonSQLExpression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:    collClass: Class[_]) extends Expression with NonSQLExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:  extends Expression with NonSQLExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:  extends Expression with NonSQLExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/objects/objects.scala:  extends Expression with NonSQLExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/package.scala:  trait NullIntolerant extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/PartitionTransforms.scala:abstract class PartitionTransformExpression extends Expression with Unevaluable
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/predicates.scala:abstract class BasePredicate extends ExpressionsEvaluator {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/predicates.scala:trait Predicate extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/PythonUDF.scala:  extends Expression with Unevaluable with NonSQLExpression with UserDefinedExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/PythonUDF.scala:  extends Expression with Unevaluable with NonSQLExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/randomExpressions.scala:trait ExpressionWithRandomSeed extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/ScalaUDF.scala:  extends Expression with NonSQLExpression with UserDefinedExpression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/SessionWindow.scala:case class SessionWindow(timeColumn: Expression, gapDuration: Expression) extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/SortOrder.scala:  extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:  extends Expression with ImplicitCastInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:    failOnError: Boolean = SQLConf.get.ansiEnabled) extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:trait StringBinaryPredicateExpressionBuilderBase extends ExpressionBuilder {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:trait String2TrimExpression extends Expression with ImplicitCastInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:trait PadExpressionBuilderBase extends ExpressionBuilder {catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/stringExpressions.scala:case class FormatString(children: Expression*) extends Expression with ImplicitCastInputTypes {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/subquery.scala:abstract class PlanExpression[T <: QueryPlan[_]] extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/TransformExpression.scala:    numBucketsOpt: Option[Int] = None) extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/urlExpressions.scala:  extends Expression with ExpectsInputTypes with CodegenFallback {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/windowExpressions.scala:    frameSpecification: WindowFrame) extends Expression with WindowSpec with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/windowExpressions.scala:sealed trait WindowFrame extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/windowExpressions.scala:    windowSpec: WindowSpecDefinition) extends Expression with Unevaluable
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/windowExpressions.scala:trait WindowFunction extends Expression {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/v2Commands.scala:sealed abstract class MergeAction extends Expression with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/logical/v2Commands.scala:case class Assignment(key: Expression, value: Expression) extends Expression
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/physical/partitioning.scala:  extends Expression with Partitioning with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/physical/partitioning.scala:  extends Expression with Partitioning with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/catalyst/plans/physical/partitioning.scala:  extends Expression with Partitioning with Unevaluable {
//	catalyst/src/main/scala/org/apache/spark/sql/internal/connector/ExpressionWithToString.scala:abstract class ExpressionWithToString extends Expression with Serializable {
//	hive/src/main/scala/org/apache/spark/sql/hive/hiveUDFs.scala:  extends Expression
//	hive/src/main/scala/org/apache/spark/sql/hive/hiveUDFs.scala:  extends Expression
		
}

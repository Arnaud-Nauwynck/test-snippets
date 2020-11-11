
cf https://jaceklaskowski.gitbooks.io/mastering-spark-sql/content/spark-sql-debugging-query-execution.html

import org.apache.spark.sql.execution.debug._

val ds = spark.sql("select * from db1.t1")
ds.debug

scala> ds.debug
	Results returned: 1
	== WholeStageCodegen (1) ==
	Tuples output: 1
	 col_str StringType: {org.apache.spark.unsafe.types.UTF8String}
	 col_int IntegerType: {java.lang.Integer}
	== ColumnarToRow ==
	Tuples output: 0
	 col_str StringType: {}
	 col_int IntegerType: {}
	== InputAdapter ==
	Tuples output: 0
	 col_str StringType: {}
	 col_int IntegerType: {}
	== FileScan parquet db1.t1[col_str#41,col_int#42] Batched: true, DataFilters: [], Format: Parquet, Location: InMemoryFileIndex[file:/D:/arn/hadoop/rootfs/db1/t1], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<col_str:string,col_int:int> ==
	Tuples output: 0
	 col_str StringType: {}
	 col_int IntegerType: {}

scala> ds.debugCodegen	 

	Found 1 WholeStageCodegen subtrees.
	== Subtree 1 / 1 (maxMethodCodeSize:257; maxConstantPoolSize:139(0,21% used); numInnerClasses:0) ==
	*(1) ColumnarToRow
	+- FileScan parquet db1.t1[col_str#41,col_int#42] Batched: true, DataFilters: [], Format: Parquet, Location: InMemoryFileIndex[file:/D:/arn/hadoop/rootfs/db1/t1], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<col_str:string,col_int:int>
	
	Generated code:
	/* 001 */ public Object generate(Object[] references) {
	/* 002 */   return new GeneratedIteratorForCodegenStage1(references);
	/* 003 */ }
	/* 004 */
	/* 005 */ // codegenStageId=1
	/* 006 */ final class GeneratedIteratorForCodegenStage1 extends org.apache.spark.sql.execution.BufferedRowIterator {
	/* 007 */   private Object[] references;
	/* 008 */   private scala.collection.Iterator[] inputs;
	/* 009 */   private int columnartorow_batchIdx_0;
	/* 010 */   private org.apache.spark.sql.execution.vectorized.OnHeapColumnVector[] columnartorow_mutableStateArray_2 = new org.apache.spark.sql.execution.vectorized.OnHeapColumnVector[2];
	/* 011 */   private org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[] columnartorow_mutableStateArray_3 = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[1];
	/* 012 */   private org.apache.spark.sql.vectorized.ColumnarBatch[] columnartorow_mutableStateArray_1 = new org.apache.spark.sql.vectorized.ColumnarBatch[1];
	/* 013 */   private scala.collection.Iterator[] columnartorow_mutableStateArray_0 = new scala.collection.Iterator[1];
	/* 014 */
	/* 015 */   public GeneratedIteratorForCodegenStage1(Object[] references) {
	/* 016 */     this.references = references;
	/* 017 */   }
	/* 018 */
	/* 019 */   public void init(int index, scala.collection.Iterator[] inputs) {
	/* 020 */     partitionIndex = index;
	/* 021 */     this.inputs = inputs;
	/* 022 */     columnartorow_mutableStateArray_0[0] = inputs[0];
	/* 023 */
	/* 024 */     columnartorow_mutableStateArray_3[0] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter(2, 32);
	/* 025 */
	/* 026 */   }
	/* 027 */
	/* 028 */   private void columnartorow_nextBatch_0() throws java.io.IOException {
	/* 029 */     if (columnartorow_mutableStateArray_0[0].hasNext()) {
	/* 030 */       columnartorow_mutableStateArray_1[0] = (org.apache.spark.sql.vectorized.ColumnarBatch)columnartorow_mutableStateArray_0[0].next();
	/* 031 */       ((org.apache.spark.sql.execution.metric.SQLMetric) references[1] /* numInputBatches */).add(1);
	/* 032 */       ((org.apache.spark.sql.execution.metric.SQLMetric) references[0] /* numOutputRows */).add(columnartorow_mutableStateArray_1[0].numRows());
	/* 033 */       columnartorow_batchIdx_0 = 0;
	/* 034 */       columnartorow_mutableStateArray_2[0] = (org.apache.spark.sql.execution.vectorized.OnHeapColumnVector) columnartorow_mutableStateArray_1[0].column(0);
	/* 035 */       columnartorow_mutableStateArray_2[1] = (org.apache.spark.sql.execution.vectorized.OnHeapColumnVector) columnartorow_mutableStateArray_1[0].column(1);
	/* 036 */
	/* 037 */     }
	/* 038 */   }
	/* 039 */
	/* 040 */   protected void processNext() throws java.io.IOException {
	/* 041 */     if (columnartorow_mutableStateArray_1[0] == null) {
	/* 042 */       columnartorow_nextBatch_0();
	/* 043 */     }
	/* 044 */     while ( columnartorow_mutableStateArray_1[0] != null) {
	/* 045 */       int columnartorow_numRows_0 = columnartorow_mutableStateArray_1[0].numRows();
	/* 046 */       int columnartorow_localEnd_0 = columnartorow_numRows_0 - columnartorow_batchIdx_0;
	/* 047 */       for (int columnartorow_localIdx_0 = 0; columnartorow_localIdx_0 < columnartorow_localEnd_0; columnartorow_localIdx_0++) {
	/* 048 */         int columnartorow_rowIdx_0 = columnartorow_batchIdx_0 + columnartorow_localIdx_0;
	/* 049 */         boolean columnartorow_isNull_0 = columnartorow_mutableStateArray_2[0].isNullAt(columnartorow_rowIdx_0);
	/* 050 */         UTF8String columnartorow_value_0 = columnartorow_isNull_0 ? null : (columnartorow_mutableStateArray_2[0].getUTF8String(columnartorow_rowIdx_0));
	/* 051 */         boolean columnartorow_isNull_1 = columnartorow_mutableStateArray_2[1].isNullAt(columnartorow_rowIdx_0);
	/* 052 */         int columnartorow_value_1 = columnartorow_isNull_1 ? -1 : (columnartorow_mutableStateArray_2[1].getInt(columnartorow_rowIdx_0));
	/* 053 */         columnartorow_mutableStateArray_3[0].reset();
	/* 054 */
	/* 055 */         columnartorow_mutableStateArray_3[0].zeroOutNullBytes();
	/* 056 */
	/* 057 */         if (columnartorow_isNull_0) {
	/* 058 */           columnartorow_mutableStateArray_3[0].setNullAt(0);
	/* 059 */         } else {
	/* 060 */           columnartorow_mutableStateArray_3[0].write(0, columnartorow_value_0);
	/* 061 */         }
	/* 062 */
	/* 063 */         if (columnartorow_isNull_1) {
	/* 064 */           columnartorow_mutableStateArray_3[0].setNullAt(1);
	/* 065 */         } else {
	/* 066 */           columnartorow_mutableStateArray_3[0].write(1, columnartorow_value_1);
	/* 067 */         }
	/* 068 */         append((columnartorow_mutableStateArray_3[0].getRow()));
	/* 069 */         if (shouldStop()) { columnartorow_batchIdx_0 = columnartorow_rowIdx_0 + 1; return; }
	/* 070 */       }
	/* 071 */       columnartorow_batchIdx_0 = columnartorow_numRows_0;
	/* 072 */       columnartorow_mutableStateArray_1[0] = null;
	/* 073 */       columnartorow_nextBatch_0();
	/* 074 */     }
	/* 075 */   }
	/* 076 */
	/* 077 */ }
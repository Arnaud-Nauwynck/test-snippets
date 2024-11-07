package fr.an.sample.impl;

import lombok.val;
import org.apache.spark.api.java.function.MapFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FooWithDeserializingLambda {

    /**
     * see javadoc of
     * @see java.lang.invoke.SerializedLambda
     * <PRE>
     * SerializedLambda has a readResolve method that looks for a (possibly private) static method
     * called $deserializeLambda$(SerializedLambda) in the capturing class,
     * invokes that with itself as the first argument, and returns the result.
     * Lambda classes implementing {@code $deserializeLambda$} are responsible for validating
     * that the properties of the {@code SerializedLambda} are consistent with a
     * lambda actually captured by that class.
     * </PRE>
     *
     * example of callstack of SerializedLambda constructor
     * ... called from spark driver while serializing a task to send
     * <PRE>
     * <init>:148, SerializedLambda (java.lang.invoke), SerializedLambda.java
     * writeReplace:-1, LocalTableScanExec$$Lambda$1974/0x0000000801c08968 (org.apache.spark.sql.execution), Unknown Source
     * invokeSpecial:-1, DirectMethodHandle$Holder (java.lang.invoke), DirectMethodHandle$Holder
     * invoke:-1, LambdaForm$MH/0x0000000801164800 (java.lang.invoke), LambdaForm$MH
     * invokeExact_MT:-1, Invokers$Holder (java.lang.invoke), Invokers$Holder
     * invokeImpl:154, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:104, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:578, Method (java.lang.reflect), Method.java
     * inspect:517, IndylambdaScalaClosures$ (org.apache.spark.util), ClosureCleaner.scala
     * getSerializationProxy:498, IndylambdaScalaClosures$ (org.apache.spark.util), ClosureCleaner.scala
     * clean:216, ClosureCleaner$ (org.apache.spark.util), ClosureCleaner.scala
     * clean:163, ClosureCleaner$ (org.apache.spark.util), ClosureCleaner.scala
     * clean:2674, SparkContext (org.apache.spark), SparkContext.scala
     * $anonfun$map$1:415, RDD (org.apache.spark.rdd), RDD.scala
     * apply:-1, RDD$$Lambda$1975/0x0000000801c09560 (org.apache.spark.rdd), Unknown Source
     * withScope:151, RDDOperationScope$ (org.apache.spark.rdd), RDDOperationScope.scala
     * withScope:112, RDDOperationScope$ (org.apache.spark.rdd), RDDOperationScope.scala
     * withScope:407, RDD (org.apache.spark.rdd), RDD.scala
     * map:414, RDD (org.apache.spark.rdd), RDD.scala
     * doExecute:60, LocalTableScanExec (org.apache.spark.sql.execution), LocalTableScanExec.scala
     * $anonfun$execute$1:195, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * apply:-1, SparkPlan$$Lambda$1949/0x0000000801beee68 (org.apache.spark.sql.execution), Unknown Source
     * $anonfun$executeQuery$1:246, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * apply:-1, SparkPlan$$Lambda$1854/0x0000000801bc22b8 (org.apache.spark.sql.execution), Unknown Source
     * withScope:151, RDDOperationScope$ (org.apache.spark.rdd), RDDOperationScope.scala
     * executeQuery:243, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * execute:191, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * inputRDD$lzycompute:141, ShuffleExchangeExec (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * inputRDD:141, ShuffleExchangeExec (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * mapOutputStatisticsFuture$lzycompute:146, ShuffleExchangeExec (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * mapOutputStatisticsFuture:145, ShuffleExchangeExec (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * $anonfun$submitShuffleJob$1:73, ShuffleExchangeLike (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * apply:-1, ShuffleExchangeLike$$Lambda$1823/0x0000000801b919c0 (org.apache.spark.sql.execution.exchange), Unknown Source
     * $anonfun$executeQuery$1:246, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * apply:-1, SparkPlan$$Lambda$1854/0x0000000801bc22b8 (org.apache.spark.sql.execution), Unknown Source
     * withScope:151, RDDOperationScope$ (org.apache.spark.rdd), RDDOperationScope.scala
     * executeQuery:243, SparkPlan (org.apache.spark.sql.execution), SparkPlan.scala
     * submitShuffleJob:73, ShuffleExchangeLike (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * submitShuffleJob$:72, ShuffleExchangeLike (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * submitShuffleJob:120, ShuffleExchangeExec (org.apache.spark.sql.execution.exchange), ShuffleExchangeExec.scala
     * shuffleFuture$lzycompute:187, ShuffleQueryStageExec (org.apache.spark.sql.execution.adaptive), QueryStageExec.scala
     * shuffleFuture:187, ShuffleQueryStageExec (org.apache.spark.sql.execution.adaptive), QueryStageExec.scala
     * doMaterialize:189, ShuffleQueryStageExec (org.apache.spark.sql.execution.adaptive), QueryStageExec.scala
     * materialize:61, QueryStageExec (org.apache.spark.sql.execution.adaptive), QueryStageExec.scala
     * $anonfun$getFinalPhysicalPlan$5:286, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * $anonfun$getFinalPhysicalPlan$5$adapted:284, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * apply:-1, AdaptiveSparkPlanExec$$Lambda$1821/0x0000000801b91350 (org.apache.spark.sql.execution.adaptive), Unknown Source
     * foreach:1856, Vector (scala.collection.immutable), Vector.scala
     * $anonfun$getFinalPhysicalPlan$1:284, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * apply:-1, AdaptiveSparkPlanExec$$Lambda$1789/0x0000000801b84888 (org.apache.spark.sql.execution.adaptive), Unknown Source
     * withActive:900, SparkSession (org.apache.spark.sql), SparkSession.scala
     * getFinalPhysicalPlan:256, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * withFinalPlanUpdate:401, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * executeCollect:374, AdaptiveSparkPlanExec (org.apache.spark.sql.execution.adaptive), AdaptiveSparkPlanExec.scala
     * collectFromPlan:4344, Dataset (org.apache.spark.sql), Dataset.scala
     * $anonfun$head$1:3326, Dataset (org.apache.spark.sql), Dataset.scala
     * apply:-1, Dataset$$Lambda$1473/0x0000000801a8e2d0 (org.apache.spark.sql), Unknown Source
     * $anonfun$withAction$2:4334, Dataset (org.apache.spark.sql), Dataset.scala
     * apply:-1, Dataset$$Lambda$1777/0x0000000801b7f810 (org.apache.spark.sql), Unknown Source
     * withInternalError:546, QueryExecution$ (org.apache.spark.sql.execution), QueryExecution.scala
     * $anonfun$withAction$1:4332, Dataset (org.apache.spark.sql), Dataset.scala
     * apply:-1, Dataset$$Lambda$1475/0x0000000801a8eb58 (org.apache.spark.sql), Unknown Source
     * $anonfun$withNewExecutionId$6:125, SQLExecution$ (org.apache.spark.sql.execution), SQLExecution.scala
     * apply:-1, SQLExecution$$$Lambda$1486/0x0000000801a921d8 (org.apache.spark.sql.execution), Unknown Source
     * withSQLConfPropagated:201, SQLExecution$ (org.apache.spark.sql.execution), SQLExecution.scala
     * $anonfun$withNewExecutionId$1:108, SQLExecution$ (org.apache.spark.sql.execution), SQLExecution.scala
     * apply:-1, SQLExecution$$$Lambda$1476/0x0000000801a8ee08 (org.apache.spark.sql.execution), Unknown Source
     * withActive:900, SparkSession (org.apache.spark.sql), SparkSession.scala
     * withNewExecutionId:66, SQLExecution$ (org.apache.spark.sql.execution), SQLExecution.scala
     * withAction:4332, Dataset (org.apache.spark.sql), Dataset.scala
     * head:3326, Dataset (org.apache.spark.sql), Dataset.scala
     * take:3549, Dataset (org.apache.spark.sql), Dataset.scala
     * getRows:280, Dataset (org.apache.spark.sql), Dataset.scala
     * showString:315, Dataset (org.apache.spark.sql), Dataset.scala
     * show:839, Dataset (org.apache.spark.sql), Dataset.scala
     * show:798, Dataset (org.apache.spark.sql), Dataset.scala
     * prepareIntDataset:99, MainApp (fr.an.sample), MainApp.java
     * doRun:74, MainApp (fr.an.sample), MainApp.java
     * run:66, MainApp (fr.an.sample), MainApp.java
     * main:50, MainApp (fr.an.sample), MainApp.java
     * </PRE>
     *
     */
    // DOES NOT COMPILE!!! synthetic method is conflicting with generated method of compiler (name reserved for hotspot?  so not generated by javac ?) ?!!
    // nothing found in real generated ".class" bytecode
    // javap -v target/classes/fr/an/sample/impl/FooWithDeserializingLambda.class > src/main/j ava/fr/an/sample/impl/FooWithDeserializingLambda.class-javap.txt
    protected static Object ____$deserializeLambda$(SerializedLambda ser) {
        System.out.println("PSEUDO CODE that <<could>> be called by introspection $deserializeLambda$(obj: " + ser + ")" +
                " => return lambda 'singleton' if identical to FooUtil::foo");
        if (ser.getCapturingClass().equals(FooWithDeserializingLambda.class.getName().replace('.', '/'))
            && ser.getCapturedArgCount() == 0
            && ser.getFunctionalInterfaceMethodName().equals(MapFunction.class.getName())
//                && ser.getImplClass().equals("???")
//                && ser.getImplMethodName().equals("????")
                ) {
            return (MapFunction<Integer, Integer>) FooUtils::foo;
        } else {
            System.out.println("should not occur: unrecognized lambda??");
            return null;
        }
    }

    public static void testLambda_writeReplace_to_SerializedLambda_then_readResolve() {
        System.out.println("test Lambda with writeReplace() to SerializedLambda, then readResolve");
        MapFunction<Integer, Integer> mapFuncObj = FooUtils::foo;

        SerializedLambda serializedLambda = invokeWriteReplaceLambda(mapFuncObj);
        // stdout result
        // serializedLambda: SerializedLambda[
        //    capturingClass=class fr.an.sample.FooWithDeserializingLambda,
        //    functionalInterfaceMethod=org/apache/spark/api/java/function/MapFunction.call:(Ljava/lang/Object;)Ljava/lang/Object;,
        //    implementation=invokeStatic fr/an/sample/impl/FooUtils.foo:(Ljava/lang/Integer;)Ljava/lang/Integer;,
        //    instantiatedMethodType=(Ljava/lang/Integer;)Ljava/lang/Integer;,
        //    numCaptured=0
        //    ]
        System.out.println("serializedLambda: " + serializedLambda);

        // stdout result
        //   class: fr/an/sample/impl/FooUtils
        //   methodName: foo
        //   methodSignature: (Ljava/lang/Integer;)Ljava/lang/Integer;
        //   methodKind: 6
        System.out.println("serializedLambda impl \n" + //
                "  class: " + serializedLambda.getImplClass() + "\n" + //
                "  methodName: " + serializedLambda.getImplMethodName() + "\n" + //
                "  methodSignature: " + serializedLambda.getImplMethodSignature() + "\n" + //
                "  methodKind: " + serializedLambda.getImplMethodKind());

        Object readResolvedObject = invokeReadResolveSerializedLambda(serializedLambda);
        MapFunction<Integer, Integer> resolvedMapFunc = (MapFunction<Integer, Integer>) readResolvedObject;

        if (mapFuncObj == resolvedMapFunc) {
            System.out.println("should not occur: expecting 2 different instances after readResolve");
        }

        Object readResolvedAgainObject = invokeReadResolveSerializedLambda(serializedLambda);
        MapFunction<Integer, Integer> resolvedAgainMapFunc = (MapFunction<Integer, Integer>) readResolvedAgainObject;
        if (resolvedAgainMapFunc != resolvedMapFunc) {
            System.out.println("should not occur: expecting SAME instance after readResolve again");
        }

        // checking both MapFunction works
        try {
            int res1 = mapFuncObj.call(1);

            int checkRes1 = resolvedMapFunc.call(1);

            int checkRes2 = FooUtils.foo(1);
            if (res1 != checkRes1 || res1 != checkRes2) {
                throw new RuntimeException("should not occur.. different results");
            }
        } catch(Exception ex) {
            throw new RuntimeException("should not occur", ex);
        }

        testLambda_SerializedLambda_for_inline();
    }

    public static void testLambda_SerializedLambda_for_inline() {
        MapFunction<Integer, Integer> func = x -> 2345 * x;
        System.out.println("SerializedLambda for an inlined code (x -> 2345*x)");
        SerializedLambda serializedLambda = invokeWriteReplaceLambda(func);

        // stdoutput:
        // serializedLambda: SerializedLambda[
        //         capturingClass=class fr.an.sample.FooWithDeserializingLambda,
        //         functionalInterfaceMethod=org/apache/spark/api/java/function/MapFunction.call:(Ljava/lang/Object;)Ljava/lang/Object;,
        //         implementation=invokeStatic fr/an/sample/FooWithDeserializingLambda.lambda$testLambda_SerializedLambda_for_inline$c9da406d$1:(Ljava/lang/Integer;)Ljava/lang/Integer;,
        //         instantiatedMethodType=(Ljava/lang/Integer;)Ljava/lang/Integer;,
        //         numCaptured=0
        //         ]
        System.out.println("serializedLambda: " + serializedLambda);
        // stdoutput:
        // serializedLambda impl
        //    class: fr/an/sample/FooWithDeserializingLambda
        //    methodName: lambda$testLambda_SerializedLambda_for_inline$c9da406d$1
        //    methodSignature: (Ljava/lang/Integer;)Ljava/lang/Integer;
        //    methodKind: 6
        System.out.println("serializedLambda impl \n" + //
                "  class: " + serializedLambda.getImplClass() + "\n" + //
                "  methodName: " + serializedLambda.getImplMethodName() + "\n" + //
                "  methodSignature: " + serializedLambda.getImplMethodSignature() + "\n" + //
                "  methodKind: " + serializedLambda.getImplMethodKind());
    }

    public static SerializedLambda invokeWriteReplaceLambda(Serializable lambda) {
        Method writeReplaceMeth;
        try {
            writeReplaceMeth = lambda.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("should not occur", e);
        }
        writeReplaceMeth.setAccessible(true);
        try {
            return (SerializedLambda) writeReplaceMeth.invoke(lambda);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException("should not occur", e);
        }
    }

    public static Object invokeReadResolveSerializedLambda(SerializedLambda ser) {
        String capturingClassName = ser.getCapturingClass().replace("/", ".");
        val cl = Thread.currentThread().getContextClassLoader();
        Class<?> capturingClass;
        try {
            capturingClass = cl.loadClass(capturingClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("should not occur", e);
        }
        Method deserializeLambdaMeth;
        try {
            deserializeLambdaMeth = capturingClass.getDeclaredMethod("$deserializeLambda$", SerializedLambda.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("should not occur", e);
        }
        deserializeLambdaMeth.setAccessible(true);

        try {
            return deserializeLambdaMeth.invoke(null, ser);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("should not occur", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("should not occur", e);
        }
    }

}

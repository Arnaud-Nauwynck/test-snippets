package fr.an.sample.impl;

import lombok.val;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class FooObjWithReadResolve implements Serializable {

    /**
     * calling stack trace:
     * <PRE>
     * readResolve:15, FooObjWithReadResolve (fr.an.sample.impl), FooObjWithReadResolve.java
     * invokeVirtual:-1, DirectMethodHandle$Holder (java.lang.invoke), DirectMethodHandle$Holder
     * invoke:-1, LambdaForm$MH/0x0000000801164800 (java.lang.invoke), LambdaForm$MH
     * invokeExact_MT:-1, Invokers$Holder (java.lang.invoke), Invokers$Holder
     * invokeImpl:154, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:104, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:578, Method (java.lang.reflect), Method.java
     * invokeReadResolve:1190, ObjectStreamClass (java.io), ObjectStreamClass.java
     * readOrdinaryObject:2289, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readArray:2182, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1748, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readArray:2182, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1748, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readArray:2182, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1748, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readArray:2182, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1748, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:538, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:496, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:58, DefaultSerializationProxy (scala.collection.generic), DefaultSerializationProxy.scala
     * invokeSpecial:-1, LambdaForm$DMH/0x000000080126e400 (java.lang.invoke), LambdaForm$DMH
     * invoke:-1, LambdaForm$MH/0x0000000801e30c00 (java.lang.invoke), LambdaForm$MH
     * invokeExact_MT:-1, Invokers$Holder (java.lang.invoke), Invokers$Holder
     * invokeImpl:155, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:104, DirectMethodHandleAccessor (jdk.internal.reflect), DirectMethodHandleAccessor.java
     * invoke:578, Method (java.lang.reflect), Method.java
     * invokeReadObject:1100, ObjectStreamClass (java.io), ObjectStreamClass.java
     * readSerialData:2440, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * <init>:2614, ObjectInputStream$FieldValues (java.io), ObjectInputStream.java
     * readSerialData:2465, ObjectInputStream (java.io), ObjectInputStream.java
     * readOrdinaryObject:2280, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject0:1760, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:538, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:496, ObjectInputStream (java.io), ObjectInputStream.java
     * readObject:87, JavaDeserializationStream (org.apache.spark.serializer), JavaSerializer.scala
     * deserialize:129, JavaSerializerInstance (org.apache.spark.serializer), JavaSerializer.scala
     * runTask:86, ResultTask (org.apache.spark.scheduler), ResultTask.scala
     * runTaskWithListeners:161, TaskContext (org.apache.spark), TaskContext.scala
     * run:141, Task (org.apache.spark.scheduler), Task.scala
     * $anonfun$run$4:620, Executor$TaskRunner (org.apache.spark.executor), Executor.scala
     * apply:-1, Executor$TaskRunner$$Lambda$2372/0x0000000801cb92b0 (org.apache.spark.executor), Unknown Source
     * tryWithSafeFinally:64, SparkErrorUtils (org.apache.spark.util), SparkErrorUtils.scala
     * tryWithSafeFinally$:61, SparkErrorUtils (org.apache.spark.util), SparkErrorUtils.scala
     * tryWithSafeFinally:94, Utils$ (org.apache.spark.util), Utils.scala
     * run:623, Executor$TaskRunner (org.apache.spark.executor), Executor.scala
     * runWorker:1144, ThreadPoolExecutor (java.util.concurrent), ThreadPoolExecutor.java
     * run:642, ThreadPoolExecutor$Worker (java.util.concurrent), ThreadPoolExecutor.java
     * runWith:1636, Thread (java.lang), Thread.java
     * run:1623, Thread (java.lang), Thread.java
     * </PRE>
     *
     * This corresponds to deserializing SerializedLambda.capturedArgs[0]
     * <PRE>
     *     SerializedLambda[
     *          capturingClass=class fr.an.sample.impl.FooObjWithReadResolve,
     *          functionalInterfaceMethod=org/apache/spark/api/java/function/MapFunction.call:(Ljava/lang/Object;)Ljava/lang/Object;,
     *          implementation=invokeVirtual fr/an/sample/impl/FooObjWithReadResolve.lambda$non_static_testMap$485d13cd$1:(Ljava/lang/Integer;)Ljava/lang/Integer;,
     *          instantiatedMethodType=(Ljava/lang/Integer;)Ljava/lang/Integer;,
     *          numCaptured=1,
     *          capturedArgs: [
     *             0 -> {{this}} FooObjWithReadResolve
     *          ]
     *          ]
     * </PRE>
     * where the serializedLambda is the "f" field of MapPartitionsRDD
     *    MapPartitionsRDD.f: (TaskContext, Int, Iterator[T]) => Iterator[U],  // (TaskContext, partition index, iterator)
     */
    protected Object readResolve() throws ObjectStreamException {
        System.out.println("called readResolve() from implicit java.io.Serializable on FooObjWithReadResolve with 'this' instance");
        return this;
    }

    public Integer non_static_foo(Integer x) {
        return 2*x;
    }

    public void non_static_testMap(Dataset<Integer> intDataset) {
        System.out.println("calling intDataset.map((MapFunction<Integer, Integer>) FooObjWithReadResolve::non_static_foo)");
        val mappedDataset = intDataset.map((MapFunction<Integer, Integer>) x -> non_static_foo(x), Encoders.INT());
        mappedDataset.show(3);
    }
}

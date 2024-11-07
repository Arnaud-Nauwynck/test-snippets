package fr.an.sample.impl;

import org.apache.spark.api.java.function.MapFunction;

import java.io.ObjectStreamException;
import java.util.concurrent.atomic.AtomicInteger;

public class FooJavaMapFunction_ReadResolve implements MapFunction<Integer, Integer> { // transitively implements Serializable

    protected static final AtomicInteger readResolveCount = new AtomicInteger(0); // to print detailed stack trace only once

    @Override
    public Integer call(Integer row) {
        return FooUtils.foo(row);
    }

    /**
     * called by introspection, from java.base/java.io.ObjectStreamClass.invokeReadResolve(ObjectStreamClass.java:1190)
     * <PRE>
     * 	at fr.an.sample.impl.FooJavaMapFunction_ReadResolve.readResolve(FooJavaMapFunction_ReadResolve.java:23)
     * 	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
     * 	at java.base/java.lang.reflect.Method.invoke(Method.java:578)
     * 	at java.base/java.io.ObjectStreamClass.invokeReadResolve(ObjectStreamClass.java:1190)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2289)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream.readArray(ObjectInputStream.java:2182)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1748)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream.readArray(ObjectInputStream.java:2182)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1748)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream.readArray(ObjectInputStream.java:2182)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1748)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream.readObject(ObjectInputStream.java:538)
     * 	at java.base/java.io.ObjectInputStream.readObject(ObjectInputStream.java:496)
     * 	at scala.collection.generic.DefaultSerializationProxy.readObject(DefaultSerializationProxy.scala:58)
     * 	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
     * 	at java.base/java.lang.reflect.Method.invoke(Method.java:578)
     * 	at java.base/java.io.ObjectStreamClass.invokeReadObject(ObjectStreamClass.java:1100)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2440)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream$FieldValues.<init>(ObjectInputStream.java:2614)
     * 	at java.base/java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2465)
     * 	at java.base/java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2280)
     * 	at java.base/java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1760)
     * 	at java.base/java.io.ObjectInputStream.readObject(ObjectInputStream.java:538)
     * 	at java.base/java.io.ObjectInputStream.readObject(ObjectInputStream.java:496)
     * 	at org.apache.spark.serializer.JavaDeserializationStream.readObject(JavaSerializer.scala:87)
     * 	at org.apache.spark.serializer.JavaSerializerInstance.deserialize(JavaSerializer.scala:129)
     * 	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:86)
     * 	at org.apache.spark.TaskContext.runTaskWithListeners(TaskContext.scala:161)
     * 	at org.apache.spark.scheduler.Task.run(Task.scala:141)
     * 	at org.apache.spark.executor.Executor$TaskRunner.$anonfun$run$4(Executor.scala:620)
     * 	at org.apache.spark.util.SparkErrorUtils.tryWithSafeFinally(SparkErrorUtils.scala:64)
     * 	at org.apache.spark.util.SparkErrorUtils.tryWithSafeFinally$(SparkErrorUtils.scala:61)
     * 	at org.apache.spark.util.Utils$.tryWithSafeFinally(Utils.scala:94)
     * 	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:623)
     * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
     * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
     * 	at java.base/java.lang.Thread.run(Thread.java:1623)
     * </PRE>
     */
    protected Object readResolve() throws ObjectStreamException {
        int count = readResolveCount.addAndGet(1);
        System.out.println("called [" + count + "] readResolve() from implicit java.io.Serializable");
        if (count == 1) {
            System.out.println(" showing current stack trace only for first call (first evaluation on VM 'executor')");
            new Exception().printStackTrace(System.out);
        }
        return this;
    }

}

package fr.an.sample;

import fr.an.sample.impl.*;
import lombok.val;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fr.an.sample.impl.ObjectSerializationUtils.deserializeObjectFromBytes;
import static fr.an.sample.impl.ObjectSerializationUtils.serializeObjectToBytes;

public class MainApp {

    protected SparkSession spark;

    private Dataset<Integer> intDataset;

    /**
     * Notice you should set the following JVM properties in your launcher
     * <PRE>
     * -Xmx3g
     * --add-opens=java.base/java.lang=ALL-UNNAMED
     * --add-opens=java.base/java.lang.invoke=ALL-UNNAMED
     * --add-opens=java.base/java.lang.reflect=ALL-UNNAMED
     * --add-opens=java.base/java.io=ALL-UNNAMED
     * --add-opens=java.base/java.net=ALL-UNNAMED
     * --add-opens=java.base/java.nio=ALL-UNNAMED
     * --add-opens=java.base/java.util=ALL-UNNAMED
     * --add-opens=java.base/java.util.concurrent=ALL-UNNAMED
     * --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED
     * --add-opens=java.base/sun.nio.ch=ALL-UNNAMED
     * --add-opens=java.base/sun.nio.cs=ALL-UNNAMED
     * --add-opens=java.base/sun.security.action=ALL-UNNAMED
     * --add-opens=java.base/sun.util.calendar=ALL-UNNAMED
     * --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED
     * </PRE>
     * @param args
     */
    public static void main(String[] args) {
        val app = new MainApp();
        try {
            app.run(args);

            System.out.println("Finished .. exiting(0)");
        } catch(Exception ex) {
            System.out.println("FAILED .. exiting(-1)");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public void run(String[] args) {
        this.spark = SparkSession.builder()
                .appName("test")
                .master("local[*]")
                .getOrCreate();
        try {
            doRun();

        } finally {
            spark.stop();
        }
    }

    private void doRun() {
        prepareIntDataset();

        testMapNewFunction();
        testMapNewFunction_ReadResolve();
        testMapLambda();
        testMapLambdaMethodReference();
        testSerializeLambda();
        FooWithDeserializingLambda.testLambda_writeReplace_to_SerializedLambda_then_readResolve();
        FooUtilWithReadResolve.testMap(intDataset);
        new FooObjWithReadResolve().non_static_testMap(intDataset);
    }


    private void prepareIntDataset() {
        List<Integer> intList = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        System.out.println("Dataset<Integer> intDataset = spark.createDataset(intList, Encoders.INT()).repartition(2)");
        this.intDataset = spark.createDataset(intList, Encoders.INT()).repartition(2);

        // will print this... only to "see" spark dataset is ok
        // +-----+
        // |value|
        // +-----+
        // |    0|
        // |    1|
        // |    2|
        // +-----+
        // only showing top 3 rows
        System.out.println("intDataset.show()");
        intDataset.show(3);
    }


    private void testMapNewFunction() {
        System.out.println("calling intDataset.map(new FooJavaMapFunction()))");
        MapFunction<Integer, Integer> myIntMapFunctionObject = new FooJavaMapFunction();
        val mappedDataset = intDataset.map(myIntMapFunctionObject, Encoders.INT());
        mappedDataset.show(3);
    }

    private void testMapNewFunction_ReadResolve() {
        System.out.println("calling intDataset.map(new FooJavaMapFunction_ReadResolve()))");
        MapFunction<Integer, Integer> myIntMapFunctionObject = new FooJavaMapFunction_ReadResolve();
        val mappedDataset = intDataset.map(myIntMapFunctionObject, Encoders.INT());
        mappedDataset.show(3);
    }

    private void testMapLambda() {
        System.out.println("calling intDataset.map((MapFunction<Integer, Integer>) x -> FooUtils.foo(x))");
        val mappedDataset = intDataset.map((MapFunction<Integer, Integer>) x -> FooUtils.foo(x), Encoders.INT());
        mappedDataset.show(3);
    }

    private void testMapLambdaMethodReference() {
        System.out.println("calling intDataset.map((MapFunction<Integer, Integer>) FooUtils::foo)");
        val mappedDataset = intDataset.map((MapFunction<Integer, Integer>) FooUtils::foo, Encoders.INT());
        mappedDataset.show(3);
    }

    private void testSerializeLambda() {
        System.out.println("test serializing Lambda with ObjectOutputStream, and deserializing");
        MapFunction<Integer, Integer> mapFuncObj = (MapFunction<Integer, Integer>) FooUtils::foo;
        System.out.println("new ObjectOutputStream(buffer).writeObject((MapFunction) FooUtils::foo)");

        // Object deserializedObject = serializeThenDeserializeObject(mapFuncObj);
        byte[] bytes = serializeObjectToBytes(mapFuncObj);
        System.out.println("serialized bytes for MapFunction: " + bytes.length + " len");
        // binary content... "�� sr !java.lang.invoke.SerializedLambdaoaД,)6�  I implMethodKind[ capturedArgst [Ljava/lang/Object;L capturingClasst Ljava/lang/Class;L functionalInterfaceClasst Ljava/lang/String;L functionalInterfaceMethodNameq ~ L "functionalInterfaceMethodSignatureq ~ L 	implClassq ~ L implMethodNameq ~ L implMethodSignatureq ~ L instantiatedMethodTypeq ~ xp   ur [Ljava.lang.Object;��X�s)l  xp    vr fr.an.sample.MainApp           xpt .org/apache/spark/api/java/function/MapFunctiont callt &(Ljava/lang/Object;)Ljava/lang/Object;t fr/an/sample/impl/FooUtilst foot ((Ljava/lang/Integer;)Ljava/lang/Integer;q ~ "
        Object deserializedObject = deserializeObjectFromBytes(bytes);

        // check obj is instanceof MapFunction<Integer, Integer>
        MapFunction<Integer, Integer> deserializedMapFuncObj = (MapFunction<Integer, Integer>) deserializedObject;
        // ??? lambda should be readResolved to same singleton VM instance
        if (deserializedMapFuncObj != mapFuncObj) {
            System.out.println("got different MapFunction after serializing/deserializing lambda method ref");
        }
        Class<?> mapFuncObjClass = mapFuncObj.getClass();
        System.out.println("mapFuncObj class: " + mapFuncObjClass.getName());
        Class<?> deserializedObjClass = deserializedMapFuncObj.getClass();
        System.out.println("deserialized class: " + deserializedObjClass.getName());
        if (mapFuncObjClass != deserializedObjClass) {
            System.out.println("got different MapFunction classes after deserializing lambda method ref");
        }

        // check serializing again gives exactly same byte[]
        byte[] serializedAgainBytes = serializeObjectToBytes(mapFuncObj);
        if (! Arrays.equals(bytes, serializedAgainBytes)) {
            System.out.println("should not occur: different serialization bytes array while serializing again");
        }
        // check de-serializing again
        Object deserializedAgainObject = deserializeObjectFromBytes(bytes);
        if (deserializedAgainObject != deserializedMapFuncObj) {
            System.out.println("should not occur: got different deserialized object MapFunction after deserializing again");
        }

        // checking both MapFunction works
        try {
            // call stack inside FooUtils:
            //   foo:6, FooUtils (fr.an.sample.impl), FooUtils.java
            //   call:-1, MainApp$$Lambda$3345/0x0000000801e00d00 (fr.an.sample), Unknown Source
            //   testSerializeLambda:177, MainApp (fr.an.sample), MainApp.java
            int res1 = mapFuncObj.call(1);

            // call stack inside FooUtils:
            //   foo:6, FooUtils (fr.an.sample.impl), FooUtils.java
            //   call:-1, MainApp$$Lambda$3329/0x0000000801e44438 (fr.an.sample), Unknown Source
            //   testSerializeLambda:184, MainApp (fr.an.sample), MainApp.java
            int checkRes1 = deserializedMapFuncObj.call(1);

            int checkRes2 = FooUtils.foo(1);
            if (res1 != checkRes1 || res1 != checkRes2) {
                throw new RuntimeException("should not occur.. different results");
            }
        } catch(Exception ex) {
            throw new RuntimeException("should not occur", ex);
        }
    }

}

package fr.an.sample;

import fr.an.sample.impl.TstBean;
import fr.an.sample.impl.TstParentBean;
import lombok.val;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.spark.sql.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainApp {

    protected SparkSession spark;


    /**
     * Notice you should set the following JVM properties in your launcher
     * <PRE>
     * -Xmx1g
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

            System.out.println("Finished");
        } catch(Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace();
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
        testBeanEncoder();
        testNestedBeanEncoder();
    }

    private void testBeanEncoder() {
        List<TstBean> beanList = IntStream.range(1, 10).boxed()
                .map(i -> TstBean.createMock(i))
                .collect(Collectors.toList());
        Encoder<TstBean> beanEncoder = Encoders.bean(TstBean.class);
        Dataset<TstBean> beanDs = spark.createDataset(beanList, beanEncoder).repartition(1);

        Dataset<Row> df = beanDs.toDF();

        System.out.println("df.show() => printed with columns ordered in alphabetical order");
        // expected stdout:
        // +-----+-----+---+---+---+---+
        // |a    |b    |t  |x  |y  |z  |
        // +-----+-----+---+---+---+---+
        // |test1|false|13 |10 |11 |12 |
        // |test2|true |23 |20 |21 |22 |
        // +-----+-----+---+---+---+---+
        df.show(3, false);

        Dataset<TstBean> beanDs2 = df.as(beanEncoder);

        // same print result on DataFrame
        beanDs2.show(3, false);

        List<TstBean> beanList2 = beanDs2.collectAsList();
        if (! beanList2.equals(beanList)) {
            throw new IllegalStateException("beanList2 is not equal to beanList");
        }

        // load similar data but with columns in correct order as property fields in bean source code:
        // x,y,z,t,a,b,nested
        Dataset<Row> correctOrderColDF = spark.read().format("json").load("src/test/data/tst-bean-correct-fields-order");
        // stdout:  also here, columns are reordered in alphabetical order (!?)
        // +-----+-----+---+---+---+---+
        // |a    |b    |t  |x  |y  |z  |
        // +-----+-----+---+---+---+---+
        // |test1|false|13 |10 |11 |12 |
        // |test2|true |23 |20 |21 |22 |
        // |test3|false|33 |30 |31 |32 |
        // +-----+-----+---+---+---+---+
        correctOrderColDF.show(3, false);

        Dataset<TstBean> correctOrderColDs = correctOrderColDF.as(beanEncoder);

        List<TstBean> correctOrderCoList = correctOrderColDs.collectAsList();
        if (! correctOrderCoList.equals(beanList)) {
            throw new IllegalStateException("correctOrderColDs is not equal to beanList");
        }
    }

    private void testNestedBeanEncoder() {
        List<TstParentBean> beanList = IntStream.range(1, 10).boxed()
                .map(i -> TstParentBean.createMock(i))
                .collect(Collectors.toList());
        Encoder<TstParentBean> beanEncoder = Encoders.bean(TstParentBean.class);
        Dataset<TstParentBean> beanDs = spark.createDataset(beanList, beanEncoder).repartition(1);

        Dataset<Row> df = beanDs.toDF();

        // stdout:
        // +-----+-----+------------------------------+---+---+---+---+
        // |a    |b    |nested                        |t  |x  |y  |z  |
        // +-----+-----+------------------------------+---+---+---+---+
        // |test1|false|{test1, false, 13, 10, 11, 12}|13 |10 |11 |12 |
        // |test2|true |{test2, true, 23, 20, 21, 22} |23 |20 |21 |22 |
        // +-----+-----+------------------------------+---+---+---+---+
        df.show(2, false);

        Dataset<TstParentBean> beanDs2 = df.as(beanEncoder);
        beanDs2.show(3, false);

        List<TstParentBean> beanList2 = beanDs2.collectAsList();
        if (! beanList2.equals(beanList)) {
            throw new IllegalStateException("beanList2 is not equal to beanList");
        }

        // df.withColumns()
        File outDir = new File("target/data/tst-parent");
        if (!outDir.exists()) {
            // Files.deleteIfExists()
            // outDir.delete();
            outDir.mkdirs();
            df.write().format("json").save("target/data/tst-parent");
        }

        // load similar data but with columns in correct order as property fields in bean source code:
        // x,y,z,t,a,b,nested
        Dataset<Row> correctOrderColDF = spark.read().format("json").load("src/test/data/tst-parent-correct-fields-order");
        // expected stdout:  also colums are reordered in alphabetical order (?)
        // +-----+-----+------------------------------+---+---+---+---+
        // |a    |b    |nested                        |t  |x  |y  |z  |
        // +-----+-----+------------------------------+---+---+---+---+
        // |test1|false|{test1, false, 13, 10, 11, 12}|13 |10 |11 |12 |
        // |test2|true |{test2, true, 23, 20, 21, 22} |23 |20 |21 |22 |
        // |test3|false|{test3, false, 33, 30, 31, 32}|33 |30 |31 |32 |
        // +-----+-----+------------------------------+---+---+---+---+
        correctOrderColDF.show(3, false);

        Dataset<TstParentBean> correctOrderColDs = correctOrderColDF.as(beanEncoder);

        List<TstParentBean> correctOrderCoList = correctOrderColDs.collectAsList();
        if (! correctOrderCoList.equals(beanList)) {
            throw new IllegalStateException("correctOrderColDs is not equal to beanList");
        }

        // TODO

    }

}

# Test for spark repartition

# spark-shell script for testing ".repartition().write()" variants

```
val ds = spark.createDataset((1 to 1000000).map(x => (s"$x", x % 100)))

spark.sql("drop table if exists db1.test_part1");
spark.sql("create table db1.test_part1 (col1 string) partitioned by (part1 int) stored as parquet");
ds.repartition(25).write.mode("overwrite").insertInto("db1.test_part1");

sc.setCallSite("INSERT TEST_PART2");
spark.sql("drop table if exists db1.test_part2");
spark.sql("create table db1.test_part2 (col1 string) partitioned by (part1 int) stored as parquet");
ds.repartition($"_2").write.mode("overwrite").insertInto("db1.test_part2");

sc.setCallSite("INSERT TEST_PART3");
spark.sql("drop table if exists db1.test_part3");
spark.sql("create table db1.test_part3 (col1 string) partitioned by (part1 int) stored as parquet");
ds.repartition($"_1").write.mode("overwrite").insertInto("db1.test_part3");

sc.setCallSite("INSERT TEST_PART4");
spark.conf.set("spark.sql.adaptive.enabled", "false");
spark.sql("drop table if exists db1.test_part4");
spark.sql("create table db1.test_part4 (col1 string) partitioned by (part1 int) stored as parquet");
ds.repartition($"_1").write.mode("overwrite").insertInto("db1.test_part4");
spark.conf.set("spark.sql.adaptive.enabled", "true")

```

![](doc/screenshot-sparkui.png)


# Launching spark standalone cluster master + worker

## Launching master server

```
/cygdrive/apps/spark/setenv-spark-3.5.cmd

/cygdrive/apps/spark/a_start-master.sh
```

equivalent to 

```
java -cp "${SPARK_HOME}/conf\;${SPARK_HOME}/jars/*;${SPARK_HOME}" \
	-Xmx1g \
	org.apache.spark.deploy.master.Master \
	--host localhost --port 7077 --webui-port 8080
```


## Launching N x worker node(s)

```
/cygdrive/apps/spark/setenv-spark-3.5.cmd

/cygdrive/apps/spark/a_start-worker.sh
```

equivalent to

```
java -cp "${SPARK_HOME}/conf\;${SPARK_HOME}/jars/*;${SPARK_HOME}" \
	-Xmx1g \
	org.apache.spark.deploy.worker.Worker \
	--webui-port 8081 spark://localhost:7077
```


# Launching spark-shell on standalone cluster 

```
spark-shell --master spark://localhost:7077
```


# Launching java application via spark-submit


```
spark-submit --master spark://localhost:7077 \
    --class fr.an.tests.testsparkrepartition.SparkAppMain \
    "file:///c:/ arn/devPerso/test-snippets/test-spark-repartition/target/tests-spark-repartition-0.0.1-SNAPSHOT.jar"
```


custom script on local PC:

```
cd c:/data

source /cygdrive/c/apps/spark/setenv-spark-3.5.sh

/cygdrive/c/apps/spark/a_spark-submit.sh --master spark://localhost:7077 \
    --class fr.an.tests.testsparkrepartition.SparkAppMain \
    "file:///c:/arn/devPerso/test-snippets/test-spark-repartition/target/tests-spark-repartition-0.0.1-SNAPSHOT.jar"

```

equivalent to low-level JVM command:

```
export JVM_OPENS=--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-opens=java.base/java.net=ALL-UNNAMED \
--add-opens=java.base/java.nio=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/sun.nio.cs=ALL-UNNAMED \
--add-opens=java.base/sun.security.action=ALL-UNNAMED \
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED \
--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED


java ${JVM_OPENS} -cp "${SPARK_HOME}/conf\;${SPARK_HOME}/jars/*;${SPARK_HOME}" \
	org.apache.spark.deploy.SparkSubmit \
	--master spark://localhost:7077 \
    --class fr.an.tests.testsparkrepartition.SparkAppMain \
    "file:///c:/ arn/devPerso/test-snippets/test-spark-repartition/target/tests-spark-repartition-0.0.1-SNAPSHOT.jar"

```

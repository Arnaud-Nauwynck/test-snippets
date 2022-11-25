

echo "source for env var JAVA_HOME"
. ~/setenv-jdk8.sh  

echo "source for env var HADOOP_HOME, HADOOP_CONF"
. /cygdrive/c/arn/hadoop/setenv-hadoop-nohdfs.sh

echo "source for env var SPARK_HOME"
. /cygdrive/c/arn/hadoop/setenv-spark-3.sh


echo 
echo "..JAVA_HOME: ${JAVA_HOME}"

echo 
echo ".. which java: "
which java
java -version

echo 
echo ".. HADOOP_HOME: ${HADOOP_HOME}"
echo ".. HADOOP_CONF_DIR: ${HADOOP_CONF_DIR}"
echo ".. HADOOP_OPS: ${HADOOP_OPS}"

echo 
echo "..SPARK_HOME: ${SPARK_HOME}"
echo ".. which spark-submit: "
which spark-submit


echo 
echo "PATH: ${PATH}"

mkdir -p master; mkdir -p workers 
 
# for Master
cd master; ${SPARK_HOME}/sbin/start-master.sh

# for Worker
cd worker1; ${SPARK_HOME}/sbin/start-worker.sh

# for Worker
cd worker2; ${SPARK_HOME}/sbin/start-worker.sh

# for Workers
cd workers; SPARK_WORKER_INSTANCES=3 ${SPARK_HOME}/sbin/start-worker.sh



# Cygwin - Start Master => 
$ ${SPARK_HOME}/sbin/start-master.sh
starting org.apache.spark.deploy.master.Master, logging to C:/apps/hadoop/spark-3.1.1/logs/spark-arnaud-org.apache.spark.deploy.master.Master-1-DESKTOP-2EGCC8R.out
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
ps: unknown option -- o
Try `ps --help' for more information.
failed to launch: nice -n 0 C:/apps/hadoop/spark-3.1.1/bin/spark-class org.apache.spark.deploy.master.Master --host DESKTOP-2EGCC8R --port 7077 --webui-port 8080
full log in C:/apps/hadoop/spark-3.1.1/logs/spark-arnaud-org.apache.spark.deploy.master.Master-1-DESKTOP-2EGCC8R.out

.. in log:
C:/apps/hadoop/spark-3.1.1/bin/spark-class: line 96: CMD: bad array subscript

# internally... ${SPARK_HOME}/sbin/spark-daemon.sh start org.apache.spark.deploy.master.Master 1 --host DESKTOP-2EGCC8R --port 7077 --webui-port 8080

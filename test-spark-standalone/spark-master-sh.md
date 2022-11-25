

${SPARK_HOME}/sbin/spark-master.sh

=> call
CLASS="org.apache.spark.deploy.master.Master"
SPARK_MASTER_HOST=$( hostname )
SPARK_MASTER_PORT=7077
SPARK_MASTER_WEBUI_PORT=8080

"${SPARK_HOME}/sbin/spark-daemon.sh" start $CLASS 1 \
  --host $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT
  
=> call
"${SPARK_HOME}/bin/spark-class" $CLASS 1 \
 	--host $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT
  
  